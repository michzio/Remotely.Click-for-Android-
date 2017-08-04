package click.remotely.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import click.remotely.android.R;

/**
 * Created by michzio on 15/07/2017.
 */

public class BrowserControlsFragment extends Fragment {

    private static final String TAG = BrowserControlsFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

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

        bindBrowserButtonsToClickListeners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private void bindBrowserButtonsToClickListeners() {

        // bind floating action buttons
        getActivity().findViewById(R.id.browser_controls_fab_action_keyboard)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSoftwareKeyboard();
                    }
                });

        getActivity().findViewById(R.id.browser_controls_fab_action_mouse)
                .setOnClickListener(new View.OnClickListener() {

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

    private void showSoftwareKeyboard() {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }

    private void browserNewTabClicked() {

        Toast.makeText(getActivity(), "Browser New Tab", Toast.LENGTH_SHORT).show();
    }

    private void browserPreviousTabClicked() {

        Toast.makeText(getActivity(), "Browser Previous Tab", Toast.LENGTH_SHORT).show();
    }

    private void browserNextTabClicked() {

        Toast.makeText(getActivity(), "Browser Next Tab", Toast.LENGTH_SHORT).show();
    }

    private void browserCloseTabClicked() {

        Toast.makeText(getActivity(), "Browser Close Tab", Toast.LENGTH_SHORT).show();
    }

    private void browserOpenFileClicked() {

        Toast.makeText(getActivity(), "Browser Open File", Toast.LENGTH_SHORT).show();
    }

    private void browserNewPrivateWindowClicked() {

        Toast.makeText(getActivity(), "Browser New Private Window", Toast.LENGTH_SHORT).show();
    }

    private void browserReopenClosedTabClicked() {

        Toast.makeText(getActivity(), "Browser Reopen Closed Tab", Toast.LENGTH_SHORT).show();
    }

    private void browserCloseWindowClicked() {

        Toast.makeText(getActivity(), "Browser Close Window", Toast.LENGTH_SHORT).show();
    }

    private void browserShowDownloadsClicked() {

        Toast.makeText(getActivity(), "Browser Show Downloads", Toast.LENGTH_SHORT).show();
    }

    private void browserShowHistoryClicked() {

        Toast.makeText(getActivity(), "Browser Show History", Toast.LENGTH_SHORT).show();
    }

    private void browserShowSidebarClicked() {

        Toast.makeText(getActivity(), "Browser Show Sidebar", Toast.LENGTH_SHORT).show();
    }

    private void browserShowPageSourceClicked() {

        Toast.makeText(getActivity(), "Browser Show Page Source", Toast.LENGTH_SHORT).show();
    }

    private void browserHomePageClicked() {

        Toast.makeText(getActivity(), "Browser Home Page", Toast.LENGTH_SHORT).show();
    }

    private void browserReloadPageClicked() {

        Toast.makeText(getActivity(), "Browser Reload Page", Toast.LENGTH_SHORT).show();
    }

    private void browserBookmarkPageClicked() {

        Toast.makeText(getActivity(), "Browser Bookmark Page", Toast.LENGTH_SHORT).show();
    }

    private void browserEnterFullscreenClicked() {

        Toast.makeText(getActivity(), "Browser Enter Fullscreen", Toast.LENGTH_SHORT).show();
    }

    private void browserZoomOutClicked() {

        Toast.makeText(getActivity(), "Browser Zoom Out", Toast.LENGTH_SHORT).show();
    }

    private void browserZoomActualSizeClicked() {

        Toast.makeText(getActivity(), "Browser Zoom Actual Size", Toast.LENGTH_SHORT).show();
    }

    private void browserZoomInClicked() {

        Toast.makeText(getActivity(), "Browser Zoom In", Toast.LENGTH_SHORT).show();
    }

    private void browserEnterLocationClicked() {

        showSoftwareKeyboard();

        Toast.makeText(getActivity(), "Browser Enter Location", Toast.LENGTH_SHORT).show();
    }
}
