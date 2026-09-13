package com.spring.ai.client;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class JarvisVoiceClient {

    private static final String SERVER_URL =
            "http://localhost:8080/api/ai/voice";

    // Microphone format
    private static final AudioFormat RECORD_FORMAT =
            new AudioFormat(
                    16000, // sample rate
                    16,    // bits
                    1,     // mono
                    true,  // signed
                    false  // little endian
            );

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("          🤖 JARVIS");
        System.out.println("=================================");
        System.out.println();

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(System.in)
                );

        try {

            while (true) {

                // START
                System.out.println("Press ENTER to start speaking...");
                reader.readLine();

                System.out.println();
                System.out.println("🎤 Listening...");
                System.out.println("Speak now.");
                System.out.println("Press ENTER when you are finished.");
                System.out.println();

                // RECORD
                byte[] audio = recordVoice(reader);

                if (audio.length == 0) {
                    System.out.println("❌ No audio recorded.");
                    continue;
                }

                System.out.println("🧠 Sending to JARVIS...");

                // SEND TO SPRING BOOT
                byte[] responseAudio =
                        sendToJarvis(audio);

                System.out.println("🔊 JARVIS is speaking...");

                // PLAY RESPONSE
                playAudio(responseAudio);

                System.out.println();
                System.out.println("✅ Done.");
                System.out.println();

            }

        } catch (Exception e) {

            System.out.println();
            System.out.println("❌ JARVIS ERROR");
            e.printStackTrace();
        }
    }

    // =========================================================
    // MICROPHONE RECORDING
    // =========================================================

    private static byte[] recordVoice(
            BufferedReader reader) throws Exception {

        TargetDataLine microphone =
                AudioSystem.getTargetDataLine(
                        RECORD_FORMAT
                );

        microphone.open(RECORD_FORMAT);

        microphone.start();

        ByteArrayOutputStream audioData =
                new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];

        // Recording thread
        Thread recordingThread = new Thread(() -> {

            try {

                while (microphone.isOpen()) {

                    int bytesRead =
                            microphone.read(
                                    buffer,
                                    0,
                                    buffer.length
                            );

                    if (bytesRead > 0) {

                        audioData.write(
                                buffer,
                                0,
                                bytesRead
                        );
                    }
                }

            } catch (Exception ignored) {
            }
        });

        recordingThread.start();

        // Wait for ENTER
        reader.readLine();

        // STOP MICROPHONE
        microphone.stop();
        microphone.close();

        recordingThread.join();

        System.out.println("🎤 Recording stopped.");

        // PCM -> WAV
        return createWavFile(
                audioData.toByteArray()
        );
    }

    // =========================================================
    // CREATE WAV
    // =========================================================

    private static byte[] createWavFile(
            byte[] pcmData) throws Exception {

        ByteArrayInputStream input =
                new ByteArrayInputStream(
                        pcmData
                );

        AudioInputStream audioStream =
                new AudioInputStream(
                        input,
                        RECORD_FORMAT,
                        pcmData.length /
                                RECORD_FORMAT.getFrameSize()
                );

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        AudioSystem.write(
                audioStream,
                AudioFileFormat.Type.WAVE,
                output
        );

        audioStream.close();

        return output.toByteArray();
    }

    // =========================================================
    // SEND AUDIO TO SPRING BOOT
    // =========================================================

    private static byte[] sendToJarvis(
            byte[] wav) throws Exception {

        String boundary =
                "----JavaJarvisBoundary";

        ByteArrayOutputStream body =
                new ByteArrayOutputStream();

        // Boundary
        body.write(
                ("--" + boundary + "\r\n")
                        .getBytes(
                                StandardCharsets.UTF_8
                        )
        );

        // File information
        body.write(
                ("Content-Disposition: form-data; " +
                        "name=\"audio\"; " +
                        "filename=\"voice.wav\"\r\n")
                        .getBytes(
                                StandardCharsets.UTF_8
                        )
        );

        body.write(
                "Content-Type: audio/wav\r\n"
                        .getBytes(
                                StandardCharsets.UTF_8
                        )
        );

        body.write(
                "\r\n"
                        .getBytes(
                                StandardCharsets.UTF_8
                        )
        );

        // Audio
        body.write(wav);

        // End boundary
        body.write(
                ("\r\n--" + boundary + "--\r\n")
                        .getBytes(
                                StandardCharsets.UTF_8
                        )
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        SERVER_URL
                                )
                        )
                        .header(
                                "Content-Type",
                                "multipart/form-data; boundary="
                                        + boundary
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofByteArray(
                                                body.toByteArray()
                                        )
                        )
                        .build();

        HttpClient client =
                HttpClient.newBuilder()
                        .build();

        HttpResponse<byte[]> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers
                                .ofByteArray()
                );

        // Check response
        if (response.statusCode() != 200) {

            System.out.println(
                    "❌ Server returned HTTP "
                            + response.statusCode()
            );

            System.out.println(
                    new String(
                            response.body(),
                            StandardCharsets.UTF_8
                    )
            );

            throw new RuntimeException(
                    "JARVIS API request failed"
            );
        }

        return response.body();
    }

    // =========================================================
    // PLAY AUDIO FROM SPEAKER
    // =========================================================

    private static void playAudio(
            byte[] pcm) throws Exception {

        AudioFormat speakerFormat =
                new AudioFormat(
                        16000,
                        16,
                        1,
                        true,
                        false
                );

        SourceDataLine speaker =
                AudioSystem.getSourceDataLine(
                        speakerFormat
                );

        speaker.open(speakerFormat);

        speaker.start();

        speaker.write(
                pcm,
                0,
                pcm.length
        );

        speaker.drain();

        speaker.stop();


        speaker.close();
    }
}