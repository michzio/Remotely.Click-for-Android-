package click.remotely.inputs;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import click.remotely.android.R;
import click.remotely.android.interfaces.RemoteControllerClientInterface;

/**
 * Created by michzio on 30/08/2017.
 */

public class BottomSheetKeyboard extends AbstractKeyboard{

    private static final int[] alphanumericKeyboardLayoutKeys = {
            R.id.keyboard_1_btn, R.id.keyboard_2_btn, R.id.keyboard_3_btn, R.id.keyboard_4_btn,
            R.id.keyboard_5_btn, R.id.keyboard_6_btn, R.id.keyboard_7_btn, R.id.keyboard_8_btn,
            R.id.keyboard_9_btn, R.id.keyboard_0_btn, R.id.keyboard_Q_btn, R.id.keyboard_W_btn,
            R.id.keyboard_E_btn, R.id.keyboard_R_btn, R.id.keyboard_T_btn, R.id.keyboard_Y_btn,
            R.id.keyboard_U_btn, R.id.keyboard_I_btn, R.id.keyboard_O_btn, R.id.keyboard_P_btn,
            R.id.keyboard_A_btn, R.id.keyboard_S_btn, R.id.keyboard_D_btn, R.id.keyboard_F_btn,
            R.id.keyboard_G_btn, R.id.keyboard_H_btn, R.id.keyboard_J_btn, R.id.keyboard_K_btn,
            R.id.keyboard_L_btn, R.id.keyboard_Z_btn, R.id.keyboard_X_btn, R.id.keyboard_C_btn,
            R.id.keyboard_V_btn, R.id.keyboard_B_btn, R.id.keyboard_N_btn, R.id.keyboard_M_btn,
    };

    private static final int[] symbolsKeyboardLayoutKeys = {
            R.id.keyboard_f1_btn, R.id.keyboard_f2_btn, R.id.keyboard_f3_btn, R.id.keyboard_f4_btn,
            R.id.keyboard_f5_btn, R.id.keyboard_f6_btn, R.id.keyboard_f7_btn, R.id.keyboard_f8_btn,
            R.id.keyboard_f5_btn, R.id.keyboard_f6_btn, R.id.keyboard_f7_btn, R.id.keyboard_f8_btn,
            R.id.keyboard_f9_btn, R.id.keyboard_f10_btn, R.id.keyboard_f11_btn, R.id.keyboard_f12_btn,
            R.id.function_keys_extra_row, R.id.keyboard_grave_btn, R.id.keyboard_minus_btn, R.id.keyboard_equal_btn,
            R.id.keyboard_left_bracket_btn, R.id.keyboard_right_bracket_btn, R.id.keyboard_backslash_btn,
            R.id.keyboard_semicolon_btn, R.id.keyboard_quote_btn, R.id.keyboard_comma_btn, R.id.keyboard_slash_btn,
            R.id.keyboard_left_arrow_btn, R.id.keyboard_right_arrow_btn, R.id.keyboard_down_arrow_btn, R.id.keyboard_up_arrow_btn,
            R.id.keyboard_esc_btn, R.id.keyboard_tab_btn, R.id.keyboard_caps_lock_btn, R.id.keyboard_left_alt_btn,
            R.id.keyboard_left_command_btn, R.id.keyboard_control_btn,
    };

    public BottomSheetKeyboard(ViewGroup keyboardLayout, RemoteControllerClientInterface clientInterface) {
        super(keyboardLayout, clientInterface);

        ToggleButton keyboardLayoutToggleButton = (ToggleButton) getKeyboardLayout().findViewById(R.id.keyboard_layout_switch);
        keyboardLayoutToggleButton.setOnCheckedChangeListener( (CompoundButton buttonView, boolean isChecked) -> switchKeyboardLayout(isChecked));
    }

    private void switchKeyboardLayout(boolean isKeyboardLayoutToggled) {

        if(isKeyboardLayoutToggled) {
            showKeys(symbolsKeyboardLayoutKeys);
            hideKeys(alphanumericKeyboardLayoutKeys);
        } else {
            hideKeys(symbolsKeyboardLayoutKeys);
            showKeys(alphanumericKeyboardLayoutKeys);
        }
    }

    private void showKeys(int[] keyIds) {

        for(int keyId : keyIds) {
            View keyView = (View) getKeyboardLayout().findViewById(keyId);
            keyView.setVisibility(View.VISIBLE);
        }
    }

    private void hideKeys(int[] keyIds) {
        for(int keyId : keyIds) {
            View keyView = (View) getKeyboardLayout().findViewById(keyId);
            keyView.setVisibility(View.GONE);
        }
    }
}
