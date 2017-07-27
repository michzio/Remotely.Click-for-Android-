package click.remotely.android.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import click.remotely.android.R;
import click.remotely.model.DPadInput;

/**
 * Created by michzio on 15/07/2017.
 */

public class GamepadFragment extends Fragment {

    private static final String TAG = GamepadFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    private GestureDetectorCompat leftThumbstickGestureDetector;
    private GestureDetectorCompat rightThumbstickGestureDetector;

    public GamepadFragment() {

        // create gesture detectors
        leftThumbstickGestureDetector = new GestureDetectorCompat(getActivity(), new ThumbstickGestureListener("Left stick (L3)"));
        rightThumbstickGestureDetector = new GestureDetectorCompat(getActivity(), new ThumbstickGestureListener("Right stick (R3)"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gamepad, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            // Restore the activity's title here
            getActivity().setTitle(savedInstanceState.getCharSequence(ACTIVITY_TITLE_KEY));
        }

        bindGamepadButtonsToClickListener();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    private void bindGamepadButtonsToClickListener() {

        // D-Pad click
        getActivity().findViewById(R.id.gamepad_dpad_left_down_btn).setOnClickListener( v -> onDPadClick(DPadInput.LEFT_DOWN) );
        getActivity().findViewById(R.id.gamepad_dpad_right_down_btn).setOnClickListener( v -> onDPadClick(DPadInput.RIGHT_DOWN) );
        getActivity().findViewById(R.id.gamepad_dpad_left_up_btn).setOnClickListener( v -> onDPadClick(DPadInput.LEFT_UP) );
        getActivity().findViewById(R.id.gamepad_dpad_right_up_btn).setOnClickListener( v -> onDPadClick(DPadInput.RIGHT_UP) );

        getActivity().findViewById(R.id.gamepad_dpad_left_btn).setOnClickListener( v -> onDPadClick(DPadInput.LEFT) );
        getActivity().findViewById(R.id.gamepad_dpad_right_btn).setOnClickListener( v -> onDPadClick(DPadInput.RIGHT) );
        getActivity().findViewById(R.id.gamepad_dpad_up_btn).setOnClickListener( v -> onDPadClick(DPadInput.UP) );
        getActivity().findViewById(R.id.gamepad_dpad_down_btn).setOnClickListener( v -> onDPadClick(DPadInput.DOWN) );

        getActivity().findViewById(R.id.gamepad_dpad_center_btn).setOnClickListener( v -> onDPadClick(DPadInput.CENTER) );

        // Select (left arrow), start (right arrow) buttons click
        getActivity().findViewById(R.id.gamepad_left_arrow).setOnClickListener( v -> onSelectButtonClick());
        getActivity().findViewById(R.id.gamepad_right_arrow).setOnClickListener( v -> onStartButtonClick());

        // gamepad buttons click
        getActivity().findViewById(R.id.gamepad_X_btn).setOnClickListener(gamepadButtonListener);
        getActivity().findViewById(R.id.gamepad_Y_btn).setOnClickListener(gamepadButtonListener);
        getActivity().findViewById(R.id.gamepad_A_btn).setOnClickListener(gamepadButtonListener);
        getActivity().findViewById(R.id.gamepad_B_btn).setOnClickListener(gamepadButtonListener);
        getActivity().findViewById(R.id.gamepad_L1_btn).setOnClickListener(gamepadButtonListener);
        getActivity().findViewById(R.id.gamepad_L2_btn).setOnClickListener(gamepadButtonListener);
        getActivity().findViewById(R.id.gamepad_R1_btn).setOnClickListener(gamepadButtonListener);
        getActivity().findViewById(R.id.gamepad_R2_btn).setOnClickListener(gamepadButtonListener);

        // thumbstick input handling
        Button leftThumbstick = (Button) getActivity().findViewById(R.id.gamepad_left_stick);
        Button rightThumbstick = (Button) getActivity().findViewById(R.id.gamepad_right_stick);

        leftThumbstick.setOnTouchListener((View v, MotionEvent event) -> {
            return leftThumbstickGestureDetector.onTouchEvent(event);
        });
        rightThumbstick.setOnTouchListener((View v, MotionEvent event) -> {
            return rightThumbstickGestureDetector.onTouchEvent(event);
        });
    }

    private void onDPadClick(DPadInput input) {

        Toast.makeText(getActivity(), "DPad input: " + input.toString(), Toast.LENGTH_LONG).show();
    }

    private void onSelectButtonClick() {

        Toast.makeText(getActivity(), "Select button.", Toast.LENGTH_LONG).show();
    }

    private void onStartButtonClick() {

        Toast.makeText(getActivity(), "Start button.", Toast.LENGTH_LONG).show();
    }

    private View.OnClickListener gamepadButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String gamepadInputName = ((Button) v).getText().toString();

            Toast.makeText(getActivity(), "Gamepad input: " + gamepadInputName, Toast.LENGTH_LONG).show();
        }
    };

    private class ThumbstickGestureListener extends GestureDetector.SimpleOnGestureListener {

        private String thumbstickkName;

        public ThumbstickGestureListener(String thumbstickkName) {

            this.thumbstickkName = thumbstickkName;
        }

        public void setThumbstickkName(String name) {
            this.thumbstickkName = name;
        }

        public String getThumbstickName() {
            return this.thumbstickkName;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            Toast.makeText(getActivity(), "Double tap on " + this.thumbstickkName + " at (" + x  + "," + y + ")", Toast.LENGTH_LONG).show();

            return true;
        }
    }
}
