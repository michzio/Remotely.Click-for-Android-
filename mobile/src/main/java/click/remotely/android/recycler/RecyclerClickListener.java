package click.remotely.android.recycler;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by michzio on 08/08/2017.
 */

public class RecyclerClickListener implements RecyclerView.OnItemTouchListener {

    public static interface OnItemClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;
    private GestureDetectorCompat mGestureDetector;

    private RecyclerView mRecyclerView;

    public RecyclerClickListener(Context context, final RecyclerView recyclerView, final OnItemClickListener listener) {

        this.mRecyclerView = recyclerView;

        this.mOnItemClickListener = listener;
        this.mGestureDetector = new GestureDetectorCompat(context, gestureListener);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());

        if(childView != null && mOnItemClickListener != null && mGestureDetector.onTouchEvent(e)) {
            mOnItemClickListener.onClick(childView, mRecyclerView.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

            View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());

            if(childView != null && mOnItemClickListener != null) {
                mOnItemClickListener.onLongClick(childView, mRecyclerView.getChildAdapterPosition(childView));
            }
        }
    };
}
