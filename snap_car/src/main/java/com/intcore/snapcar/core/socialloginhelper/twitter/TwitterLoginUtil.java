package com.intcore.snapcar.core.socialloginhelper.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.intcore.snapcar.core.socialloginhelper.SocialLoginListener;
import com.intcore.snapcar.core.util.Preconditions;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

public class TwitterLoginUtil {

    final boolean shouldRequestEmail;
    private final TwitterAuthClient authClient;
    final SocialLoginListener socialMediaListener;

    TwitterLoginUtil(SocialLoginListener socialMediaListener, boolean shouldGetEmail) {
        this.socialMediaListener = socialMediaListener;
        this.shouldRequestEmail = shouldGetEmail;
        this.authClient = new TwitterAuthClient();
    }

    public void login(Activity activity) {
        Preconditions.checkNonNull(activity);
        authClient.authorize(activity, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession twitterSession = result.data;
                if (shouldRequestEmail) {
                    requestEmail(twitterSession);
                } else {
                    socialMediaListener.onLoggedIn(String.valueOf(twitterSession.getUserId()), twitterSession.getUserName(), "", null);
                }
            }

            @Override
            public void failure(TwitterException exception) {
                socialMediaListener.onError(exception);
            }
        });
    }

    public boolean isSessionActive() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession() != null;
    }

    public void logout() {
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        socialMediaListener.onLoggedOut();
    }

    void requestEmail(TwitterSession twitterSession) {
        authClient.requestEmail(twitterSession, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                socialMediaListener.onLoggedIn(String.valueOf(twitterSession.getUserId()), twitterSession.getUserName(), "", result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                socialMediaListener.onError(exception);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        authClient.onActivityResult(requestCode, resultCode, data);
    }

    public static class Builder {

        private SocialLoginListener socialMediaListener;
        private boolean shouldRequestEmail;

        public Builder(Context context, String consumerKey, String consumerSecret) {
            TwitterConfig twitterConfig = new TwitterConfig.Builder(context)
                    .twitterAuthConfig(new TwitterAuthConfig(consumerKey, consumerSecret))
                    .build();
            Twitter.initialize(twitterConfig);
        }

        public Builder registerCallback(SocialLoginListener socialMediaListener) {
            this.socialMediaListener = socialMediaListener;
            return this;
        }

        public Builder requestEmail() {
            shouldRequestEmail = true;
            return this;
        }

        public TwitterLoginUtil build() {
            return new TwitterLoginUtil(socialMediaListener, shouldRequestEmail);
        }
    }
}
