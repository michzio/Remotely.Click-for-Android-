package click.remotely.common;

import android.content.SharedPreferences;

/**
 * Created by michzio on 29/06/2017.
 */

public class SessionHandler {

    private static final String PREFS_KEY_IS_FIRST_RUN = "PREFS_KEY_IS_FIRST_RUN";

    private SharedPreferences preferences;

    public SessionHandler(SharedPreferences prefs) {
        this.preferences = prefs;
    }

    // store some global application data in preferences.

    public boolean isApplicationFirstRun() {

        if(preferences.getBoolean(PREFS_KEY_IS_FIRST_RUN, true)) {

            SharedPreferences.Editor prefsEditor = preferences.edit();
            prefsEditor.putBoolean(PREFS_KEY_IS_FIRST_RUN, false);
            prefsEditor.apply();
            return true;

        }
        return false;
    }
}
