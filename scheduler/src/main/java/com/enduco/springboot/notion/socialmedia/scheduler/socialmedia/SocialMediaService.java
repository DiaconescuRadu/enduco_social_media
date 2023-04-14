package com.enduco.springboot.notion.socialmedia.scheduler.socialmedia;

import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost.ChannelType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost.ChannelType.*;
import static com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost.ChannelType.EMPTY;

@Service
public class SocialMediaService {

    @Autowired
    @Qualifier("discord")
    SocialMediaPoster discordPoster;

    @Autowired
    @Qualifier("facebook")
    SocialMediaPoster facebookPoster;

    public boolean post(SocialMediaPost post) {
        return switch (post.getChannelType()) {
            case DISCORD -> discordPoster.postToSocialMedia(post);
            case FACEBOOK -> facebookPoster.postToSocialMedia(post);
            default -> false;
        };
    }

}
