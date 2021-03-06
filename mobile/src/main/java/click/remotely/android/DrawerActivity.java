package click.remotely.android;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import click.remotely.android.fragments.BrowserControlsFragment;
import click.remotely.android.fragments.ConsoleFragment;
import click.remotely.android.fragments.GamepadFragment;
import click.remotely.android.fragments.KeyboardFragment;
import click.remotely.android.fragments.MediaPlayerControlsFragment;
import click.remotely.android.fragments.MouseFragment;
import click.remotely.android.fragments.NumpadFragment;
import click.remotely.android.fragments.PowerControlsFragment;
import click.remotely.android.fragments.ShortcutsFragment;
import click.remotely.android.fragments.SlideShowControlsFragment;
import click.remotely.android.fragments.TouchpadFragment;
import click.remotely.android.fragments.TouchscreenFragment;

/**
 * Created by michzio on 12/07/2017.
 */

public class DrawerActivity extends AppCompatActivity {

    private static final String TAG = DrawerActivity.class.getName();

    private DrawerLayout mDrawerLayout = null;
    private NavigationView mNavigationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open,  R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.start_nav_view);
        mNavigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().addOnBackStackChangedListener(backStackChangedListener);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Handle action bar item clicks here.
        switch ( menuItem.getItemId() ) {

            case R.id.action_remote_devices:
                Intent remoteDevicesIntent = new Intent(this, RemoteDevicesActivity.class);
                startActivity(remoteDevicesIntent);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_console:
                startConsoleFragment();
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.d(TAG, item.getTitle() + " item selected.");

            selectItem(item);
            return false;
        }
    };

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(MenuItem item) {

        int orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;

        Fragment fragment = null;
        String fragmentName = null;

        switch(item.getItemId()) {
            case R.id.nav_touchpad:
                Log.d(TAG, "Navigation Menu - touchpad item selected");
                fragment = new TouchpadFragment();
                fragmentName = getString(R.string.action_touchpad);
                break;
            case R.id.nav_mouse:
                Log.d(TAG, "Navigation Menu - mouse item selected");
                fragment = new MouseFragment();
                orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
                fragmentName = getString(R.string.action_mouse);
                break;
            case R.id.nav_keyboard:
                Log.d(TAG, "Navigation Menu - keyboard item selected");
                fragment = new KeyboardFragment();
                orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                fragmentName = getString(R.string.action_keyboard);
                break;
            case R.id.nav_numpad:
                Log.d(TAG, "Navigation Menu - numpad item selected");
                fragment = new NumpadFragment();
                fragmentName = getString(R.string.action_numpad);
                break;
            case R.id.nav_gamepad:
                Log.d(TAG, "Navigation Menu - gamepad item selected");
                fragment = new GamepadFragment();
                orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                fragmentName = getString(R.string.action_gamepad);
                break;
            case R.id.nav_touchscreen:
                Log.d(TAG, "Navigation Menu - touchscreen item selected");
                fragment = new TouchscreenFragment();
                orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                fragmentName = getString(R.string.action_touchscreen);
                break;
            case R.id.nav_media_player:
                Log.d(TAG, "Navigation Menu - media player item selected");
                fragment = new MediaPlayerControlsFragment();
                fragmentName = getString(R.string.action_media_player);
                break;
            case R.id.nav_slideshow:
                Log.d(TAG, "Navigation Menu - slide show item selected");
                fragment = new SlideShowControlsFragment();
                fragmentName = getString(R.string.action_slide_show);
                break;
            case R.id.nav_browser:
                Log.d(TAG, "Navigation Menu - browser item selected");
                fragment = new BrowserControlsFragment();
                fragmentName = getString(R.string.action_browser);
                break;
            case R.id.nav_power_controls:
                Log.d(TAG, "Navigation Menu - power controls item selected");
                fragment = new PowerControlsFragment();
                fragmentName = getString(R.string.action_power_controls);
                break;
            case R.id.nav_shortcuts:
                Log.d(TAG, "Navigation Menu - shortcuts item selected");
                fragment = new ShortcutsFragment();
                fragmentName = getString(R.string.action_shortcuts);
                break;
            default:
                Log.d(TAG, "Navigation Menu - item id: " + item.getItemId());
                break;
        }

        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(fragmentName);
            fragmentTransaction.commit();

            mNavigationView.setCheckedItem(item.getItemId());
            mDrawerLayout.closeDrawer(Gravity.LEFT);

            setTitle(item.getTitle());

            setRequestedOrientation(orientation);
        }
    }

    protected void navigationSelectItem(int itemId) {
        mNavigationView.setCheckedItem(itemId);
        mNavigationView.getMenu().performIdentifierAction(itemId, 0);
    }

    private void startConsoleFragment() {
        Fragment fragment = new ConsoleFragment();
        FragmentManager fragmetManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmetManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack("Console");
        fragmentTransaction.commit();
        navigationViewUncheckItems();

        setTitle(R.string.action_console);
    }

    private void navigationViewUncheckItems() {
        int size = mNavigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            MenuItem menuItem = mNavigationView.getMenu().getItem(i);
            int sub_size = menuItem.getSubMenu().size();
            for( int j=0; j < sub_size; j++){
                mNavigationView.getMenu().getItem(i).getSubMenu().getItem(j).setChecked(false);
            }
        }
    }

    private void navigationViewCheckItemByTitle(String title) {
        int size = mNavigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            MenuItem menuItem = mNavigationView.getMenu().getItem(i);
            int sub_size = menuItem.getSubMenu().size();
            for( int j=0; j < sub_size; j++){
                String currentTitle = (String) mNavigationView.getMenu().getItem(i).getSubMenu().getItem(j).getTitle();
                if(currentTitle.equals(title)) {
                    mNavigationView.getMenu().getItem(i).getSubMenu().getItem(j).setChecked(true);
                    return;
                }
            }
        }
    }

    FragmentManager.OnBackStackChangedListener backStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {

            int lastBackStackEntryCount = getSupportFragmentManager().getBackStackEntryCount()-1;
            if(lastBackStackEntryCount >= 0) {
                FragmentManager.BackStackEntry lastBackStackEntry = getSupportFragmentManager().getBackStackEntryAt(lastBackStackEntryCount);
                setTitle(lastBackStackEntry.getName());
                navigationViewCheckItemByTitle(lastBackStackEntry.getName());
            } else {
                setTitle(getString(R.string.app_name));
            }
        }
    };
}
