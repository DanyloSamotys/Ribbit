package samotys.danylo.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Danylo on 16.03.15.
 */
public class RibbitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "gBjGhfrIxYiR7Jp3aqMGuEEtaethxah43TuJpIit", "21veNHK6AiAfwDyPLKOHztiaV5vcbOlQP3fc8Zgl");
    }
}
