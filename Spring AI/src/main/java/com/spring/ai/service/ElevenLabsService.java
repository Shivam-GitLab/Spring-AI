package com.spring.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class ElevenLabsService {

    private final RestClient restClient;

    @Value("${elevenlabs.api-key}")
    private String apiKey;

    @Value("${elevenlabs.voice-id}")
    private String voiceId;

    public ElevenLabsService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    // 🎤 Audio -> Text
    public String speechToText(byte[] audioBytes, String filename) {

        ByteArrayResource audioResource = new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", audioResource);
        body.add("model_id", "scribe_v2");

        return restClient.post()
                .uri("https://api.elevenlabs.io/v1/speech-to-text")
                .header("xi-api-key", apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    // 📝 Text -> Audio
    public byte[] textToSpeechs(String text) {

        String requestBody = """
                {
                    "text": "%s",
                    "model_id": "eleven_multilingual_v2"
                }
                """.formatted(text.replace("\"", "\\\""));

        return restClient.post()
                .uri("https://api.elevenlabs.io/v1/text-to-speech/" + voiceId)
                .header("xi-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.parseMediaType("audio/mpeg"))
                .body(requestBody)
                .retrieve()
                .body(byte[].class);
    }

    public byte[] textToSpeech(String text) {

        String requestBody = """
            {
                "text": "%s",
                "model_id": "eleven_multilingual_v2"
            }
            """.formatted(text.replace("\"", "\\\""));

        return restClient.post()
                .uri("https://api.elevenlabs.io/v1/text-to-speech/"
                        + voiceId + "?output_format=pcm_16000")
                .header("xi-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .body(requestBody)
                .retrieve()
                .body(byte[].class);
    }
}