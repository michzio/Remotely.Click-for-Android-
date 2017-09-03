package click.remotely.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import click.remotely.android.MainDrawerActivity;
import click.remotely.android.R;
import click.remotely.android.interfaces.RemoteControllerClientInterface;
import click.remotely.android.services.RemoteControllerClientService;
import click.remotely.inputs.BottomSheetKeyboard;

/**
 * Created by michzio on 15/07/2017.
 */

public class BrowserControlsFragment extends Fragment {

    private static final String TAG = BrowserControlsFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    private FloatingActionButton mKeyboardButton;
    private FloatingActionButton mMouseButton;
    private ViewGroup mBottomSheetKeyboard;

    public BrowserControlsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_browser, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            // Restore the activity's title here
            getActivity().setTitle(savedInstanceState.getCharSequence(ACTIVITY_TITLE_KEY));
        }

        initFloatingActionButtons();
        bindBrowserButtonsToClickListeners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private void initFloatingActionButtons() {

        mKeyboardButton = (FloatingActionButton) getActivity().findViewById(R.id.browser_controls_fab_action_keyboard);
        mMouseButton = (FloatingActionButton) getActivity().findViewById(R.id.browser_controls_fab_action_mouse);

        mMouseButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        NavigationView navigationView = (NavigationView) BrowserControlsFragment.this.getActivity().findViewById(R.id.start_nav_view);
                        navigationView.getMenu().performIdentifierAction(R.id.nav_touchpad, 0);

                        // or make new fragment transaction
                        //Fragment fragment = new TouchpadFragment();
                        //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        //fragmentTransaction.replace(R.id.content_frame, fragment);
                        //fragmentTransaction.addToBackStack("Browser Controls Show Touchpad");
                        //fragmentTransaction.commit();
                    }
        });

        mBottomSheetKeyboard = (ViewGroup) getActivity().findViewById(R.id.bottom_sheet_keyboard);

        BottomSheetKeyboard keyboard = new BottomSheetKeyboard(mBottomSheetKeyboard, (RemoteControllerClientInterface) getActivity());

        mKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show custom keyboard
                mKeyboardButton.setVisibility(View.GONE);
                mMouseButton.setVisibility(View.GONE);
                mBottomSheetKeyboard.setVisibility(View.VISIBLE);
            }
        });

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if(mBottomSheetKeyboard.getVisibility() == View.VISIBLE) {
                            mBottomSheetKeyboard.setVisibility(View.GONE);
                            mMouseButton.setVisibility(View.VISIBLE);
                            mKeyboardButton.setVisibility(View.VISIBLE);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void bindBrowserButtonsToClickListeners() {


        // bind browser control buttons
        getActivity().findViewById(R.id.browser_controls_new_tab_btn).setOnClickListener(v -> browserNewTabClicked());
        getActivity().findViewById(R.id.browser_controls_previous_tab_btn).setOnClickListener(v -> browserPreviousTabClicked());
        getActivity().findViewById(R.id.browser_controls_next_tab_btn).setOnClickListener(v -> browserNextTabClicked());
        getActivity().findViewById(R.id.browser_controls_close_tab_btn).setOnClickListener(v -> browserCloseTabClicked());
        getActivity().findViewById(R.id.browser_controls_open_file_btn).setOnClickListener(v -> browserOpenFileClicked());
        getActivity().findViewById(R.id.browser_controls_new_private_window_btn).setOnClickListener(v -> browserNewPrivateWindowClicked());
        getActivity().findViewById(R.id.browser_controls_reopen_closed_tab_btn).setOnClickListener(v -> browserReopenClosedTabClicked());
        getActivity().findViewById(R.id.browser_controls_close_window_btn).setOnClickListener(v -> browserCloseWindowClicked());
        getActivity().findViewById(R.id.browser_controls_show_downloads_btn).setOnClickListener(v -> browserShowDownloadsClicked());
        getActivity().findViewById(R.id.browser_controls_show_history_btn).setOnClickListener(v -> browserShowHistoryClicked());
        getActivity().findViewById(R.id.browser_controls_show_sidebar_btn).setOnClickListener(v -> browserShowSidebarClicked());
        getActivity().findViewById(R.id.browser_controls_show_page_source_btn).setOnClickListener(v -> browserShowPageSourceClicked());
        getActivity().findViewById(R.id.browser_controls_home_page_btn).setOnClickListener(v -> browserHomePageClicked());
        getActivity().findViewById(R.id.browser_controls_reload_page_btn).setOnClickListener(v -> browserReloadPageClicked());
        getActivity().findViewById(R.id.browser_controls_bookmark_page_btn).setOnClickListener(v -> browserBookmarkPageClicked());
        getActivity().findViewById(R.id.browser_controls_enter_fullscreen_btn).setOnClickListener(v -> browserEnterFullscreenClicked());
        getActivity().findViewById(R.id.browser_controls_zoom_out_btn).setOnClickListener(v -> browserZoomOutClicked());
        getActivity().findViewById(R.id.browser_controls_zoom_actual_size_btn).setOnClickListener(v -> browserZoomActualSizeClicked());
        getActivity().findViewById(R.id.browser_controls_zoom_in_btn).setOnClickListener(v -> browserZoomInClicked());
        getActivity().findViewById(R.id.browser_controls_enter_location_btn).setOnClickListener(v -> browserEnterLocationClicked());
    }

    private void browserNewTabClicked() {

        Log.d(TAG, "Browser New Tab");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserNewTab();
        }
    }

    private void browserPreviousTabClicked() {

        Log.d(TAG, "Browser Previous Tab");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserPreviousTab();
        }
    }

    private void browserNextTabClicked() {

        Log.d(TAG, "Browser Next Tab");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserNextTab();
        }
    }

    private void browserCloseTabClicked() {

        Log.d(TAG, "Browser Close Tab");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserCloseTab();
        }
    }

    private void browserOpenFileClicked() {

        Log.d(TAG, "Browser Open File");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserOpenFile();
        }
    }

    private void browserNewPrivateWindowClicked() {

       Log.d(TAG, "Browser New Private Window");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserNewPrivateWindow();
        }
    }

    private void browserReopenClosedTabClicked() {

        Log.d(TAG, "Browser Reopen Closed Tab");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserReopenClosedTab();
        }
    }

    private void browserCloseWindowClicked() {

        Log.d(TAG, "Browser Close Window");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserCloseWindow();
        }
    }

    private void browserShowDownloadsClicked() {

        Log.d(TAG, "Browser Show Downloads");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserShowDownloads();
        }
    }

    private void browserShowHistoryClicked() {

        Log.d(TAG, "Browser Show History");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserShowHistory();
        }
    }

    private void browserShowSidebarClicked() {

        Log.d(TAG, "Browser Show Sidebar");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserShowSidebar();
        }
    }

    private void browserShowPageSourceClicked() {

        Log.d(TAG, "Browser Show Page Source");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserShowPageSource();
        }
    }

    private void browserHomePageClicked() {

        Log.d(TAG, "Browser Home Page");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserHomePage();
        }
    }

    private void browserReloadPageClicked() {

        Log.d(TAG, "Browser Reload Page");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserReloadPage();
        }
    }

    private void browserBookmarkPageClicked() {

        Log.d( TAG, "Browser Bookmark Page");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserBookmarkPage();
        }
    }

    private void browserEnterFullscreenClicked() {

        Log.d( TAG, "Browser Enter Fullscreen");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserEnterFullscreen();
        }
    }

    private void browserZoomOutClicked() {

        Log.d(TAG, "Browser Zoom Out");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserZoomOut();
        }
    }

    private void browserZoomActualSizeClicked() {

        Log.d(TAG, "Browser Zoom Actual Size");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserZoomActualSize();
        }
    }

    private void browserZoomInClicked() {

        Log.d(TAG, "Browser Zoom In");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserZoomIn();
        }
    }

    private void browserEnterLocationClicked() {

        // show custom keyboard
        mKeyboardButton.setVisibility(View.GONE);
        mMouseButton.setVisibility(View.GONE);
        mBottomSheetKeyboard.setVisibility(View.VISIBLE);

        Log.d(TAG, "Browser Enter Location");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.browserEnterLocation();
        }
    }


}
