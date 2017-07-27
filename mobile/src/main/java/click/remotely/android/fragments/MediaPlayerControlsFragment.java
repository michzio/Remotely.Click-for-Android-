package click.remotely.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import click.remotely.android.R;

/**
 * Created by michzio on 15/07/2017.
 */

public class MediaPlayerControlsFragment extends Fragment {

    private static final String TAG = MediaPlayerControlsFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    public MediaPlayerControlsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_media_player, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            // Restore the activity's title here
            getActivity().setTitle(savedInstanceState.getCharSequence(ACTIVITY_TITLE_KEY));
        }

        bindMediaPlayerButtonsToClickListener();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private void bindMediaPlayerButtonsToClickListener() {

    }

}
