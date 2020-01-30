package com.intcore.snapcar.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.core.chat.ChatModel;
import com.intcore.snapcar.core.scope.ApplicationScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.di.service.ServiceModule;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.util.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import javax.inject.Inject;

import timber.log.Timber;

@ApplicationScope
public class FCMService extends FirebaseMessagingService {

    public static String NOTIFICATION_TAG = "notificationTag";
    public static String HOME_TAG = "homeTag";

    @Inject
    UserManager userSessionManager;

    @Override
    public void onCreate() {
        SnapCarApplication.getComponent(this)
                .plus(new ServiceModule(this))
                .inject(this);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (userSessionManager.sessionManager().isSessionActive()) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                Timber.tag("manarDebug").v(json.toString());
                if (json.getString("type").contentEquals("11")) {
                    if (ChatModel.isChatAlive != json.optInt("room_id")) {
                        sendPushNotification(json);
                    }
                } else {
                    sendPushNotification(json);
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void sendPushNotification(JSONObject content) throws JSONException {
        Intent intent;
        intent = new Intent(this, HostActivity.class);
        if (content.getString("type").contentEquals("11")) {
            intent.putExtra(NOTIFICATION_TAG, true);
        } else {
            intent.putExtra(HOME_TAG, true);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String CHANNEL_ID = getString(R.string.default_notification_channel_id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String title = "سناب كارز";
        if (LocaleUtil.getLanguage(this).contentEquals("en")) {
            title = "SnapCars";
        }
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_login)
                        .setContentTitle(title)
                        .setContentText(content.getString("body"))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(content.getString("body")))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setColor(getResources().getColor(R.color.md_white_1000));
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Sayes",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(channel);
        }
        Random r = new Random();
        int id = (r.nextInt(100) + 100);
        assert mNotificationManager != null;
        mNotificationManager.notify(id, notificationBuilder.build());
    }
}
