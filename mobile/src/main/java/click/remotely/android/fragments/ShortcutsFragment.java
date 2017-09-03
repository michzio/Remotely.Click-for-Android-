package click.remotely.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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

public class ShortcutsFragment extends Fragment {

    private static final String TAG = ShortcutsFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    private FloatingActionButton mKeyboardButton;
    private FloatingActionButton mMouseButton;
    private ViewGroup mBottomSheetKeyboard;

    public ShortcutsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_shortcuts, container, false);

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
        bindShortcutsButtonsToClickListeners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private void initFloatingActionButtons() {

        mKeyboardButton = (FloatingActionButton) getActivity().findViewById(R.id.shortcuts_fab_action_keyboard);
        mMouseButton = (FloatingActionButton) getActivity().findViewById(R.id.shortcuts_fab_action_mouse);

        mMouseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NavigationView navigationView = (NavigationView) ShortcutsFragment.this.getActivity().findViewById(R.id.start_nav_view);
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

    private void bindShortcutsButtonsToClickListeners() {

        getActivity().findViewById(R.id.shortcuts_select_all_btn).setOnClickListener(v -> shortcutSelectAllClicked());
        getActivity().findViewById(R.id.shortcuts_cut_btn).setOnClickListener(v -> shortcutCutClicked());
        getActivity().findViewById(R.id.shortcuts_copy_btn).setOnClickListener(v -> shortcutCopyClicked());
        getActivity().findViewById(R.id.shortcuts_paste_btn).setOnClickListener(v -> shortcutPasteClicked());
        getActivity().findViewById(R.id.shortcuts_open_file_btn).setOnClickListener(v -> shortcutOpenFileClicked());
        getActivity().findViewById(R.id.shortcuts_save_btn).setOnClickListener(v -> shortcutSaveClicked());
        getActivity().findViewById(R.id.shortcuts_find_btn).setOnClickListener(v -> shortcutFindClicked());
        getActivity().findViewById(R.id.shortcuts_print_btn).setOnClickListener(v -> shortcutPrintClicked());
        getActivity().findViewById(R.id.shortcuts_new_window_btn).setOnClickListener(v -> shortcutNewWindowClicked());
        getActivity().findViewById(R.id.shortcuts_minimize_window_btn).setOnClickListener(v -> shortcutMinimizeWindowClicked());
        getActivity().findViewById(R.id.shortcuts_close_window_btn).setOnClickListener(v -> shortcutCloseWindowClicked());
        getActivity().findViewById(R.id.shortcuts_switch_apps_btn).setOnClickListener(v -> shortcutSwitchAppsClicked());
        getActivity().findViewById(R.id.shortcuts_undo_btn).setOnClickListener(v -> shortcutUndoClicked());
        getActivity().findViewById(R.id.shortcuts_redo_btn).setOnClickListener(v -> shortcutRedoClicked());
        getActivity().findViewById(R.id.shortcuts_system_search_btn).setOnClickListener(v -> shortcutSystemSearchClicked());
        getActivity().findViewById(R.id.shortcuts_force_quit_btn).setOnClickListener(v -> shortcutForceQuitClicked());
        getActivity().findViewById(R.id.shortcuts_show_desktop_btn).setOnClickListener(v -> shortcutShowDesktopClicked());
        getActivity().findViewById(R.id.shortcuts_left_desktop_btn).setOnClickListener(v -> shortcutLeftDesktopClicked());
        getActivity().findViewById(R.id.shortcuts_right_desktop_btn).setOnClickListener(v -> shortcutRightDesktopClicked());
    }

    private void shortcutSelectAllClicked() {

       Log.d(TAG, "Shortcut Select All");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutSelectAll();
        }
    }

    private void shortcutCutClicked() {

        Log.d(TAG, "Shortcut Cut");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutCut();
        }
    }

    private void shortcutCopyClicked() {

        Log.d(TAG, "Shortcut Copy");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutCopy();
        }
    }

    private void shortcutPasteClicked() {

        Log.d(TAG, "Shortcut Paste");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutPaste();
        }
    }

    private void shortcutOpenFileClicked() {

        Log.d(TAG, "Shortcut Open File");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutOpenFile();
        }
    }

    private void shortcutSaveClicked() {

        Log.d(TAG, "Shortcut Save");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutSave();
        }
    }

    private void shortcutFindClicked() {

        Log.d(TAG,"Shortcut Find");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutFind();
        }

        // show custom keyboard
        mKeyboardButton.setVisibility(View.GONE);
        mMouseButton.setVisibility(View.GONE);
        mBottomSheetKeyboard.setVisibility(View.VISIBLE);
    }

    private void shortcutPrintClicked() {

        Log.d(TAG, "Shortcut Print");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutPrint();
        }
    }

    private void shortcutNewWindowClicked() {

        Log.d(TAG, "Shortcut New Window");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutNewWindow();
        }
    }

    private void shortcutMinimizeWindowClicked() {

        Log.d(TAG, "Shortcut Minimize Window");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutMinimizeWindow();
        }
    }

    private void shortcutCloseWindowClicked() {

        Log.d(TAG,"Shortcut Close Window");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutCloseWindow();
        }
    }

    private void shortcutSwitchAppsClicked() {

        Log.d(TAG,"Shortcut Switch Apps");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutSwitchApps();
        }
    }

    private void shortcutUndoClicked() {

        Log.d(TAG, "Shortcut Undo Clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutUndo();
        }
    }

    private void shortcutRedoClicked() {

        Log.d(TAG,"Shortcut Redo Clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutRedo();
        }
    }

    private void shortcutSystemSearchClicked() {

        Log.d(TAG, "Shortcut System Search");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutSystemSearch();
        }

        // show custom keyboard
        mKeyboardButton.setVisibility(View.GONE);
        mMouseButton.setVisibility(View.GONE);
        mBottomSheetKeyboard.setVisibility(View.VISIBLE);
    }

    private void shortcutForceQuitClicked() {

        Log.d(TAG, "Shortcut Force Quit");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutForceQuit();
        }
    }

    private void shortcutShowDesktopClicked() {

        Log.d(TAG, "Shortcut Show Desktop");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutShowDesktop();
        }
    }

    private void shortcutLeftDesktopClicked() {

        Log.d(TAG, "Shortcut Left Desktop");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutLeftDesktop();
        }
    }

    private void shortcutRightDesktopClicked() {

        Log.d(TAG, "Shortcut Right Desktop");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.shortcutRightDesktop();
        }

    }
}
