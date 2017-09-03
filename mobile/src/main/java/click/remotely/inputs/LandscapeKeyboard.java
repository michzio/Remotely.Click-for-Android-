package click.remotely.inputs;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import click.remotely.android.R;
import click.remotely.android.interfaces.RemoteControllerClientInterface;

/**
 * Created by michzio on 31/08/2017.
 */

public class LandscapeKeyboard extends AbstractKeyboard {

    public LandscapeKeyboard(ViewGroup keyboardLayout, RemoteControllerClientInterface clientInterface) {
        super(keyboardLayout, clientInterface);

        // keyboard arrows show/hide listener
        getKeyboardLayout().findViewById(R.id.keyboard_arrows_btn).setOnClickListener( (View v) -> onKeyboardArrowsButtonClicked(v) );
        getKeyboardLayout().findViewById(R.id.keyboard_hide_arrows_btn).setOnClickListener( (View v) -> onKeyboardHideArrowsButtonClicked(v) );

    }

    private void onKeyboardArrowsButtonClicked(View v) {

        FrameLayout keyboardArrowsLayout = (FrameLayout) getKeyboardLayout().findViewById(R.id.keyboard_arrows_layout);
        keyboardArrowsLayout.setVisibility(View.VISIBLE);
    }

    private void onKeyboardHideArrowsButtonClicked(View v) {

        FrameLayout keyboardArrowsLayout = (FrameLayout) getKeyboardLayout().findViewById(R.id.keyboard_arrows_layout);
        keyboardArrowsLayout.setVisibility(View.GONE);
    }
}
