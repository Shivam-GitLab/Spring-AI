package com.spring.ai.api.controlller;

import com.spring.ai.service.ElevenLabsService;
import com.spring.ai.service.UserService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
public class VoiceController {

    private final ChatClient chatClient;
    private final ElevenLabsService elevenLabsService;
    private final UserService userService;

    public VoiceController(
            ChatClient.Builder builder,
            ElevenLabsService elevenLabsService,
            UserService userService) {

        this.chatClient = builder.build();
        this.elevenLabsService = elevenLabsService;
        this.userService = userService;
    }

    @PostMapping(
            value = "/voice",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "audio/mpeg"
    )
    public ResponseEntity<byte[]> voice(
            @RequestPart("audio") MultipartFile audio) throws Exception {

        // Voice -> Text
        String transcription = elevenLabsService.speechToText(
                audio.getBytes(),
                audio.getOriginalFilename()
        );

        // Text -> AI + Tools
        String answer = chatClient
                .prompt()
                .user(transcription)
                .tools(userService)
                .call()
                .content();

        // Text -> Voice
        byte[] responseAudio =
                elevenLabsService.textToSpeech(answer);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(responseAudio);
    }
}