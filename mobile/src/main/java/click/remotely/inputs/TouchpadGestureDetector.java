package click.remotely.inputs;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by michzio on 24/08/2017.
 */

public class TouchpadGestureDetector {

    private static final String TAG = TouchpadGestureDetector.class.getName();

    public interface OnGestureListener {

        public boolean onCursorMove(MotionEvent evt, float dx, float dy); // Done
        public boolean onSingleTap(MotionEvent evt); // Done
        public boolean onDoubleTap(MotionEvent evt); // Done
        public boolean onTripleTap(MotionEvent evt); // Done
        public boolean onSingleRightTap(MotionEvent evt); // Done
        public boolean onDoubleRightTap(MotionEvent evt); // Done
        public boolean onTripleRightTap(MotionEvent evt); // Done
        public boolean onScroll(MotionEvent evt, Direction scrollDirection, float scrollValue); // Done
        public boolean onPinch(MotionEvent ect, float scaleFactor);  // Done
        public boolean onTripleSwipe(MotionEvent evt, Direction swipeDirection, float swipeValue); // Done
        public boolean onRotate(MotionEvent evt, float rotationAngle); // Done
        public boolean onDrag(MotionEvent evt, float dx, float dy); // Done
    }

    public static enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private Context mContext;
    private OnGestureListener mListener;

    public TouchpadGestureDetector(Context context, OnGestureListener listener) {

        mContext = context;
        mListener = listener;
    }

    private int mPointerCount;
    private Point mPoint0;
    private Point mPoint1;
    private Point mPoint2;

    private Point mLastPoint0;
    private Point mLastPoint1;

    private float mFingerDistance;

    private int mScaledTouchSlop;
    private int mScaledDoubleTapSlop;
    private int mTapTimeout;
    private int mDoubleTapTimeout;

    private Handler mLeftHandler = new Handler();
    private long mLeftTapDownTimestamp = 0;
    private long mLastLeftTapDownTimestamp = 0;
    private int mNumberOfLeftTaps = 0;

    private Handler mRightHandler = new Handler();
    private long mRightTapDownTimestamp = 0;
    private long mLastRightTapDownTimestamp = 0;
    private int mNumberOfRightTaps = 0;

    private float mStartAngle = 0;

    public boolean onTouchEvent(MotionEvent event) {

        final int action = MotionEventCompat.getActionMasked(event);
        final ViewConfiguration viewConfig = ViewConfiguration.get(mContext);
        mScaledTouchSlop = viewConfig.getScaledTouchSlop();
        mScaledDoubleTapSlop = viewConfig.getScaledDoubleTapSlop();
        mTapTimeout = viewConfig.getTapTimeout();
        mDoubleTapTimeout = viewConfig.getDoubleTapTimeout();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                actionPointerDown(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                actionPointerUp(event);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(event);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;
        }

        Log.d(TAG, "Pointers count: " + mPointerCount);

        return false;
    };

    private void actionDown(MotionEvent evt) {
        Log.d(TAG, "Action Down");
        // first finger touches the screen
        // increase number of pointers and store first point coordinates
        mPointerCount++;
        mPoint0 = new Point(evt.getX(0), evt.getY(0));
        mLastPoint0 = mPoint0;

        // record left finger touch timestamp
        mLeftTapDownTimestamp = System.currentTimeMillis();

        // reset start angle
        mStartAngle = 0;
    }

    private void actionPointerDown(MotionEvent evt) {
        Log.d(TAG, "Action Pointer Down");
        // subsequent finger touches the screen
        // increase number of pointers and store point coordinates
        mPointerCount++;
        if(mPointerCount == 2) {
            mPoint1 = new Point(evt.getX(1), evt.getY(1));
            mLastPoint1 = mPoint1;
            mFingerDistance = mPoint0.distanceFrom(mPoint1);

            // record right finger touch timestamp
            mRightTapDownTimestamp = System.currentTimeMillis();
        }
        if(mPointerCount == 3) {
            mPoint2 = new Point(evt.getX(2), evt.getY(2));
        }
    }

    private void actionPointerUp(MotionEvent evt) {
        Log.d(TAG, "Action Pointer Up");
        if(mPointerCount == 2) {
            handleRightTap(evt);
        }

        mPointerCount--;
        if(mPointerCount < 3) {
            mPoint2 = null;
        }
        if(mPointerCount < 2) {
            mPoint1 = null;
        }
    }

    private void actionUp(MotionEvent evt) {
        Log.d(TAG, "Action Up");

        handleLeftTap(evt);

        // last finger touch released
        mPointerCount--;
        if(mPointerCount == 0) {
            mPoint0 = null;
        }

    }

    private void actionMove(MotionEvent evt) {
        Log.d(TAG, "Action Move");
        Log.d(TAG,  mPointerCount + " pointers move.");

        handleScrolling(evt);
        handlePinch(evt);
        handleTripleSwipe(evt);
        handleMove(evt);
        handleRotate(evt);
        handleDrag(evt);
    }

    private boolean handleDrag(MotionEvent evt) {

        if(mPointerCount != 2) {
            return false;
        }

        Point evtPoint0 = new Point(evt.getX(0), evt.getY(0));
        Point evtPoint1 = new Point(evt.getX(1), evt.getY(1));
        Vector vector0 = new Vector(mLastPoint0, evtPoint0);
        Vector vector1 = new Vector(mLastPoint1, evtPoint1);

        if(vector0.distX() > mScaledTouchSlop || vector0.distY() > mScaledTouchSlop) {
            return false;
        }

        if(vector1.distX() > mScaledTouchSlop || vector1.distY() > mScaledTouchSlop) {

            Log.d(TAG, "Gesture: Drag by: [" + vector1.getX() + ", " + vector1.getY() + "]");

            boolean isHandled = false;

            if(mListener != null) {
                isHandled = mListener.onDrag(evt, vector1.getX(), vector1.getY());
            }

            // update last point 1
            mLastPoint1.setX(evtPoint1.getX());
            mLastPoint1.setY(evtPoint1.getY());

            return isHandled;
        }

        return false;
    }

    private boolean handleMove(MotionEvent evt) {

        if(mPointerCount != 1) {
            return false;
        }

        Point evtPoint0 = new Point(evt.getX(0), evt.getY(0));
        Vector vector0 = new Vector(mLastPoint0, evtPoint0);

        if(vector0.distX() > mScaledTouchSlop || vector0.distY() > mScaledTouchSlop) {

            // Log.d(TAG, "Gesture: Move by: [" + vector0.getX() + ", " + vector0.getY() + "]");

            boolean isHandled = false;

            if(mListener != null) {
                isHandled = mListener.onCursorMove(evt, vector0.getX(), vector0.getY());
            }

            // update last point 0
            mLastPoint0.setX(evtPoint0.getX());
            mLastPoint0.setY(evtPoint0.getY());

            return isHandled;
        }

        return false;

    }

    private boolean handleScrolling(MotionEvent evt) {

        if(mPointerCount != 2) {
            return false;
        }

        Point evtPoint0 = new Point(evt.getX(0), evt.getY(0));
        Point evtPoint1 = new Point(evt.getX(1), evt.getY(1));

        if( mPoint0.distanceFrom(mPoint1) > mScaledDoubleTapSlop
                || evtPoint0.distanceFrom(evtPoint1) > mScaledDoubleTapSlop ) {
            return false;
        }

        Vector vector0 = new Vector(mPoint0, evtPoint0);
        Vector vector1 = new Vector(mPoint1, evtPoint1);

        if( vector0.distX() > mScaledTouchSlop*2 && vector1.distX() > mScaledTouchSlop*2 && vector0.getX() * vector1.getX() > 0) {

            Log.d(TAG, "Gesture: Scrolling horizontal.");

            if(mListener != null) {
                return mListener.onScroll(evt, Direction.HORIZONTAL, (vector0.getX() + vector1.getX())/2 );
            }

        } else if( vector0.distY() > mScaledTouchSlop*2 && vector1.distY() > mScaledTouchSlop*2 && vector0.getY()*vector1.getY() > 0) {

            Log.d(TAG, "Gesture: Scrolling vertical.");

            if(mListener != null) {
                return mListener.onScroll(evt, Direction.VERTICAL, (vector0.getY() + vector1.getY())/2 );
            }
        }

        return false;
    }

    private boolean handleRotate(MotionEvent evt) {

        if(mPointerCount != 2) {
            return false;
        }

        Point evtPoint0 = new Point(evt.getX(0), evt.getY(0));
        Point evtPoint1 = new Point(evt.getX(1), evt.getY(1));

        float angle1 = (float) Math.atan2( (mPoint0.getY() - mPoint1.getY()), (mPoint0.getX() - mPoint1.getX()) );
        float angle2 = (float) Math.atan2( (evtPoint0.getY() - evtPoint1.getY()), (evtPoint0.getX() - evtPoint1.getY()) );
        float angle = ((float)Math.toDegrees(angle1 - angle2)) % 360;

        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;

        if(mStartAngle == 0) {
            mStartAngle = angle;
        } else {

            float rotationAngle = angle - mStartAngle;

            if( Math.abs(rotationAngle) > 50) {

                Log.d(TAG, "Gesture: Rotate with angle: " + rotationAngle);

                if(mListener != null) {
                    return mListener.onRotate(evt, rotationAngle);
                }
            }
        }

        return false;
    }


    private boolean handleTripleSwipe(MotionEvent evt) {

        if(mPointerCount != 3) {
            return false;
        }

        Point evtPoint0 = new Point(evt.getX(0), evt.getY(0));
        Point evtPoint1 = new Point(evt.getX(1), evt.getY(1));
        Point evtPoint2 = new Point(evt.getX(2), evt.getY(2));

        float dist01 = evtPoint0.distanceFrom(evtPoint1);
        float dist02 = evtPoint0.distanceFrom(evtPoint2);
        float dist12 = evtPoint1.distanceFrom(evtPoint2);
        if( (dist01 + dist02 + dist12) > mScaledDoubleTapSlop*4) {
            return false;
        }

        Vector vector0 = new Vector(mPoint0, evtPoint0);
        Vector vector1 = new Vector(mPoint1, evtPoint1);
        Vector vector2 = new Vector(mPoint2, evtPoint2);

        if( vector0.distX() > mScaledTouchSlop*2 && vector1.distX() > mScaledTouchSlop*2 && vector2.distX() > mScaledTouchSlop*2) {

            // TODO: check whether movement is in the same direction? all getX() are positive or all getX() are negative?

            Log.d(TAG, "Gesture: Triple swipe horizontal.");

            if(mListener != null) {
                return mListener.onTripleSwipe(evt, Direction.HORIZONTAL, (vector0.getX() + vector1.getX() + vector2.getX())/3 );
            }

        } else if( vector0.distY() > mScaledTouchSlop*2 && vector1.distY() > mScaledTouchSlop*2 && vector2.distY() > mScaledTouchSlop) {

            // TODO check whether movement is in the same direction? all getY() are positive or all getY() are negative?

            Log.d(TAG, "Gesture: Triple swipe vertical.");

            if(mListener != null) {
                return mListener.onTripleSwipe(evt, Direction.VERTICAL, (vector0.getY() + vector1.getY() + vector2.getY())/3 );
            }
        }

        return false;
    }

    private boolean handlePinch(MotionEvent evt) {

        if(mPointerCount != 2) {
            return false;
        }

        Point evtPoint0 = new Point(evt.getX(0), evt.getY(0));
        Point evtPoint1 = new Point(evt.getX(1), evt.getY(1));

        Vector vector0 = new Vector(mPoint0, evtPoint0);
        Vector vector1 = new Vector(mPoint1, evtPoint1);

        if( (vector0.getX() * vector1.getX()) > 0
                || (vector0.getY() * vector1.getY()) > 0) {
            Log.d(TAG, "Not opposite finger movement.");
            return false;
        }

        final float evtFingerDistance = evtPoint0.distanceFrom(evtPoint1);

        // 1. distance between two fingers has increased
        //    and the fingers are moving in opposite directions
        if( Math.abs(evtFingerDistance - mFingerDistance) > mScaledTouchSlop) {


            float scaleFactor = (mFingerDistance != 0) ?  (evtFingerDistance/mFingerDistance) : 1.0f;

            if( /* evtFingerDistance > mFingerDistance */ scaleFactor > 1.0f) {
                Log.d(TAG, "Gesture: Pinch out. Pinch scale factor: " + scaleFactor);

                if(mListener != null) {
                    return mListener.onPinch(evt, scaleFactor);
                }
            } else {
                Log.d(TAG, "Gesture: Pinch in. Pinch scale factor: " + scaleFactor);

                if(mListener != null) {
                    return mListener.onPinch(evt, scaleFactor);
                }
            }
        }

        return false;
    }

    private void handleLeftTap(MotionEvent evt) {

        mLeftHandler.removeCallbacksAndMessages(null);

        if( (System.currentTimeMillis() - mLeftTapDownTimestamp) > mTapTimeout) {
            mNumberOfLeftTaps = 0;
            mLastLeftTapDownTimestamp = 0;
            return;
        }

        if(mPointerCount > 1) {
            mNumberOfLeftTaps = 0;
            mLastLeftTapDownTimestamp = 0;
            return;
        }

        if(mNumberOfLeftTaps > 0 && (System.currentTimeMillis() - mLastLeftTapDownTimestamp) < mDoubleTapTimeout ) {
            mNumberOfLeftTaps++;
        } else {
            mNumberOfLeftTaps = 1;
        }

        mLastLeftTapDownTimestamp = System.currentTimeMillis();

        if(mNumberOfLeftTaps > 0) {
            MotionEvent motionEvent = MotionEvent.obtain(evt);
            mLeftHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(mNumberOfLeftTaps == 3) {
                        Log.d(TAG, "Gesture: left triple tap.");

                        if(mListener != null) {
                            mListener.onTripleTap(evt);
                        }
                    } else if( mNumberOfLeftTaps == 2) {
                        Log.d(TAG, "Gesture: left double tap.");

                        if(mListener != null) {
                            mListener.onDoubleTap(evt);
                        }
                    } else if( mNumberOfLeftTaps == 1) {
                        Log.d(TAG, "Gesture: left single tap.");

                        if(mListener != null) {
                            mListener.onSingleTap(evt);
                        }
                    }
                }
            }, mDoubleTapTimeout);
        }

        return;
    }

    private void handleRightTap(MotionEvent evt) {

        mRightHandler.removeCallbacksAndMessages(null);

        if( (System.currentTimeMillis() - mRightTapDownTimestamp) > mTapTimeout) {
            mNumberOfRightTaps = 0;
            mLastRightTapDownTimestamp = 0;
            return;
        }

        if( mPoint1.getX() - mPoint0.getX() < 0) {
            mNumberOfRightTaps = 0;
            mLastRightTapDownTimestamp = 0;
            return;
        }

        if(mNumberOfRightTaps > 0 && (System.currentTimeMillis() - mLastRightTapDownTimestamp) < mDoubleTapTimeout ) {
            mNumberOfRightTaps++;
        } else {
            mNumberOfRightTaps = 1;
        }

        mLastRightTapDownTimestamp = System.currentTimeMillis();

        if(mNumberOfRightTaps > 0) {
            MotionEvent motionEvent = MotionEvent.obtain(evt);
            mRightHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(mNumberOfRightTaps == 3) {
                        Log.d(TAG, "Gesture: right triple tap.");

                        if(mListener != null) {
                            mListener.onTripleRightTap(evt);
                        }
                    } else if( mNumberOfRightTaps == 2) {
                        Log.d(TAG, "Gesture: right double tap.");

                        if(mListener != null) {
                            mListener.onDoubleRightTap(evt);
                        }
                    } else if( mNumberOfRightTaps == 1) {
                        Log.d(TAG, "Gesture: right single tap.");

                        if(mListener != null) {
                            mListener.onSingleRightTap(evt);
                        }
                    }
                }
            }, mDoubleTapTimeout);
        }
    }

    public static class Point {

        private float mX;
        private float mY;

        public Point(float x, float y) { mX = x; mY = y; }

        public float getX() { return mX; };
        public void setX(float x) { mX = x; }
        public float getY() { return mY; };
        public void setY(float y) { mY = y; }

        public float distanceFrom(Point otherPoint) {

            // calculate norm of vector between two Points
            // V = [xi - xj, yi -yj]; |V| = sqrt( (xi - xj)^2 + (yi - yj)^2);

            // calculate vector coordinates
            final float vectorX = this.mX - otherPoint.getX();
            final float vectorY = this.mY - otherPoint.getY();

            // calculate norm of vector
            float norm = (float) Math.sqrt(vectorX*vectorX + vectorY*vectorY);

            return norm;
        }
    }

    public static class Vector {

        private Point mStartPoint;
        private Point mEndPoint;

        public Vector(Point startPoint, Point endPoint) {
            mStartPoint = startPoint;
            mEndPoint = endPoint;
        }

        public Point getStartPoint() {
            return mStartPoint;
        }

        public void setStartPoint(Point startPoint) {
            this.mStartPoint = startPoint;
        }

        public Point getEndPoint() {
            return mEndPoint;
        }

        public void setEndPoint(Point endPoint) {
            this.mEndPoint = endPoint;
        }

        public float getX() {
            return mEndPoint.getX() - mStartPoint.getX();
        }

        public float getY() {
            return mEndPoint.getY() - mStartPoint.getY();
        }

        public float distX() {
            return Math.abs(getX());
        }

        public float distY() {
            return Math.abs(getY());
        }

        // calculate norm of vector
        public float norm() {
            float norm = (float) Math.sqrt(getX()*getX() + getY()*getY());
            return norm;
        }
    }
}
