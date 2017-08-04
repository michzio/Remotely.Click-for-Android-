package click.remotely.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import java.security.PrivilegedExceptionAction;

import click.remotely.android.R;

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
        getActivity().findViewById(R.id.media_player_volume_mute_btn).setOnClickListener( v -> muteMediaPlayerVolume() );
        getActivity().findViewById(R.id.media_player_volume_up_btn).setOnClickListener( v -> maxMediaPlayerVolume() );

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
    }

    SeekBar.OnSeekBarChangeListener volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            updateMediaPlayerVolume(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    private void muteMediaPlayerVolume() {

        mVolumeSeekBar.setProgress(0);

        Toast.makeText(getActivity(), "MediaPlayer volume muted!", Toast.LENGTH_SHORT).show();
    }

    private void maxMediaPlayerVolume() {
        mVolumeSeekBar.setProgress(100);

        Toast.makeText(getActivity(), "MediaPlayer volume 100.", Toast.LENGTH_SHORT).show();
    }

    private void updateMediaPlayerVolume(int volume) {

        Toast.makeText(getActivity(), "MediaPlayer volume " + volume, Toast.LENGTH_SHORT).show();
    }

    private void mediaPlayerPlayButtonClicked() {

        Toast.makeText(getActivity(), "MediaPlayer play/pause clicked", Toast.LENGTH_SHORT).show();
    }

    private void mediaPlayerStopButtonClicked() {

        Toast.makeText(getActivity(), "MediaPlayer stop clicked", Toast.LENGTH_SHORT).show();
    }

    private void mediaPlayerStepForwardButtonClicked() {

        Toast.makeText(getActivity(), "MediaPlayer step forward clicked", Toast.LENGTH_SHORT).show();
    }

    private void mediaPlayerStepBackwardButtonClicke() {

        Toast.makeText(getActivity(), "MediaPlayer step backward clicked", Toast.LENGTH_SHORT).show();
    }

    private void mediaPlayerSkipNextButtonClicked() {

        Toast.makeText(getActivity(), "MediaPlayer skip next clicked", Toast.LENGTH_SHORT).show();
    }

    private void mediaPlayerSkipPreviousButtonClicked() {

        Toast.makeText(getActivity(), "MediaPlayer skip previous clicked", Toast.LENGTH_SHORT).show();
    }

    private void mediaPlayerLoopButtonClicked() {

        Toast.makeText(getActivity(), "MediaPlayer loop clicked", Toast.LENGTH_SHORT).show();
    }

    private void mediaPlayerShuffleButtonClicked() {

        Toast.makeText(getActivity(), "MediaPlayer shuffle clicked", Toast.LENGTH_SHORT).show();
    }

    private void mediaPlayerFullScreenButtonClicked() {

        Toast.makeText(getActivity(), "Media Player full screen clicked", Toast.LENGTH_SHORT).show();
    }
}
