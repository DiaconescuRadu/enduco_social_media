package com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.discord;

import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost;
import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPoster;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import org.springframework.stereotype.Component;

@Component("discord")
public class DiscordPoster implements SocialMediaPoster {

    private static final String DISCORD_TOKEN = "MTA5MTMyODc1NDUwMjA3NDM4OQ.GF59mV.kn-VHerG8vVd_AhUJ5LeVyNV3uRMgZBWWyjWdI";
    private static final long CHANNEL_ID = 1091327560069156876L;

    @Override
    public boolean postToSocialMedia(SocialMediaPost post) {
        GatewayDiscordClient client = DiscordClientBuilder.create(DISCORD_TOKEN)
                .build()
                .login()
                .block();

        client.getChannelById(Snowflake.of(CHANNEL_ID))
                .flatMap(channel -> ((TextChannel) channel).createMessage(post.getContent()))
                .block();

        client.logout().block();
        return true;
    }
}
