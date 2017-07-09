package teetech.com.meet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    Map<String, String> meetingData;
    public MyFirebaseMessagingService()
    {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        if (remoteMessage.getData().size() > 0)
        {
            Map<String, String> map;
            map = remoteMessage.getData();
            storeRegIdInPref(map);
            Log.e(TAG, "Message data payload: Lon: " + map.get("Lon")+" Lat:"+map.get("Lat"));
            sendNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null)
        {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //sendNotification(remoteMessage.getNotification().getBody());
        }


    }

    private void sendNotification(String messageBody)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cast_ic_expanded_controller_mute)
                .setContentTitle("Meeting Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void storeRegIdInPref(Map<String,String> map)
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("meeting_data", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Lat", map.get("Lat"));
        editor.putString("Lon",map.get("Lon"));
        editor.putString("date",map.get("date"));
        editor.putString("details",map.get("meetingDetails"));
        editor.putString("title",map.get("meetingTitle"));
        editor.putString("time",map.get("time"));
        editor.putString("venue",map.get("venue"));
        editor.commit();
    }
}
