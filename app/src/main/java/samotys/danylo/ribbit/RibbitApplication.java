package samotys.danylo.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

import samotys.danylo.ribbit.ui.MainActivity;
import samotys.danylo.ribbit.utils.ParseConstants;

/**
 * Created by Danylo on 16.03.15.
 */
public class RibbitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        Parse.initialize(this,
                "gBjGhfrIxYiR7Jp3aqMGuEEtaethxah43TuJpIit",
                "21veNHK6AiAfwDyPLKOHztiaV5vcbOlQP3fc8Zgl");

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
