package click.remotely.android;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import click.remotely.common.SessionHandler;

/**
 * Created by michzio on 29/06/2017.
 */

public class RemotelyClickApplication extends Application {

    private static RemotelyClickApplication sharedInstance;

    private SessionHandler sessionHandler;

    public static RemotelyClickApplication getSharedInstance() {
        return sharedInstance;
    }

    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedInstance = this;
        sharedInstance.init();
    }

    protected void init() {
        // do all initialization of shared application instance here
        sessionHandler = new SessionHandler( getSharedPreferences("APPLICATION_PREFERENCES", Context.MODE_PRIVATE) );
        if( sessionHandler.isApplicationFirstRun() ) {
            Toast.makeText(this, "Application is run first time!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Application is run again.", Toast.LENGTH_LONG).show();
        }
    }
}
