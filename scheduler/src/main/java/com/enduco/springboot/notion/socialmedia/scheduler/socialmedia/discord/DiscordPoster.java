package com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.discord;

import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost;
import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPoster;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("discord")
public class DiscordPoster implements SocialMediaPoster {

    @Value("${com.enduco.scheduler.discord.token}")
    private String token;

    @Value("${com.enduco.scheduler.discord.channel-id}")
    private String channelId;

    @Override
    public boolean postToSocialMedia(SocialMediaPost post) {
        GatewayDiscordClient client = DiscordClientBuilder.create(token)
                .build()
                .login()
                .block();

        client.getChannelById(Snowflake.of(channelId))
                .flatMap(channel -> ((TextChannel) channel).createMessage(post.getContent()))
                .block();

        client.logout().block();
        return true;
    }
}
