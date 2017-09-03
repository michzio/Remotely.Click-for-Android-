package click.remotely.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import click.remotely.android.MainDrawerActivity;
import click.remotely.android.R;
import click.remotely.android.services.RemoteControllerClientService;

/**
 * Created by michzio on 15/07/2017.
 */

public class MediaPlayerControlsFragment extends Fragment {

    private static final String TAG = MediaPlayerControlsFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    private SeekBar mVolumeSeekBar;

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

        bindMediaPlayerButtonsToClickListeners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private void bindMediaPlayerButtonsToClickListeners() {

        // volume seek bar listener
        mVolumeSeekBar = (SeekBar) getActivity().findViewById(R.id.volume_seek_bar);
        mVolumeSeekBar.setOnSeekBarChangeListener(volumeChangeListener);

        // volume buttons listeners
        getActivity().findViewById(R.id.media_player_volume_mute_btn).setOnClickListener( v -> mediaPlayerVolumeMute() );
        getActivity().findViewById(R.id.media_player_volume_up_btn).setOnClickListener( v -> mediaPlayerVolumeMax() );

        getActivity().findViewById(R.id.media_player_player_volume_mute_btn).setOnClickListener( v -> mediaPlayerPlayerVolumeMute() );
        getActivity().findViewById(R.id.media_player_player_volume_down_btn).setOnClickListener( v -> mediaPlayerPlayerVolumeDown() );
        getActivity().findViewById(R.id.media_player_player_volume_up_btn).setOnClickListener( v -> mediaPlayerPlayerVolumeUp() );

        // media player buttons listeners
        getActivity().findViewById(R.id.media_player_play_pause_btn).setOnClickListener( v -> mediaPlayerPlayButtonClicked() );
        getActivity().findViewById(R.id.media_player_stop_btn).setOnClickListener(v -> mediaPlayerStopButtonClicked() );
        getActivity().findViewById(R.id.media_player_step_forward_btn).setOnClickListener(v -> mediaPlayerStepForwardButtonClicked() );
        getActivity().findViewById(R.id.media_player_step_backward_btn).setOnClickListener(v -> mediaPlayerStepBackwardButtonClicke() );
        getActivity().findViewById(R.id.media_player_skip_next_btn).setOnClickListener(v -> mediaPlayerSkipNextButtonClicked());
        getActivity().findViewById(R.id.media_player_skip_previous_btn).setOnClickListener(v -> mediaPlayerSkipPreviousButtonClicked());

        getActivity().findViewById(R.id.media_player_loop_btn).setOnClickListener(v -> mediaPlayerLoopButtonClicked() );
        getActivity().findViewById(R.id.media_player_shuffle_btn).setOnClickListener(v -> mediaPlayerShuffleButtonClicked());
        getActivity().findViewById(R.id.media_player_fullscreen_btn).setOnClickListener(v -> mediaPlayerFullScreenButtonClicked());
        getActivity().findViewById(R.id.media_player_subtitles_btn).setOnClickListener(v -> mediaPlayerSubtitlesButtonClicked());
    }

    SeekBar.OnSeekBarChangeListener volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            mediaPlayerVolumeSetValue(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    private void mediaPlayerVolumeMute() {

        mVolumeSeekBar.setProgress(0);
        Log.d(TAG,  "MediaPlayer volume muted!");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.systemVolumeMute();
        }
    }

    private void mediaPlayerVolumeMax() {
        mVolumeSeekBar.setProgress(100);

        Log.d(TAG,"MediaPlayer volume 100.");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.systemVolumeMax();
        }
    }

    private void mediaPlayerVolumeSetValue(int volume) {

        Log.d(TAG, "MediaPlayer volume " + volume);

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.systemVolume(volume);
        }
    }

    private void mediaPlayerPlayerVolumeMute() {

        Log.d(TAG,  "MediaPlayer player volume muted!");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerVolumeMute();
        }
    }

    private void mediaPlayerPlayerVolumeDown() {

        Log.d(TAG,  "MediaPlayer player volume down.");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerVolumeDown();
        }
    }

    private void mediaPlayerPlayerVolumeUp() {

        Log.d(TAG,  "MediaPlayer player volume up.");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerVolumeUp();
        }
    }

    private void mediaPlayerPlayButtonClicked() {

        Log.d(TAG, "MediaPlayer play/pause clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerPlayPause();
        }
    }

    private void mediaPlayerStopButtonClicked() {

        Log.d(TAG,"MediaPlayer stop clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerStop();
        }
    }

    private void mediaPlayerStepForwardButtonClicked() {

        Log.d(TAG, "MediaPlayer step forward clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerStepForward();
        }
    }

    private void mediaPlayerStepBackwardButtonClicke() {

        Log.d(TAG, "MediaPlayer step backward clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerStepBackward();
        }
    }

    private void mediaPlayerSkipNextButtonClicked() {

        Log.d(TAG, "MediaPlayer skip next clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerSkipNext();
        }
    }

    private void mediaPlayerSkipPreviousButtonClicked() {

        Log.d(TAG,"MediaPlayer skip previous clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerSkipPrevious();
        }
    }

    private void mediaPlayerLoopButtonClicked() {

        Log.d(TAG, "MediaPlayer loop clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerLoop();
        }
    }

    private void mediaPlayerShuffleButtonClicked() {

        Log.d( TAG,"MediaPlayer shuffle clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerShuffle();
        }
    }

    private void mediaPlayerFullScreenButtonClicked() {

        Log.d(TAG,"Media Player full screen clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerFullScreen();
        }
    }

    private void mediaPlayerSubtitlesButtonClicked() {

        Log.d(TAG,"Media Player subtitles clicked");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.playerSubtitles();
        }
    }
}
