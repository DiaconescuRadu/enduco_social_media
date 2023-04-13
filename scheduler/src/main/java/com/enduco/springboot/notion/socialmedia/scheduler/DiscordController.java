package com.enduco.springboot.notion.socialmedia.scheduler;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.common.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscordController {

    @Autowired
    private NotionService notionService;

    private static final String DISCORD_TOKEN = "MTA5MTMyODc1NDUwMjA3NDM4OQ.GF59mV.kn-VHerG8vVd_AhUJ5LeVyNV3uRMgZBWWyjWdI";
    private static final long CHANNEL_ID = 1091327560069156876L;

    @PostMapping("/send")
    public String sendMessage(@RequestParam(value = "message", required = true) String message) {
        GatewayDiscordClient client = DiscordClientBuilder.create(DISCORD_TOKEN)
                .build()
                .login()
                .block();

        client.getChannelById(Snowflake.of(CHANNEL_ID))
                .flatMap(channel -> ((TextChannel) channel).createMessage(message))
                .block();

        client.logout().block();

        return "Message sent!";
    }

    @GetMapping("/databases")
    public ResponseEntity<Object> listDatabases() {
        Object databases = notionService.listDatabases();
        return ResponseEntity.ok(databases);
    }

    @GetMapping("/databases/{databaseId}/contents")
    public ResponseEntity<Object> getDatabaseContents(String databaseId) {
        Object databaseContents = notionService.getDatabaseContents(databaseId);
        return ResponseEntity.ok(databaseContents);
    }
}