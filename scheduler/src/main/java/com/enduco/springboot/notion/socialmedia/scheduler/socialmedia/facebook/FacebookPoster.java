package com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.facebook;

import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost;
import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPoster;
import org.springframework.stereotype.Component;

@Component("facebook")
public class FacebookPoster implements SocialMediaPoster {
    @Override
    public boolean postToSocialMedia(SocialMediaPost post) {
        System.out.println("Posting to facebook");
        return true;
    }
}
