package com.enduco.springboot.notion.socialmedia.scheduler.socialmedia;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import notion.api.v1.model.pages.Page;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class SocialMediaPost {
    final private String name;
    final private String status;
    final private String channel;
    final private String postAt;
    final private String content;
    final private Page page;

    public enum ChannelType {
        DISCORD,
        FACEBOOK,
        EMPTY,
        TWITTER
    }

    public ChannelType getChannelType() {
        return switch (channel.toLowerCase()) {
            case "discord" -> ChannelType.DISCORD;
            case "facebook" -> ChannelType.FACEBOOK;
            case "twitter" -> ChannelType.TWITTER;
            default -> ChannelType.EMPTY;
        };
    }
}
