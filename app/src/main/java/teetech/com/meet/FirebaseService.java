package teetech.com.meet;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



/**
 * Created by aKI on 02/03/2017.
 */

public class FirebaseService extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh()
    {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token", "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        storeRegIdInPref(refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(String token)
    {

    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("firebaseUserToken", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("firebaseUserToken", token);
        editor.commit();
    }
}
