package click.remotely.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import click.remotely.android.R;

/**
 * Created by michzio on 15/07/2017.
 */

public class TouchpadFragment extends Fragment {

    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    private FloatingActionButton mFloatingActionButton;

    public TouchpadFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_touchpad, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFloatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab_action_keyboard);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
        });

        if(savedInstanceState != null) {
            // Restore the activity's title here
            getActivity().setTitle(savedInstanceState.getCharSequence(ACTIVITY_TITLE_KEY));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }
}
