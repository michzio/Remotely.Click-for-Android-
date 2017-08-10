package click.remotely.android.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import click.remotely.android.R;
import click.remotely.android.RemoteDevicesActivity;
import click.remotely.model.RemoteDevicesRecyclerAdapter;

/**
 * Created by michzio on 09/08/2017.
 */

public class SwipeDeleteHelper extends ItemTouchHelper.SimpleCallback {

    public static interface AdapterInterface {
        public boolean isUndoOn();
        public boolean isItemRemovable(int position);
        public boolean isPendingRemoval(int position);
        public void pendingRemoval(int position);
        public void remove(int position);
    }

    private Context mContext;
    private AdapterInterface mAdapter;

    // hold this references to not allocate them repeatedly in the onChildDraw method
    private Drawable mDeleteBackground;
    private Drawable mRightClearIcon;
    private Drawable mLeftClearIcon;
    private int mClearIconMargin;

    public SwipeDeleteHelper(Context context, AdapterInterface adapterInterface) {

        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);

        mContext = context;
        mAdapter = adapterInterface;

        // Use default values
        Drawable deleteBackground = new ColorDrawable(ContextCompat.getColor(mContext, R.color.colorPowerRed));
        Drawable leftClearIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_sweep_left_black_24dp);
        leftClearIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        Drawable rightClearIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_sweep_right_black_24dp);
                 rightClearIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        int clearIconMargin = (int) mContext.getResources().getDimension(R.dimen.swipeDeleteClearIconMargin);

        mDeleteBackground = deleteBackground;
        mLeftClearIcon = leftClearIcon;
        mRightClearIcon = rightClearIcon;
        mClearIconMargin = clearIconMargin;

    }

    public SwipeDeleteHelper(Context context, AdapterInterface adapterInterface, Drawable deleteBackground, Drawable leftClearIcon, Drawable rightClearIcon, int clearIconMargin) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);

        mContext = context;
        mAdapter = adapterInterface;
        mDeleteBackground = deleteBackground;
        mLeftClearIcon = leftClearIcon;
        mRightClearIcon = rightClearIcon;
        mClearIconMargin = clearIconMargin;
    }

    public void setDeleteBackground(Drawable deleteBackground) {
        mDeleteBackground = deleteBackground;
    }

    public void setLeftClearIcon(Drawable leftClearIcon) {
        mLeftClearIcon = leftClearIcon;
    }

    public void setmRightClearIcon(Drawable rightClearIcon) {
        mRightClearIcon = rightClearIcon;
    }

    public void setClearIconMargin(int clearIconMargin) {
        mClearIconMargin = clearIconMargin;
    }

    /**
     * This method enables to implement drag & drop functionality
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        int position = viewHolder.getAdapterPosition();

        if( !mAdapter.isItemRemovable(viewHolder.getAdapterPosition()) ) {
            return 0;
        }

        if(mAdapter.isUndoOn() && mAdapter.isPendingRemoval(position)) {
            return 0;
        }

        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        int position = viewHolder.getAdapterPosition();

        if(mAdapter.isUndoOn()) {
            mAdapter.pendingRemoval(position);
        } else {
            mAdapter.remove(position);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;

        // not interested in items that are already swiped away
        if(viewHolder.getAdapterPosition() < 0) {
            return;
        }

        if(dX < 0) {   // swipe LEFT

            // draw delete background to the right of swiped to the left side recycler view's item
            mDeleteBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            mDeleteBackground.draw(c);

            // calculate right clear icon bounds
            int itemViewHeight = itemView.getBottom() - itemView.getTop(); // itemView.getHeight();
            int clearIconInnerWidth = mRightClearIcon.getIntrinsicWidth();
            int clearIconInnerHeight = mRightClearIcon.getIntrinsicHeight();
            int clearIconLeft = itemView.getRight() - mClearIconMargin - clearIconInnerWidth;
            int clearIconRight = itemView.getRight() - mClearIconMargin;
            int clearIconTop = itemView.getTop() + (itemViewHeight - clearIconInnerHeight) / 2;
            int clearIconBottom = clearIconTop + clearIconInnerHeight;

            // draw clear icon
            mRightClearIcon.setBounds(clearIconLeft, clearIconTop, clearIconRight, clearIconBottom);
            mRightClearIcon.draw(c);

        } else if(dX > 0) {  // swipe RIGHT

            // draw delete background to the left of swiped to the right side recycler view's item
            mDeleteBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
            mDeleteBackground.draw(c);

            // calculate left clear icon bounds
            int itemViewHeight = itemView.getBottom() - itemView.getTop(); // itemView.getHeight();
            int clearIconInnerWidth = mLeftClearIcon.getIntrinsicWidth();
            int clearIconInnerHeight = mLeftClearIcon.getIntrinsicHeight();
            int clearIconLeft = itemView.getLeft() + mClearIconMargin;
            int clearIconRight = itemView.getLeft() + mClearIconMargin + clearIconInnerWidth;
            int clearIconTop = itemView.getTop() + (itemViewHeight - clearIconInnerHeight) / 2;
            int clearIconBottom = clearIconTop + clearIconInnerHeight;

            // draw clear icon
            mLeftClearIcon.setBounds(clearIconLeft, clearIconTop, clearIconRight, clearIconBottom);
            mLeftClearIcon.draw(c);
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
