package click.remotely.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.Map;

import click.remotely.android.R;
import click.remotely.model.KeyboardFlags;

/**
 * Created by michzio on 15/07/2017.
 */

public class KeyboardFragment extends Fragment {

    private static final String TAG = KeyboardFragment.class.getName();

    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    private int keyboardFlags = 0;

    private static final int[] characterKeyButtonIds = {
            R.id.keyboard_Q_btn, R.id.keyboard_W_btn, R.id.keyboard_E_btn, R.id.keyboard_R_btn,
            R.id.keyboard_T_btn, R.id.keyboard_Y_btn, R.id.keyboard_U_btn, R.id.keyboard_I_btn,
            R.id.keyboard_O_btn, R.id.keyboard_P_btn, R.id.keyboard_A_btn, R.id.keyboard_S_btn,
            R.id.keyboard_D_btn, R.id.keyboard_F_btn, R.id.keyboard_G_btn, R.id.keyboard_H_btn,
            R.id.keyboard_J_btn, R.id.keyboard_K_btn, R.id.keyboard_L_btn, R.id.keyboard_Z_btn,
            R.id.keyboard_X_btn, R.id.keyboard_C_btn, R.id.keyboard_V_btn, R.id.keyboard_B_btn,
            R.id.keyboard_N_btn, R.id.keyboard_M_btn
    };

    private static final int[] shiftableKeyButtonIds = {
            R.id.keyboard_grave_btn, R.id.keyboard_1_btn, R.id.keyboard_2_btn, R.id.keyboard_3_btn,
            R.id.keyboard_4_btn, R.id.keyboard_5_btn, R.id.keyboard_6_btn, R.id.keyboard_7_btn,
            R.id.keyboard_8_btn, R.id.keyboard_9_btn, R.id.keyboard_0_btn, R.id.keyboard_minus_btn,
            R.id.keyboard_equal_btn, R.id.keyboard_left_bracket_btn, R.id.keyboard_right_bracket_btn,
            R.id.keyboard_semicolon_btn, R.id.keyboard_comma_btn, R.id.keyboard_backslash_btn,
            R.id.keyboard_comma_btn, R.id.keyboard_period_btn, R.id.keyboard_slash_btn
    };

    private static final int[] toggleKeyButtonIds = {
            R.id.keyboard_caps_lock_btn, R.id.keyboard_left_shift_btn, R.id.keyboard_control_btn,
            R.id.keyboard_left_alt_btn, R.id.keyboard_left_command_btn, R.id.keyboard_right_command_btn,
            R.id.keyboard_right_alt_btn, R.id.keyboard_right_shift_btn
    };

    private static final int[] otherKeyButtonIds = {
            R.id.keyboard_esc_btn, R.id.keyboard_f1_btn, R.id.keyboard_f2_btn, R.id.keyboard_f3_btn,
            R.id.keyboard_f4_btn, R.id.keyboard_f5_btn, R.id.keyboard_f6_btn, R.id.keyboard_f7_btn,
            R.id.keyboard_f8_btn, R.id.keyboard_f9_btn, R.id.keyboard_f10_btn, R.id.keyboard_f11_btn,
            R.id.keyboard_f12_btn, R.id.keyboard_backspace_btn, R.id.keyboard_tab_btn, R.id.keyboard_caps_lock_btn,
            R.id.keyboard_enter_btn, R.id.keyboard_space_btn, R.id.keyboard_left_arrow_btn,
            R.id.keyboard_up_arrow_btn, R.id.keyboard_right_arrow_btn, R.id.keyboard_down_arrow_btn
    };

    private static final Map<Integer, String> keyButtonIdKeyNameMap = new HashMap<>();

    public KeyboardFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_keyboard, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            // Restore the activity's title here
            getActivity().setTitle(savedInstanceState.getCharSequence(ACTIVITY_TITLE_KEY));
        }

        bindKeyboardKeyButtonsToClickListener();
    }

    private void bindKeyboardKeyButtonsToClickListener() {

        // keyboard arrows show/hide listener
        getActivity().findViewById(R.id.keyboard_arrows_btn).setOnClickListener(keyboardArrowsBtnListener);
        getActivity().findViewById(R.id.keyboard_hide_arrows_btn).setOnClickListener(keyboardHideArrowsListener);

        // keyboard flags change listener
        ((ToggleButton) getActivity().findViewById(R.id.keyboard_caps_lock_btn)).setOnCheckedChangeListener(keyboardCapsLockBtnListener);
        ((ToggleButton) getActivity().findViewById(R.id.keyboard_left_shift_btn)).setOnCheckedChangeListener(keyboardLeftShiftBtnListener);
        ((ToggleButton) getActivity().findViewById(R.id.keyboard_right_shift_btn)).setOnCheckedChangeListener(keyboardRightShiftBtnListener);

        ((ToggleButton) getActivity().findViewById(R.id.keyboard_control_btn)).setOnCheckedChangeListener(keyboardToggleBtnListener);
        ((ToggleButton) getActivity().findViewById(R.id.keyboard_left_alt_btn)).setOnCheckedChangeListener(keyboardToggleBtnListener);
        ((ToggleButton) getActivity().findViewById(R.id.keyboard_left_command_btn)).setOnCheckedChangeListener(keyboardToggleBtnListener);
        ((ToggleButton) getActivity().findViewById(R.id.keyboard_right_command_btn)).setOnCheckedChangeListener(keyboardToggleBtnListener);
        ((ToggleButton) getActivity().findViewById(R.id.keyboard_right_alt_btn)).setOnCheckedChangeListener(keyboardToggleBtnListener);

        // keyboard key button click listener
        for(int characterKeyButtonId : characterKeyButtonIds) {
            getActivity().findViewById(characterKeyButtonId).setOnClickListener(keyboardKeyBtnClickListener);
        }
        for(int shiftableKeyButtonId : shiftableKeyButtonIds) {
            getActivity().findViewById(shiftableKeyButtonId).setOnClickListener(keyboardKeyBtnClickListener);
        }
        for(int otherKeyButtonId : otherKeyButtonIds) {
            getActivity().findViewById(otherKeyButtonId).setOnClickListener(keyboardKeyBtnClickListener);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    private View.OnClickListener keyboardArrowsBtnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            FrameLayout dimmer = (FrameLayout) getActivity().findViewById(R.id.keyboard_arrows_layout);
            dimmer.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener keyboardHideArrowsListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            FrameLayout dimmer = (FrameLayout) getActivity().findViewById(R.id.keyboard_arrows_layout);
            dimmer.setVisibility(View.GONE);
        }
    };

    private ToggleButton.OnCheckedChangeListener keyboardCapsLockBtnListener = new ToggleButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(isChecked) {
                keyboardUppercaseCharacters();
                keyboardFlags = keyboardFlags | KeyboardFlags.CAPS_LOCK.value();
            } else {
                keyboardFlags = keyboardFlags & ~(KeyboardFlags.CAPS_LOCK.value());
                keyboardLowercaseCharacters();
            }
        }
    };

    private void keyboardUppercaseCharacters() {

        for(int characterKeyButtonId : characterKeyButtonIds) {
            ((Button) getActivity().findViewById(characterKeyButtonId)).setAllCaps(true);
        }
    }

    private void keyboardLowercaseCharacters() {

        // check whether all keyboard flags enforcing Uppercase characters are unchecked
        if((keyboardFlags & (KeyboardFlags.CAPS_LOCK.value()
                            | KeyboardFlags.LEFT_SHIFT.value()
                            | KeyboardFlags.RIGHT_SHIFT.value()) ) > 0) {
            return;
        }

        for(int characterKeyButtonId : characterKeyButtonIds) {
            ((Button) getActivity().findViewById(characterKeyButtonId)).setAllCaps(false);
        }
    }

    private ToggleButton.OnCheckedChangeListener keyboardLeftShiftBtnListener = new ToggleButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(isChecked) {
                keyboardUppercaseCharacters();
                keyboardShiftKeys();
                keyboardFlags = keyboardFlags | KeyboardFlags.LEFT_SHIFT.value();
            } else {
                keyboardFlags = keyboardFlags & ~(KeyboardFlags.LEFT_SHIFT.value());
                keyboardLowercaseCharacters();
                keyboardUnshiftKeys();
            }
        }
    };

    private ToggleButton.OnCheckedChangeListener keyboardRightShiftBtnListener = new ToggleButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(isChecked) {
                keyboardUppercaseCharacters();
                keyboardShiftKeys();
                keyboardFlags = keyboardFlags | KeyboardFlags.RIGHT_SHIFT.value();
            } else {
                keyboardFlags = keyboardFlags & ~(KeyboardFlags.RIGHT_SHIFT.value());
                keyboardLowercaseCharacters();
                keyboardUnshiftKeys();
            }
        }
    };

    private void keyboardShiftKeys() {

        for(int shiftableKeyButtonId : shiftableKeyButtonIds) {
            Button button = (Button) getActivity().findViewById(shiftableKeyButtonId);
            String tag = (String) button.getTag();
            String[] shiftableKeys = getResources().getStringArray(Integer.parseInt(tag.substring(1)));
            button.setText(shiftableKeys[1]);
        }
    }

    private void keyboardUnshiftKeys() {

        // check whether both right and left shift buttons are unchecked
        if((keyboardFlags & (KeyboardFlags.LEFT_SHIFT.value() | KeyboardFlags.RIGHT_SHIFT.value()) ) > 0) {
            return;
        }

        for(int shiftableKeyButtonId : shiftableKeyButtonIds) {
            Button button = (Button) getActivity().findViewById(shiftableKeyButtonId);
            String tag = (String) button.getTag();
            String[] shiftableKeys = getResources().getStringArray(Integer.parseInt(tag.substring(1)));
            button.setText(shiftableKeys[0]);
        }
    }

    private ToggleButton.OnCheckedChangeListener keyboardToggleBtnListener = new ToggleButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            int toggleBtnFlag = 0;

            switch(buttonView.getId()) {
                case R.id.keyboard_control_btn:
                    toggleBtnFlag = KeyboardFlags.LEFT_CTRL.value();
                    break;
                case R.id.keyboard_left_alt_btn:
                    toggleBtnFlag = KeyboardFlags.LEFT_ALT.value();
                    break;
                case R.id.keyboard_left_command_btn:
                    toggleBtnFlag = KeyboardFlags.LEFT_CMD.value();
                    break;
                case R.id.keyboard_right_command_btn:
                    toggleBtnFlag = KeyboardFlags.RIGHT_CMD.value();
                    break;
                case R.id.keyboard_right_alt_btn:
                    toggleBtnFlag = KeyboardFlags.RIGHT_ALT.value();
                    break;
                default:
                    toggleBtnFlag = 0;
                    break;
            }

            if(isChecked) {
                keyboardFlags = keyboardFlags | toggleBtnFlag;
            } else {
                keyboardFlags = keyboardFlags & ~(toggleBtnFlag);
            }
        }
    };

    private View.OnClickListener keyboardKeyBtnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Button keyButton = (Button) v;
            int keyBtnId = keyButton.getId();
            //String text = keyButton.getText().toString();

            StringBuilder flagsBuilder = new StringBuilder();
            if((keyboardFlags & KeyboardFlags.CAPS_LOCK.value()) > 0) {
                flagsBuilder.append("CAPS LOCK");
                flagsBuilder.append(", ");
            }
            if((keyboardFlags & KeyboardFlags.LEFT_SHIFT.value()) > 0) {
                flagsBuilder.append("LEFT SHIFT");
                flagsBuilder.append(", ");
            }
            if((keyboardFlags & KeyboardFlags.LEFT_CTRL.value()) > 0) {
                flagsBuilder.append("LEFT CTRL");
                flagsBuilder.append(", ");
            }
            if((keyboardFlags & KeyboardFlags.LEFT_ALT.value()) > 0) {
                flagsBuilder.append("LEFT ALT");
                flagsBuilder.append(", ");
            }
            if((keyboardFlags & KeyboardFlags.LEFT_CMD.value()) > 0) {
                flagsBuilder.append("LEFT CMD");
                flagsBuilder.append(", ");
            }
            if((keyboardFlags & KeyboardFlags.RIGHT_CMD.value()) > 0) {
                flagsBuilder.append("RIGHT CMD");
                flagsBuilder.append(", ");
            }
            if((keyboardFlags & KeyboardFlags.RIGHT_ALT.value()) > 0) {
                flagsBuilder.append("RIGHT ALT");
                flagsBuilder.append(", ");
            }
            if((keyboardFlags & KeyboardFlags.RIGHT_SHIFT.value()) > 0) {
                flagsBuilder.append("RIGHT SHIFT");
                flagsBuilder.append(", ");
            }

            String virtualKeyName = keyButtonIdKeyNameMap.get(keyBtnId);

            Toast.makeText(getActivity(), "Key input: " + virtualKeyName + " with flags: " + flagsBuilder.toString(), Toast.LENGTH_LONG).show();
        }
    };

    static {

        keyButtonIdKeyNameMap.put(R.id.keyboard_esc_btn,    "kVK_Escape");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f1_btn,     "kVK_F1");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f2_btn,     "kVK_F2");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f3_btn,     "kVK_F3");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f4_btn,     "kVK_F4");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f5_btn,     "kVK_F5");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f6_btn,     "kVK_F6");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f7_btn,     "kVK_F7");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f8_btn,     "kVK_F8");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f9_btn,     "kVK_F9");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f10_btn,    "kVK_F10");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f11_btn,    "kVK_F11");
        keyButtonIdKeyNameMap.put(R.id.keyboard_f12_btn,    "kVK_F12");
        keyButtonIdKeyNameMap.put(R.id.keyboard_grave_btn,  "kVK_ANSI_Grave");
        keyButtonIdKeyNameMap.put(R.id.keyboard_1_btn,      "kVK_ANSI_1");
        keyButtonIdKeyNameMap.put(R.id.keyboard_2_btn,      "kVK_ANSI_2");
        keyButtonIdKeyNameMap.put(R.id.keyboard_3_btn,      "kVK_ANSI_3");
        keyButtonIdKeyNameMap.put(R.id.keyboard_4_btn,      "kVK_ANSI_4");
        keyButtonIdKeyNameMap.put(R.id.keyboard_5_btn,      "kVK_ANSI_5");
        keyButtonIdKeyNameMap.put(R.id.keyboard_6_btn,      "kVK_ANSI_6");
        keyButtonIdKeyNameMap.put(R.id.keyboard_7_btn,      "kVK_ANSI_7");
        keyButtonIdKeyNameMap.put(R.id.keyboard_8_btn,      "kVK_ANSI_8");
        keyButtonIdKeyNameMap.put(R.id.keyboard_9_btn,      "kVK_ANSI_9");
        keyButtonIdKeyNameMap.put(R.id.keyboard_0_btn,      "kVK_ANSI_0");
        keyButtonIdKeyNameMap.put(R.id.keyboard_minus_btn,  "kVK_ANSI_Minus");
        keyButtonIdKeyNameMap.put(R.id.keyboard_equal_btn,  "kVK_ANSI_Equal");
        keyButtonIdKeyNameMap.put(R.id.keyboard_backspace_btn,"kVK_Backspace");
        keyButtonIdKeyNameMap.put(R.id.keyboard_tab_btn,    "kVK_Tab");
        keyButtonIdKeyNameMap.put(R.id.keyboard_Q_btn,      "kVK_ANSI_Q");
        keyButtonIdKeyNameMap.put(R.id.keyboard_W_btn,      "kVK_ANSI_W");
        keyButtonIdKeyNameMap.put(R.id.keyboard_E_btn,      "kVK_ANSI_E");
        keyButtonIdKeyNameMap.put(R.id.keyboard_R_btn,      "kVK_ANSI_R");
        keyButtonIdKeyNameMap.put(R.id.keyboard_T_btn,      "kVK_ANSI_T");
        keyButtonIdKeyNameMap.put(R.id.keyboard_Y_btn,      "kVK_ANSI_Y");
        keyButtonIdKeyNameMap.put(R.id.keyboard_U_btn,      "kVK_ANSI_U");
        keyButtonIdKeyNameMap.put(R.id.keyboard_I_btn,      "kVK_ANSI_I");
        keyButtonIdKeyNameMap.put(R.id.keyboard_O_btn,      "kVK_ANSI_O");
        keyButtonIdKeyNameMap.put(R.id.keyboard_P_btn,      "kVK_ANSI_P");
        keyButtonIdKeyNameMap.put(R.id.keyboard_left_bracket_btn, "kVK_ANSI_LeftBracket");
        keyButtonIdKeyNameMap.put(R.id.keyboard_right_bracket_btn, "kVK_ANSI_RightBracket");
        keyButtonIdKeyNameMap.put(R.id.keyboard_backslash_btn, "kVK_ANSI_Backslash");
        keyButtonIdKeyNameMap.put(R.id.keyboard_caps_lock_btn, "kVK_CapsLock");
        keyButtonIdKeyNameMap.put(R.id.keyboard_A_btn,      "kVK_ANSI_A");
        keyButtonIdKeyNameMap.put(R.id.keyboard_S_btn,      "kVK_ANSI_S");
        keyButtonIdKeyNameMap.put(R.id.keyboard_D_btn,      "kVK_ANSI_D");
        keyButtonIdKeyNameMap.put(R.id.keyboard_F_btn,      "kVK_ANSI_F");
        keyButtonIdKeyNameMap.put(R.id.keyboard_G_btn,      "kVK_ANSI_G");
        keyButtonIdKeyNameMap.put(R.id.keyboard_H_btn,      "kVK_ANSI_H");
        keyButtonIdKeyNameMap.put(R.id.keyboard_J_btn,      "kVK_ANSI_J");
        keyButtonIdKeyNameMap.put(R.id.keyboard_K_btn,      "kVK_ANSI_K");
        keyButtonIdKeyNameMap.put(R.id.keyboard_L_btn,      "kVK_ANSI_L");
        keyButtonIdKeyNameMap.put(R.id.keyboard_semicolon_btn, "kVK_ANSI_Semicolon");
        keyButtonIdKeyNameMap.put(R.id.keyboard_quote_btn,  "kVK_ANSI_Quote");
        keyButtonIdKeyNameMap.put(R.id.keyboard_enter_btn,  "kVK_Return");
        keyButtonIdKeyNameMap.put(R.id.keyboard_Z_btn,      "kVK_ANSI_Z");
        keyButtonIdKeyNameMap.put(R.id.keyboard_X_btn,      "kVK_ANSI_X");
        keyButtonIdKeyNameMap.put(R.id.keyboard_C_btn,      "kVK_ANSI_C");
        keyButtonIdKeyNameMap.put(R.id.keyboard_V_btn,      "kVK_ANSI_V");
        keyButtonIdKeyNameMap.put(R.id.keyboard_B_btn,      "kVK_ANSI_B");
        keyButtonIdKeyNameMap.put(R.id.keyboard_N_btn,      "kVK_ANSI_N");
        keyButtonIdKeyNameMap.put(R.id.keyboard_M_btn,      "kVK_ANSI_M");
        keyButtonIdKeyNameMap.put(R.id.keyboard_comma_btn,  "kVK_ANSI_Comma");
        keyButtonIdKeyNameMap.put(R.id.keyboard_period_btn, "kVK_ANSI_Period");
        keyButtonIdKeyNameMap.put(R.id.keyboard_slash_btn,  "kVK_ANSI_Slash");
        keyButtonIdKeyNameMap.put(R.id.keyboard_space_btn,  "kVK_Space");
        keyButtonIdKeyNameMap.put(R.id.keyboard_left_arrow_btn, "kVK_LeftArrow");
        keyButtonIdKeyNameMap.put(R.id.keyboard_up_arrow_btn, "kVK_UpArrow");
        keyButtonIdKeyNameMap.put(R.id.keyboard_right_arrow_btn, "kVK_RightArrow");
        keyButtonIdKeyNameMap.put(R.id.keyboard_down_arrow_btn, "kVK_DownArrow");
    }
}
