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

public class SlideShowControlsFragment extends Fragment {

    private static final String TAG = SlideShowControlsFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    public SlideShowControlsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_slide_show, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            // Restore the activity's title here
            getActivity().setTitle(savedInstanceState.getCharSequence(ACTIVITY_TITLE_KEY));
        }

        bindSlidShowButtonsToClickListeners();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private void bindSlidShowButtonsToClickListeners() {

        getActivity().findViewById(R.id.slide_show_start_btn).setOnClickListener(v -> slideShowStartClicked());
        getActivity().findViewById(R.id.slide_show_end_btn).setOnClickListener(v -> slideShowEndClicked());
        getActivity().findViewById(R.id.slide_show_previous_btn).setOnClickListener(v -> slideShowPreviousClicked());
        getActivity().findViewById(R.id.slide_show_next_btn).setOnClickListener(v -> slideShowNextClicked());
        getActivity().findViewById(R.id.slide_show_previous_no_animation_btn).setOnClickListener(v -> slideShowPreviousNoAnimationClicked());
        getActivity().findViewById(R.id.slide_show_next_no_animation_btn).setOnClickListener(v -> slideShowNextNoAnimationClicked());
        getActivity().findViewById(R.id.slide_show_arrow_pointer_btn).setOnClickListener(v -> slideShowArrowPointerClicked());
        getActivity().findViewById(R.id.slide_show_pen_pointer_btn).setOnClickListener(v -> slideShowPenPointerClicked());
        getActivity().findViewById(R.id.slide_show_blank_black_slide_btn).setOnClickListener(v -> slideShowBlankSlideBlackClicked());
        getActivity().findViewById(R.id.slide_show_blank_white_slide_btn).setOnClickListener(v -> slideShowBlankSlideWhiteClicked());
        getActivity().findViewById(R.id.slide_show_first_slide_btn).setOnClickListener(v -> slideShowFirstSlideClicked());
        getActivity().findViewById(R.id.slide_show_last_slide_btn).setOnClickListener(v -> slideShowLastSlideClicked());
    }

    private void slideShowStartClicked() {

        Log.d(TAG, "Slide show start");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowStart();
        }
    }

    private void slideShowEndClicked() {

        Log.d(TAG,"Slide show end");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowEnd();
        }
    }

    private void slideShowPreviousClicked() {

        Log.d(TAG, "Slide show previous");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowPrevious(true);
        }
    }

    private void slideShowNextClicked() {

        Log.d( TAG,"Slide show next");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowNext(true);
        }
    }

    private void slideShowPreviousNoAnimationClicked() {

        Log.d(TAG, "Slide show previous no animation");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowPrevious(false);
        }
    }

    private void slideShowNextNoAnimationClicked() {

        Log.d(TAG, "Slide show next no animation");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowNext(false);
        }
    }

    private void slideShowArrowPointerClicked() {

        Log.d(TAG, "Slide show arrow pointer");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowPointer(RemoteControllerClientService.SLIDE_SHOW_POINTER_ARROW);
        }
    }

    private void slideShowPenPointerClicked() {

        Log.d(TAG,"Slide show pen pointer");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowPointer(RemoteControllerClientService.SLIDE_SHOW_POINTER_PEN);
        }
    }

    private void slideShowBlankSlideBlackClicked() {

        Log.d(TAG, "Slide show blank black slide");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowBlankSlide(RemoteControllerClientService.SLIDE_SHOW_BLANK_SLIDE_BLACK);
        }
    }

    private void slideShowBlankSlideWhiteClicked() {

        Log.d(TAG, "Slide show blank white slide");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowBlankSlide(RemoteControllerClientService.SLIDE_SHOW_BLANK_SLIDE_WHITE);
        }
    }

    private void slideShowFirstSlideClicked() {

        Log.d(TAG, "Slide show first slide");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowFirstSlide();
        }
    }

    private void slideShowLastSlideClicked() {

        Log.d(TAG, "Slide show last slide");

        RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
        if(clientService != null) {
            clientService.slideShowLastSlide();
        }
    }
}
