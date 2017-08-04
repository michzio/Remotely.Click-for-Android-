package click.remotely.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import click.remotely.android.R;

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
        getActivity().findViewById(R.id.slide_show_blank_black_slide_btn).setOnClickListener(v -> slideShowBlankBlackSlideClicked());
        getActivity().findViewById(R.id.slide_show_blank_white_slide_btn).setOnClickListener(v -> slideShowBlankWhiteSlideClicked());
        getActivity().findViewById(R.id.slide_show_first_slide_btn).setOnClickListener(v -> slideShowFirstSlideClicked());
        getActivity().findViewById(R.id.slide_show_last_slide_btn).setOnClickListener(v -> slideShowLastSlideClicked());
    }

    private void slideShowStartClicked() {

        Toast.makeText(getActivity(), "Slide show start", Toast.LENGTH_SHORT).show();
    }

    private void slideShowEndClicked() {

        Toast.makeText(getActivity(), "Slide show end", Toast.LENGTH_SHORT).show();
    }

    private void slideShowPreviousClicked() {

        Toast.makeText(getActivity(), "Slide show previous", Toast.LENGTH_SHORT).show();
    }

    private void slideShowNextClicked() {

        Toast.makeText(getActivity(), "Slide show next", Toast.LENGTH_SHORT).show();
    }

    private void slideShowPreviousNoAnimationClicked() {

        Toast.makeText(getActivity(), "Slide show previous no animation", Toast.LENGTH_SHORT).show();
    }

    private void slideShowNextNoAnimationClicked() {

        Toast.makeText(getActivity(), "Slide show next no animation", Toast.LENGTH_SHORT).show();
    }

    private void slideShowArrowPointerClicked() {

        Toast.makeText(getActivity(), "Slide show arrow pointer", Toast.LENGTH_SHORT).show();
    }

    private void slideShowPenPointerClicked() {

        Toast.makeText(getActivity(), "Slide show pen pointer", Toast.LENGTH_SHORT).show();
    }

    private void slideShowBlankBlackSlideClicked() {

        Toast.makeText(getActivity(), "Slide show blank black slide", Toast.LENGTH_SHORT).show();
    }

    private void slideShowBlankWhiteSlideClicked() {

        Toast.makeText(getActivity(), "Slide show blank white slide", Toast.LENGTH_SHORT).show();
    }

    private void slideShowFirstSlideClicked() {

        Toast.makeText(getActivity(), "Slide show first slide", Toast.LENGTH_SHORT).show();
    }

    private void slideShowLastSlideClicked() {

        Toast.makeText(getActivity(), "Slide show last slide", Toast.LENGTH_SHORT).show();
    }
}
