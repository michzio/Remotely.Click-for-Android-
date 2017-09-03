package click.remotely.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import click.remotely.android.MainDrawerActivity;
import click.remotely.android.R;
import click.remotely.android.services.RemoteControllerClientService;

/**
 * Created by michzio on 15/07/2017.
 */

public class PowerControlsFragment extends Fragment {

    private static final String TAG = PowerControlsFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    public PowerControlsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_power_controls, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            // Restore the activity's title here
            getActivity().setTitle(savedInstanceState.getCharSequence(ACTIVITY_TITLE_KEY));
        }

        bindPowerControlButtonsToClickListeners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private void bindPowerControlButtonsToClickListeners() {

        getActivity().findViewById(R.id.power_controls_shut_down_btn).setOnClickListener(v -> shutDownClicked());
        getActivity().findViewById(R.id.power_controls_restart_btn).setOnClickListener(v -> restartClicked());
        getActivity().findViewById(R.id.power_controls_sleep_btn).setOnClickListener(v -> sleepClicked());
        getActivity().findViewById(R.id.power_controls_logout_btn).setOnClickListener(v -> logOutClicked());
    }

    private void shutDownClicked() {

        Log.d(TAG, "Shut down");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.systemShutDown();
        }
    }

    private void restartClicked() {

        Log.d(TAG, "Restart");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.systemRestart();
        }
    }

    private void sleepClicked() {

        Log.d(TAG, "Sleep");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.systemSleep();
        }
    }

    private void logOutClicked() {

        Log.d(TAG,"Log out");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.systemLogOut();
        }
    }

}
