package click.remotely.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import click.remotely.android.R;

/**
 * Created by michzio on 15/07/2017.
 */

public class ShortcutsFragment extends Fragment {

    private static final String TAG = ShortcutsFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

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

        bindShortcutsButtonsToClickListeners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private void bindShortcutsButtonsToClickListeners() {

        // bind floating action buttons
        getActivity().findViewById(R.id.shortcuts_fab_action_keyboard)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSoftwareKeyboard();
                    }
                });

        getActivity().findViewById(R.id.shortcuts_fab_action_mouse)
                .setOnClickListener(new View.OnClickListener() {

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
        getActivity().findViewById(R.id.shortcuts_force_quite_btn).setOnClickListener(v -> shortcutForceQuiteClicked());
        getActivity().findViewById(R.id.shortcuts_show_desktop_btn).setOnClickListener(v -> shortcutShowDesktopClicked());
        getActivity().findViewById(R.id.shortcuts_left_desktop_btn).setOnClickListener(v -> shortcutLeftDesktopClicked());
        getActivity().findViewById(R.id.shortcuts_right_desktop_btn).setOnClickListener(v -> shortcutRightDesktopClicked());
    }

    private void showSoftwareKeyboard() {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }

    private void shortcutSelectAllClicked() {

        Toast.makeText(getActivity(), "Shortcut Select All", Toast.LENGTH_SHORT).show();
    }

    private void shortcutCutClicked() {

        Toast.makeText(getActivity(), "Shortcut Cut", Toast.LENGTH_SHORT).show();
    }

    private void shortcutCopyClicked() {

        Toast.makeText(getActivity(), "Shortcut Copy", Toast.LENGTH_SHORT).show();
    }

    private void shortcutPasteClicked() {

        Toast.makeText(getActivity(), "Shortcut Paste", Toast.LENGTH_SHORT).show();
    }

    private void shortcutOpenFileClicked() {

        Toast.makeText(getActivity(), "Shortcut Open File", Toast.LENGTH_SHORT).show();
    }

    private void shortcutSaveClicked() {

        Toast.makeText(getActivity(), "Shortcut Save", Toast.LENGTH_SHORT).show();
    }

    private void shortcutFindClicked() {

        Toast.makeText(getActivity(), "Shortcut Find", Toast.LENGTH_SHORT).show();

        showSoftwareKeyboard();
    }

    private void shortcutPrintClicked() {

        Toast.makeText(getActivity(), "Shortcut Print", Toast.LENGTH_SHORT).show();
    }

    private void shortcutNewWindowClicked() {

        Toast.makeText(getActivity(), "Shortcut New Window", Toast.LENGTH_SHORT).show();
    }

    private void shortcutMinimizeWindowClicked() {

        Toast.makeText(getActivity(), "Shortcut Minimize Window", Toast.LENGTH_SHORT).show();
    }

    private void shortcutCloseWindowClicked() {

        Toast.makeText(getActivity(), "Shortcut Close Window", Toast.LENGTH_SHORT).show();
    }

    private void shortcutSwitchAppsClicked() {

        Toast.makeText(getActivity(), "Shortcut Switch Apps", Toast.LENGTH_SHORT).show();
    }

    private void shortcutUndoClicked() {

        Toast.makeText(getActivity(), "Shortcut Undo Clicked", Toast.LENGTH_SHORT).show();
    }

    private void shortcutRedoClicked() {

        Toast.makeText(getActivity(), "Shortcut Redo Clicked", Toast.LENGTH_SHORT).show();
    }

    private void shortcutSystemSearchClicked() {

        Toast.makeText(getActivity(), "Shortcut System Search", Toast.LENGTH_SHORT).show();

        showSoftwareKeyboard();
    }

    private void shortcutForceQuiteClicked() {

        Toast.makeText(getActivity(), "Shortcut Force Quite", Toast.LENGTH_SHORT).show();
    }

    private void shortcutShowDesktopClicked() {

        Toast.makeText(getActivity(), "Shortcut Show Desktop", Toast.LENGTH_SHORT).show();
    }

    private void shortcutLeftDesktopClicked() {

        Toast.makeText(getActivity(), "Shortcut Left Desktop", Toast.LENGTH_SHORT).show();
    }

    private void shortcutRightDesktopClicked() {

        Toast.makeText(getActivity(), "Shortcut Right Desktop", Toast.LENGTH_SHORT).show();
    }
}
