package click.remotely.android.recycler;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by michzio on 09/08/2017.
 */

public class SwipeDeleteBackgroundItemDecoration extends RecyclerView.ItemDecoration {

    // hold this references to not allocate them repeatedly in the onDraw method
    private Drawable mDeleteBackground;

    public SwipeDeleteBackgroundItemDecoration(Drawable deleteBackground) {

        mDeleteBackground = deleteBackground;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        // if animation is in progress
        if(parent.getItemAnimator().isRunning()) {

            // some items might be animating down and some items might be animating up
            // to close the gap left by the removed item, this is not exclusive,
            // both movement can be happening at the same time
            // to reproduce this leave just enough items so the first one and the last one
            // would be just a little off screen, then remove one from the middle

            // find first child item with translationY > 0
            // and last child item with translationY < 0
            View firstViewComingUp = null;
            View lastViewComingDown = null;

            // this is fixed
            int left = 0;
            int right = parent.getWidth();

            // this need to be calculated
            int top = 0;
            int bottom = 0;

            // find relevant translating views
            int childCount = parent.getLayoutManager().getChildCount();
            for(int i=0; i<childCount; i++) {

                View child = parent.getLayoutManager().getChildAt(i);
                if(child.getTranslationY() < 0) {
                    // view is coming down
                    lastViewComingDown = child;
                } else if(child.getTranslationY() > 0) {
                    // view is coming up
                    if(firstViewComingUp == null) {
                        firstViewComingUp = child;
                    }
                }
            }

            // calculate top & bottom values depending on views movement
            if(lastViewComingDown != null && firstViewComingUp != null) {
                // views are coming down and going up to fill the void
                top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
            } else if(lastViewComingDown != null) {
                // views are going down to fill the void
                top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                bottom = lastViewComingDown.getBottom();
            } else if(firstViewComingUp != null) {
                // views are going up to fill the void
                top = firstViewComingUp.getTop();
                bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
            }


            mDeleteBackground.setBounds(left, top, right, bottom);
            mDeleteBackground.draw(c);
        }

        super.onDraw(c, parent, state);
    }
}
