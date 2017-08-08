package click.remotely.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by michzio on 07/08/2017.
 */

public class Utils {

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public static void showSlideDownAnimatingView(View view) {

        // prepare view for the animation
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);

        // start view show slide down animation
        view.animate()
                .translationY(view.getHeight())
                .alpha(1.0f)
                .setDuration(2000)
                .setListener(null);
    }

    public static void hideSlideUpAnimatingView(View view) {

        view.animate()
                .translationY(0)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }
}
