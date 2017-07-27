package click.remotely.inputs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import click.remotely.android.R;

/**
 * Created by michzio on 03/07/2017.
 */

public class TouchpadView extends View implements OnTouchListener {

    private static final String TAG = TouchpadView.class.getName();

    private int mPaintColor;
    private float mStrokeWidth;

    private Paint mPaint;
    private Path mPath;

    private RectF mDirtyRect = new RectF();

    public TouchpadView(Context context) {
        this(context, null);
    }

    public TouchpadView(Context context, AttributeSet attrs) {

        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TouchpadView, 0, 0);
        mPaintColor = a.getColor(R.styleable.TouchpadView_paintColor, Color.WHITE);
        mStrokeWidth = a.getFloat(R.styleable.TouchpadView_strokeWidth, 10f);

        // configure Paint object
        mPaint = new Paint();
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);

        // create Path object to track user finger motion
        mPath = new Path();

        // set this as Touch event listener
        setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch(event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(event.getX(), event.getY());
                resetDirtyRect(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                // draw path using history of touch events
                // android can skip some touch events if there is many of them
                for(int i=0; i < event.getHistorySize(); i++) {
                    resizeDirtyRect(event.getHistoricalX(i), event.getHistoricalY(i));
                    mPath.lineTo(event.getHistoricalX(i), event.getHistoricalY(i));
                }
                // draw path to the position of the last touch event
                mPath.lineTo(event.getX(), event.getY());
                resizeDirtyRect(event.getX(), event.getY());

                // enforce redrawing TouchPad View dirty rectangle
                invalidate( (int) (mDirtyRect.left - mPaint.getStrokeWidth()/2),
                            (int) (mDirtyRect.top - mPaint.getStrokeWidth()/2),
                            (int) (mDirtyRect.right - mPaint.getStrokeWidth()/2),
                            (int) (mDirtyRect.bottom - mPaint.getStrokeWidth()/2)
                );

                // after redrawing reset dirty rectangle
                resetDirtyRect(event.getX(), event.getY());

                return false;
            default:
                Log.d(TouchpadView.TAG, "Incorrect touch event action type " + event.toString());
                return false;
        }
    }

    private void resizeDirtyRect(float touchMoveX, float touchMoveY) {

        // resize horizontally
        if(touchMoveX < mDirtyRect.left) {
            mDirtyRect.left = touchMoveX;
        } else if(touchMoveX > mDirtyRect.right) {
            mDirtyRect.right = touchMoveX;
        }

        // resize vertically
        if(touchMoveY < mDirtyRect.top) {
            mDirtyRect.top = touchMoveY;
        } else if(touchMoveY > mDirtyRect.bottom) {
            mDirtyRect.bottom = touchMoveY;
        }
    }

    private  void resetDirtyRect(float touchDownX, float touchDownY) {
        mDirtyRect.left = touchDownX;
        mDirtyRect.right = touchDownX;
        mDirtyRect.top = touchDownY;
        mDirtyRect.bottom = touchDownY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }
}
