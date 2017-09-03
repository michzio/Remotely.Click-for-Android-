package click.remotely.inputs;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.Map;

import click.remotely.android.R;
import click.remotely.android.interfaces.RemoteControllerClientInterface;
import click.remotely.android.services.RemoteControllerClientService;
import click.remotely.model.KeyboardFlags;

/**
 * Created by michzio on 31/08/2017.
 */

public abstract class AbstractKeyboard {

    private static final String TAG = AbstractKeyboard.class.getName();

    private RemoteControllerClientInterface mClientInterface;
    private ViewGroup mKeyboardLayout;
    private int mKeyboardFlags = 0;

    public AbstractKeyboard(ViewGroup keyboardLayout, RemoteControllerClientInterface clientInterface) {
        mKeyboardLayout = keyboardLayout;
        mClientInterface = clientInterface;

        // keyboard flags checked change listener
        ToggleButton keyboardCapsLockButton = (ToggleButton) mKeyboardLayout.findViewById(R.id.keyboard_caps_lock_btn);
        if(keyboardCapsLockButton != null) {
            keyboardCapsLockButton.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> onCapsLockKeyCheckedChange(isChecked));
        }

        ToggleButton keyboardLeftShiftButton = (ToggleButton) mKeyboardLayout.findViewById(R.id.keyboard_left_shift_btn);
        if(keyboardLeftShiftButton != null) {
            keyboardLeftShiftButton.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> onLeftShiftKeyCheckedChange(isChecked));
        }

        ToggleButton keyboardRightShiftButton = (ToggleButton) mKeyboardLayout.findViewById(R.id.keyboard_right_shift_btn);
        if(keyboardRightShiftButton != null) {
            keyboardRightShiftButton.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> onRightShiftKeyCheckedChange(isChecked));
        }

        ToggleButton keyboardControlButton = (ToggleButton) mKeyboardLayout.findViewById(R.id.keyboard_control_btn);
        if(keyboardControlButton != null) {
            keyboardControlButton.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> onKeyboardToggleButtonCheckedChange(buttonView, isChecked));
        }

        ToggleButton keyboardLeftAltButton = (ToggleButton) mKeyboardLayout.findViewById(R.id.keyboard_left_alt_btn);
        if(keyboardLeftAltButton != null) {
            keyboardLeftAltButton.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> onKeyboardToggleButtonCheckedChange(buttonView, isChecked));
        }

        ToggleButton keyboardLeftCommandButton = (ToggleButton) mKeyboardLayout.findViewById(R.id.keyboard_left_command_btn);
        if(keyboardLeftCommandButton != null) {
            keyboardLeftCommandButton.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> onKeyboardToggleButtonCheckedChange(buttonView, isChecked));
        }

        ToggleButton keyboardRightCommandButton = (ToggleButton) mKeyboardLayout.findViewById(R.id.keyboard_right_command_btn);
        if(keyboardRightCommandButton != null) {
            keyboardRightCommandButton.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> onKeyboardToggleButtonCheckedChange(buttonView, isChecked));
        }

        ToggleButton keyboardRightAltButton = (ToggleButton) mKeyboardLayout.findViewById(R.id.keyboard_right_alt_btn);
        if(keyboardRightAltButton != null) {
            keyboardRightAltButton.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> onKeyboardToggleButtonCheckedChange(buttonView, isChecked));
        }

        // keyboard key button click listener
        for(int characterKeyButtonId : characterKeyButtonIds()) {
            mKeyboardLayout.findViewById(characterKeyButtonId).setOnClickListener( (View v) -> onKeyButtonClicked(v) );
        }
        for(int shiftableKeyButtonId : shiftableKeyButtonIds()) {
            mKeyboardLayout.findViewById(shiftableKeyButtonId).setOnClickListener( (View v) -> onKeyButtonClicked(v) );
        }
        for(int otherKeyButtonId : otherKeyButtonIds()) {
            mKeyboardLayout.findViewById(otherKeyButtonId).setOnClickListener( (View v) -> onKeyButtonClicked(v) );
        }
    }

    protected ViewGroup getKeyboardLayout() {
        return mKeyboardLayout;
    }

    private void onCapsLockKeyCheckedChange(boolean isChecked) {

        if(isChecked) {
            keyboardToUppercaseCharacters();
            mKeyboardFlags = mKeyboardFlags | KeyboardFlags.CAPS_LOCK.value();
        } else {
            mKeyboardFlags = mKeyboardFlags & ~(KeyboardFlags.CAPS_LOCK.value());
            keyboardToLowercaseCharacters();
        }
    }

    private void keyboardToUppercaseCharacters() {

        for(int characterKeyButtonId : characterKeyButtonIds()) {
            ((Button) mKeyboardLayout.findViewById(characterKeyButtonId)).setAllCaps(true);
        }
    }

    private void keyboardToLowercaseCharacters() {

        // check whether all keyboard flags enforcing Uppercase characters are unchecked
        if((mKeyboardFlags & (KeyboardFlags.CAPS_LOCK.value()
                | KeyboardFlags.LEFT_SHIFT.value()
                | KeyboardFlags.RIGHT_SHIFT.value()) ) > 0) {
            return;
        }

        for(int characterKeyButtonId : characterKeyButtonIds()) {
            ((Button) mKeyboardLayout.findViewById(characterKeyButtonId)).setAllCaps(false);
        }
    }

    private void onLeftShiftKeyCheckedChange(boolean isChecked) {

        if(isChecked) {
            keyboardToUppercaseCharacters();
            keyboardShiftSymbolKeys();
            mKeyboardFlags = mKeyboardFlags | KeyboardFlags.LEFT_SHIFT.value();
        } else {
            mKeyboardFlags = mKeyboardFlags & ~(KeyboardFlags.LEFT_SHIFT.value());
            keyboardUnshiftSymbolKeys();
            keyboardToLowercaseCharacters();
        }
    }

    private void onRightShiftKeyCheckedChange(boolean isChecked) {

        if(isChecked) {
            keyboardToUppercaseCharacters();
            keyboardShiftSymbolKeys();
            mKeyboardFlags = mKeyboardFlags | KeyboardFlags.RIGHT_SHIFT.value();
        } else {
            mKeyboardFlags = mKeyboardFlags & ~(KeyboardFlags.RIGHT_SHIFT.value());
            keyboardUnshiftSymbolKeys();
            keyboardToLowercaseCharacters();
        }
    }

    private void keyboardShiftSymbolKeys() {

        for(int shiftableKeyButtonId : shiftableKeyButtonIds()) {
            Button button = (Button) mKeyboardLayout.findViewById(shiftableKeyButtonId);
            String tag = (String) button.getTag();
            String[] shiftableKeys = mKeyboardLayout.getResources().getStringArray(Integer.parseInt(tag.substring(1)));
            button.setText(shiftableKeys[1]);
        }
    }

    private void keyboardUnshiftSymbolKeys() {

        // check whether both right and left shift buttons are unchecked
        if((mKeyboardFlags & (KeyboardFlags.LEFT_SHIFT.value() | KeyboardFlags.RIGHT_SHIFT.value()) ) > 0) {
            return;
        }

        for(int shiftableKeyButtonId : shiftableKeyButtonIds()) {
            Button button = (Button) mKeyboardLayout.findViewById(shiftableKeyButtonId);
            String tag = (String) button.getTag();
            String[] shiftableKeys = mKeyboardLayout.getResources().getStringArray(Integer.parseInt(tag.substring(1)));
            button.setText(shiftableKeys[0]);
        }
    }

    private void onKeyboardToggleButtonCheckedChange(CompoundButton buttonView, boolean isChecked) {

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
            mKeyboardFlags = mKeyboardFlags | toggleBtnFlag;
        } else {
            mKeyboardFlags = mKeyboardFlags & ~(toggleBtnFlag);
        }
    }

    private void onKeyButtonClicked(View v) {

        Button keyButton = (Button) v;
        String virtualKeyName = keyButtonIdKeyNameMap.get(keyButton.getId());
        //String keyText = keyButton.getText().toString();

        StringBuilder flagsBuilder = new StringBuilder();
        if((mKeyboardFlags & KeyboardFlags.CAPS_LOCK.value()) > 0) {
            flagsBuilder.append("kMFCapsLock");
            flagsBuilder.append(", ");
        }
        if((mKeyboardFlags & KeyboardFlags.LEFT_SHIFT.value()) > 0) {
            flagsBuilder.append("kMFLeftShift");
            flagsBuilder.append(", ");
        }
        if((mKeyboardFlags & KeyboardFlags.LEFT_CTRL.value()) > 0) {
            flagsBuilder.append("kMFControl");
            flagsBuilder.append(", ");
        }
        if((mKeyboardFlags & KeyboardFlags.LEFT_ALT.value()) > 0) {
            flagsBuilder.append("kMFLeftOption");
            flagsBuilder.append(", ");
        }
        if((mKeyboardFlags & KeyboardFlags.LEFT_CMD.value()) > 0) {
            flagsBuilder.append("kMFCommand");
            flagsBuilder.append(", ");
        }
        if((mKeyboardFlags & KeyboardFlags.RIGHT_CMD.value()) > 0) {
            flagsBuilder.append("kMFCommand");
            flagsBuilder.append(", ");
        }
        if((mKeyboardFlags & KeyboardFlags.RIGHT_ALT.value()) > 0) {
            flagsBuilder.append("kMFRightOption");
            flagsBuilder.append(", ");
        }
        if((mKeyboardFlags & KeyboardFlags.RIGHT_SHIFT.value()) > 0) {
            flagsBuilder.append("kMFRightShift");
            flagsBuilder.append(", ");
        }

        if(flagsBuilder.length() > 1) {
            flagsBuilder.setLength(flagsBuilder.length() - 2);
        }

        Log.d(TAG,"Key input: " + virtualKeyName + " with flags: " + flagsBuilder.toString());

        RemoteControllerClientService clientService = ((RemoteControllerClientInterface) mClientInterface).getClientService();
        if(clientService != null) {
            clientService.keyboardInput(virtualKeyName, flagsBuilder.toString());
        }
    }

    protected int[] characterKeyButtonIds() {
       return new int[] {
               R.id.keyboard_Q_btn, R.id.keyboard_W_btn, R.id.keyboard_E_btn, R.id.keyboard_R_btn,
               R.id.keyboard_T_btn, R.id.keyboard_Y_btn, R.id.keyboard_U_btn, R.id.keyboard_I_btn,
               R.id.keyboard_O_btn, R.id.keyboard_P_btn, R.id.keyboard_A_btn, R.id.keyboard_S_btn,
               R.id.keyboard_D_btn, R.id.keyboard_F_btn, R.id.keyboard_G_btn, R.id.keyboard_H_btn,
               R.id.keyboard_J_btn, R.id.keyboard_K_btn, R.id.keyboard_L_btn, R.id.keyboard_Z_btn,
               R.id.keyboard_X_btn, R.id.keyboard_C_btn, R.id.keyboard_V_btn, R.id.keyboard_B_btn,
               R.id.keyboard_N_btn, R.id.keyboard_M_btn
        };
    }

    protected int[] shiftableKeyButtonIds() {
        return new int[] {
                R.id.keyboard_grave_btn, R.id.keyboard_1_btn, R.id.keyboard_2_btn, R.id.keyboard_3_btn,
                R.id.keyboard_4_btn, R.id.keyboard_5_btn, R.id.keyboard_6_btn, R.id.keyboard_7_btn,
                R.id.keyboard_8_btn, R.id.keyboard_9_btn, R.id.keyboard_0_btn, R.id.keyboard_minus_btn,
                R.id.keyboard_equal_btn, R.id.keyboard_left_bracket_btn, R.id.keyboard_right_bracket_btn,
                R.id.keyboard_semicolon_btn, R.id.keyboard_quote_btn, R.id.keyboard_comma_btn, R.id.keyboard_backslash_btn,
                R.id.keyboard_comma_btn, R.id.keyboard_period_btn, R.id.keyboard_slash_btn
        };
    }

    protected int[] toggleKeyButtonIds() {
      return new int[] {
              R.id.keyboard_caps_lock_btn, R.id.keyboard_left_shift_btn, R.id.keyboard_control_btn,
              R.id.keyboard_left_alt_btn, R.id.keyboard_left_command_btn, R.id.keyboard_right_command_btn,
              R.id.keyboard_right_alt_btn, R.id.keyboard_right_shift_btn
        };
    }

    protected int[] otherKeyButtonIds() {
        return new int[]{
                R.id.keyboard_esc_btn, R.id.keyboard_f1_btn, R.id.keyboard_f2_btn, R.id.keyboard_f3_btn,
                R.id.keyboard_f4_btn, R.id.keyboard_f5_btn, R.id.keyboard_f6_btn, R.id.keyboard_f7_btn,
                R.id.keyboard_f8_btn, R.id.keyboard_f9_btn, R.id.keyboard_f10_btn, R.id.keyboard_f11_btn,
                R.id.keyboard_f12_btn, R.id.keyboard_backspace_btn, R.id.keyboard_tab_btn, R.id.keyboard_caps_lock_btn,
                R.id.keyboard_enter_btn, R.id.keyboard_space_btn, R.id.keyboard_left_arrow_btn,
                R.id.keyboard_up_arrow_btn, R.id.keyboard_right_arrow_btn, R.id.keyboard_down_arrow_btn
        };
    }

    private static final Map<Integer, String> keyButtonIdKeyNameMap = new HashMap<>();

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