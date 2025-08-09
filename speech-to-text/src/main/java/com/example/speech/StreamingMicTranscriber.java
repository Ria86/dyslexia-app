package com.example.speech;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

public class StreamingMicTranscriber {

    private SpeechClient speechClient;
    private ClientStream<StreamingRecognizeRequest> clientStream;
    private ExecutorService micExecutor;
    private TargetDataLine mic;
    private AtomicBoolean keepGoing;
    private final StringBuilder transcript = new StringBuilder();
    private volatile boolean recording = false;

    public synchronized void startListening() throws Exception {
        if (recording) {
            throw new IllegalStateException("Already recording");
        }

        speechClient = SpeechClient.create();

        ResponseObserver<StreamingRecognizeResponse> responseObserver =
            new ResponseObserver<StreamingRecognizeResponse>() {
                @Override
                public void onStart(StreamController controller) {}

                @Override
                public void onResponse(StreamingRecognizeResponse response) {
                    for (StreamingRecognitionResult result : response.getResultsList()) {
                        for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
                            synchronized (transcript) {
                                transcript.append(alternative.getTranscript()).append(" ");
                            }
                        }
                    }
                }

                @Override
                public void onComplete() {}

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                }
            };

        clientStream = speechClient.streamingRecognizeCallable().splitCall(responseObserver);

        RecognitionConfig recConfig = RecognitionConfig.newBuilder()
            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
            .setSampleRateHertz(16000)
            .setLanguageCode("en-US")
            .build();

        StreamingRecognitionConfig config = StreamingRecognitionConfig.newBuilder()
            .setConfig(recConfig)
            .setInterimResults(true)
            .build();

        clientStream.send(StreamingRecognizeRequest.newBuilder()
            .setStreamingConfig(config)
            .build());

        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        mic = (TargetDataLine) AudioSystem.getLine(info);
        mic.open(format);
        mic.start();

        keepGoing = new AtomicBoolean(true);

        micExecutor = Executors.newSingleThreadExecutor();
        micExecutor.submit(() -> {
            byte[] buffer = new byte[4096];
            try {
                while (keepGoing.get()) {
                    int bytesRead = mic.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        clientStream.send(StreamingRecognizeRequest.newBuilder()
                            .setAudioContent(ByteString.copyFrom(buffer, 0, bytesRead))
                            .build());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Clear previous transcript
        synchronized (transcript) {
            transcript.setLength(0);
        }

        recording = true;
    }

    public synchronized String stopListeningAndGetTranscript() throws Exception {
        if (!recording) {
            throw new IllegalStateException("Not currently recording");
        }

        keepGoing.set(false);

        mic.stop();
        mic.close();

        clientStream.closeSend();

        micExecutor.shutdown();
        micExecutor.awaitTermination(2, TimeUnit.SECONDS);

        speechClient.close();

        recording = false;

        String finalTranscript;
        synchronized (transcript) {
            finalTranscript = transcript.toString().trim();
        }
        System.out.println("Recording stopped.");
        return finalTranscript;
    }

    public synchronized boolean isRecording() {
        return recording;
    }

    // Call this when your app exits to clean up resources if recording not already stopped
    public synchronized void close() {
        try {
            if (recording) {
                stopListeningAndGetTranscript();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
