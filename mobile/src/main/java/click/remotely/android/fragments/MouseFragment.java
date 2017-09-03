package click.remotely.android.fragments;

import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import click.remotely.android.MainDrawerActivity;
import click.remotely.android.R;
import click.remotely.android.interfaces.RemoteControllerClientInterface;
import click.remotely.android.services.RemoteControllerClientService;
import click.remotely.common.Utils;
import click.remotely.inputs.BottomSheetKeyboard;
import click.remotely.inputs.RemoteControllerTouchpadGestureListener;
import click.remotely.inputs.SensorMouse;
import click.remotely.inputs.TouchpadGestureDetector;

/**
 * Created by michzio on 15/07/2017.
 */

public class MouseFragment extends Fragment {

    private static final String TAG = MouseFragment.class.getName();
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    private GestureDetectorCompat mMouseButtonGestureDetector;
    private TouchpadGestureDetector mTouchpadGestureDetector;
    //private GestureDetectorCompat mMouseTouchGestureDetector;
    //private ScaleGestureDetector mMousePinchGestureDetector;
    private Button mLeftMouseButton;
    private Button mRightMouseButton;
    private Button mMouseTouchArea;

    private FloatingActionButton mAirMouseButton;
    private FloatingActionButton mKeyboardButton;
    private ViewGroup mBottomSheetKeyboard;

    private SensorMouse mSensorMouse;

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

        if(isAirMouseOn) {
            mSensorMouse.resumeSensorMouse();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(isAirMouseOn) {
            mSensorMouse.pauseSensorMouse();
            isAirMouseOn = false;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            // Restore the activity's title here
            getActivity().setTitle(savedInstanceState.getCharSequence(ACTIVITY_TITLE_KEY));
        }

        initFloatingActionButtons();
        initGestureDetectors();
        bindMouseButtonsToClickListener();
        bindMouseTouchAreaToTouchListener();
        handleNavigationDrawer();

        mSensorMouse = new SensorMouse(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private void initGestureDetectors() {

        mMouseButtonGestureDetector = new GestureDetectorCompat(getActivity(), mMouseButtonGestureListener);
        mTouchpadGestureDetector = new TouchpadGestureDetector(getActivity(), new RemoteControllerTouchpadGestureListener((RemoteControllerClientInterface) getActivity()));
        //mMouseTouchGestureDetector = new GestureDetectorCompat(getActivity(), mMouseTouchGestureListener);
        //mMousePinchGestureDetector = new ScaleGestureDetector(getActivity(), mMousePinchGestureListener);
    }

    private void bindMouseButtonsToClickListener() {

        mLeftMouseButton = (Button) getActivity().findViewById(R.id.leftMouseButton);
        mRightMouseButton = (Button) getActivity().findViewById(R.id.rightMouseButton);

        mLeftMouseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mMouseButtonGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        mRightMouseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mMouseButtonGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    private void bindMouseTouchAreaToTouchListener() {

        mMouseTouchArea = (Button) getActivity().findViewById(R.id.mouseTouchArea);

        mMouseTouchArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mTouchpadGestureDetector.onTouchEvent(event);
                //mMouseTouchGestureDetector.onTouchEvent(event);
                //mMousePinchGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    private void handleNavigationDrawer() {

        DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                mTouchpadGestureDetector = new TouchpadGestureDetector(getActivity(), new RemoteControllerTouchpadGestureListener((RemoteControllerClientInterface) getActivity()));
                super.onDrawerClosed(drawerView);
            }
        });
    }

    private boolean isAirMouseOn = false;

    private void initFloatingActionButtons() {

        mAirMouseButton = (FloatingActionButton) getActivity().findViewById(R.id.mouse_fragment_fab_action_mouse);
        mKeyboardButton = (FloatingActionButton) getActivity().findViewById(R.id.mouse_fragment_fab_action_keyboard);

        mAirMouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAirMouseOn) {
                    mSensorMouse.pauseSensorMouse();
                    isAirMouseOn = false;
                } else {
                    mSensorMouse.resumeSensorMouse();
                    isAirMouseOn = true;
                }
            }
        });

        mBottomSheetKeyboard = (ViewGroup) getActivity().findViewById(R.id.bottom_sheet_keyboard);

        BottomSheetKeyboard keyboard = new BottomSheetKeyboard(mBottomSheetKeyboard, (RemoteControllerClientInterface) getActivity());

        mKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show custom keyboard
                mKeyboardButton.setVisibility(View.GONE);
                mAirMouseButton.setVisibility(View.GONE);
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
                            mAirMouseButton.setVisibility(View.VISIBLE);
                            mKeyboardButton.setVisibility(View.VISIBLE);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private GestureDetector.SimpleOnGestureListener mMouseButtonGestureListener =  new GestureDetector.SimpleOnGestureListener() {

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

            if(Utils.isViewContains(mLeftMouseButton, (int) x, (int) y)  ) {
                Log.d(TAG, "Left mouse button double tap.");

                RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
                if(clientService != null) {
                    clientService.mouseDoubleClick(RemoteControllerClientService.MouseButton.LEFT);
                }

            } else if(Utils.isViewContains(mRightMouseButton, (int) x, (int) y) ) {
                Log.d(TAG, "Right mouse button double tap.");

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

            if( Utils.isViewContains(mLeftMouseButton, (int) x, (int) y) ) {
                Log.d(TAG, "Left mouse button single tap.");

                RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
                if(clientService != null) {
                    clientService.mouseClick(RemoteControllerClientService.MouseButton.LEFT);
                }
            } else if(Utils.isViewContains(mRightMouseButton, (int) x, (int) y) ) {
                Log.d(TAG, "Right mouse button single tap.");

                RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
                if(clientService != null) {
                    clientService.mouseClick(RemoteControllerClientService.MouseButton.RIGHT);
                }
            }

            return true;
        }
    };

    /*
    private GestureDetector.SimpleOnGestureListener mMouseTouchGestureListener =  new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onContextClick(MotionEvent e) {

            Log.d(TAG, "Context Click on touch area.");

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            Log.d(TAG, "Scrolling");

            if(e1.getPointerCount() == 2 && e2.getPointerCount() == 2) {

                Log.d(TAG, "Double finger scrolling.");
            }

            return false;
        }
    };

    private ScaleGestureDetector.SimpleOnScaleGestureListener mMousePinchGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private float scale = 1f;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5.0f));

            Log.d(TAG, "Pinch gesture detected with scale factor: "+ scale);
            return false;
        }
    };
    */
}

