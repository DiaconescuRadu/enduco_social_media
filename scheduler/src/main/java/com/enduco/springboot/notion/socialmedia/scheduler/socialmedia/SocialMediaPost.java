package com.enduco.springboot.notion.socialmedia.scheduler.socialmedia;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    public boolean isPostingDateClose(SocialMediaPost post) {
        return true;
    }

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
