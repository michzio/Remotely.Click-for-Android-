package click.remotely.android.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import click.remotely.android.R;

/**
 * Created by michzio on 15/07/2017.
 */

public class MouseFragment extends Fragment {

    private static final String TAG = MouseFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    public MouseFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_mouse, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }
}

