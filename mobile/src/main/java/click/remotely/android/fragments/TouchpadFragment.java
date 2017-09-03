package click.remotely.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import click.remotely.android.MainDrawerActivity;
import click.remotely.android.R;
import click.remotely.android.interfaces.RemoteControllerClientInterface;
import click.remotely.android.services.RemoteControllerClientService;
import click.remotely.common.Utils;
import click.remotely.inputs.BottomSheetKeyboard;
import click.remotely.inputs.RemoteControllerTouchpadGestureListener;
import click.remotely.inputs.TouchpadGestureDetector;
import click.remotely.inputs.TouchpadView;

/**
 * Created by michzio on 15/07/2017.
 */

public class TouchpadFragment extends Fragment {

    private static final String TAG = TouchpadFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    private FloatingActionButton mFloatingActionButton;
    private ViewGroup mBottomSheetKeyboard;

    private GestureDetectorCompat mTouchpadButtonGestureDetector;
    private TouchpadGestureDetector mTouchpadGestureDetector;

    private Button mLeftTouchpadButton;
    private Button mRightTouchpadButton;
    private TouchpadView mTouchpadView;

    public TouchpadFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_touchpad, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            // Restore the activity's title here
            getActivity().setTitle(savedInstanceState.getCharSequence(ACTIVITY_TITLE_KEY));
        }

        initFloatingActionButtons();
        initGestureDetectors();
        bindTouchpadButtonsToClickListener();
        bindTouchpadViewToTouchListener();
        handleNavigationDrawer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private void initFloatingActionButtons() {

        mFloatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.touchpad_fab_action_keyboard);
        mBottomSheetKeyboard = (ViewGroup) getActivity().findViewById(R.id.bottom_sheet_keyboard);

        BottomSheetKeyboard keyboard = new BottomSheetKeyboard(mBottomSheetKeyboard, (RemoteControllerClientInterface) getActivity());

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show system soft keyboard
                // InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                // show custom keyboard
                mFloatingActionButton.setVisibility(View.GONE);
                mBottomSheetKeyboard.setVisibility(View.VISIBLE);
            }
        });

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if(mBottomSheetKeyboard.getVisibility() == View.VISIBLE) {
                            mBottomSheetKeyboard.setVisibility(View.GONE);
                            mFloatingActionButton.setVisibility(View.VISIBLE);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void initGestureDetectors() {

        mTouchpadButtonGestureDetector = new GestureDetectorCompat(getActivity(), mTouchpadButtonGestureListener);
        mTouchpadGestureDetector = new TouchpadGestureDetector(getActivity(), new RemoteControllerTouchpadGestureListener((RemoteControllerClientInterface) getActivity()));
    }

    private void bindTouchpadButtonsToClickListener() {

        mLeftTouchpadButton = (Button) getActivity().findViewById(R.id.touchpad_left_button);
        mRightTouchpadButton = (Button) getActivity().findViewById(R.id.touchpad_right_button);

        mLeftTouchpadButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mTouchpadButtonGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        mRightTouchpadButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mTouchpadButtonGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    private void bindTouchpadViewToTouchListener() {

        mTouchpadView = (TouchpadView) getActivity().findViewById(R.id.touchpad_view);
        mTouchpadView.setTouchpadGestureDetector(mTouchpadGestureDetector);
    }

    private void handleNavigationDrawer() {

        DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mTouchpadGestureDetector = new TouchpadGestureDetector(getActivity(), new RemoteControllerTouchpadGestureListener((RemoteControllerClientInterface) getActivity()));
                mTouchpadView.setTouchpadGestureDetector(mTouchpadGestureDetector);
            }

        });
    }

    private GestureDetector.SimpleOnGestureListener mTouchpadButtonGestureListener =  new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getRawX();
            float y = e.getRawY();

            Log.d(TAG, "Double tapped at: (" + x + "," + y + ")");

            if(Utils.isViewContains(mLeftTouchpadButton, (int) x, (int) y)  ) {
                Log.d(TAG, "Left touchpad button double tap.");

                RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
                if(clientService != null) {
                    clientService.mouseDoubleClick(RemoteControllerClientService.MouseButton.LEFT);
                }

            } else if(Utils.isViewContains(mRightTouchpadButton, (int) x, (int) y) ) {
                Log.d(TAG, "Right touchpad button double tap.");

                RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
                if(clientService != null) {
                    clientService.mouseDoubleClick(RemoteControllerClientService.MouseButton.RIGHT);
                }
            }

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            float x = e.getRawX();
            float y = e.getRawY();

            Log.d(TAG, "Single tapped confirmed at (" + x + "," + y + ")");

            if( Utils.isViewContains(mLeftTouchpadButton, (int) x, (int) y) ) {
                Log.d(TAG, "Left touchpad button single tap.");

                RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
                if(clientService != null) {
                    clientService.mouseClick(RemoteControllerClientService.MouseButton.LEFT);
                }
            } else if( Utils.isViewContains(mRightTouchpadButton, (int) x, (int) y) ) {
                Log.d(TAG, "Right touchpad button single tap.");

                RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
                if(clientService != null) {
                    clientService.mouseClick(RemoteControllerClientService.MouseButton.RIGHT);
                }
            }

            return true;
        }
    };
}
