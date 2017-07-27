package click.remotely.android.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import click.remotely.android.R;

public class NumpadFragment extends Fragment {

    private static final String TAG = NumpadFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    public NumpadFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_numpad, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            // Restore the activity's title here
            getActivity().setTitle(savedInstanceState.getCharSequence(ACTIVITY_TITLE_KEY));
        }

        bindNumpadKeyButtonsToClickListener();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private void bindNumpadKeyButtonsToClickListener() {
        getActivity().findViewById(R.id.numpad_0_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_1_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_2_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_3_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_4_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_5_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_6_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_7_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_8_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_9_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_clear_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_decimal_point_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_divide_sign_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_enter_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_equal_sign_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_minus_sign_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_multiply_sign_btn).setOnClickListener(numpadKeyListener);
        getActivity().findViewById(R.id.numpad_plus_sign_btn).setOnClickListener(numpadKeyListener);
    }

    private View.OnClickListener numpadKeyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String numpadKey = ((Button) v).getText().toString();
            Log.d(TAG, "Numpad key: " + numpadKey + " clicked.");
        }
    };
}