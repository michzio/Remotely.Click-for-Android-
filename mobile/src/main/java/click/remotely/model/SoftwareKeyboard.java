package click.remotely.model;

import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michzio on 22/08/2017.
 */

public class SoftwareKeyboard {

    public static Map<Integer, String> keyCodeKeyNameMap = new HashMap<>();

    static {
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_ESCAPE, "kVK_Escape");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F1,     "kVK_F1");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F2,     "kVK_F2");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F3,     "kVK_F3");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F4,     "kVK_F4");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F5,     "kVK_F5");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F6,     "kVK_F6");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F7,     "kVK_F7");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F8,     "kVK_F8");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F9,     "kVK_F9");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F10,    "kVK_F10");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F11,    "kVK_F11");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F12,    "kVK_F12");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_GRAVE,  "kVK_ANSI_Grave");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_1,      "kVK_ANSI_1");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_2,      "kVK_ANSI_2");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_3,      "kVK_ANSI_3");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_4,      "kVK_ANSI_4");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_5,      "kVK_ANSI_5");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_6,      "kVK_ANSI_6");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_7,      "kVK_ANSI_7");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_8,      "kVK_ANSI_8");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_9,      "kVK_ANSI_9");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_0,      "kVK_ANSI_0");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_MINUS,  "kVK_ANSI_Minus");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_EQUALS,  "kVK_ANSI_Equal");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_DEL,    "kVK_Backspace");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_TAB,    "kVK_Tab");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_Q,      "kVK_ANSI_Q");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_W,      "kVK_ANSI_W");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_E,      "kVK_ANSI_E");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_R,      "kVK_ANSI_R");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_T,      "kVK_ANSI_T");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_Y,      "kVK_ANSI_Y");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_U,      "kVK_ANSI_U");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_I,      "kVK_ANSI_I");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_O,      "kVK_ANSI_O");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_P,      "kVK_ANSI_P");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_LEFT_BRACKET, "kVK_ANSI_LeftBracket");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_RIGHT_BRACKET, "kVK_ANSI_RightBracket");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_BACKSLASH, "kVK_ANSI_Backslash");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_CAPS_LOCK, "kVK_CapsLock");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_A,      "kVK_ANSI_A");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_S,      "kVK_ANSI_S");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_D,      "kVK_ANSI_D");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_F,      "kVK_ANSI_F");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_G,      "kVK_ANSI_G");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_H,      "kVK_ANSI_H");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_J,      "kVK_ANSI_J");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_K,      "kVK_ANSI_K");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_L,      "kVK_ANSI_L");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_SEMICOLON, "kVK_ANSI_Semicolon");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_APOSTROPHE,  "kVK_ANSI_Quote");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_ENTER,  "kVK_Return");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_Z,      "kVK_ANSI_Z");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_X,      "kVK_ANSI_X");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_C,      "kVK_ANSI_C");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_V,      "kVK_ANSI_V");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_B,      "kVK_ANSI_B");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_N,      "kVK_ANSI_N");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_M,      "kVK_ANSI_M");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_COMMA,  "kVK_ANSI_Comma");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_PERIOD, "kVK_ANSI_Period");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_SLASH,  "kVK_ANSI_Slash");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_SPACE,  "kVK_Space");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_DPAD_LEFT, "kVK_LeftArrow");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_DPAD_UP, "kVK_UpArrow");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, "kVK_RightArrow");
        keyCodeKeyNameMap.put(KeyEvent.KEYCODE_DPAD_DOWN, "kVK_DownArrow");
    }

    public static String getModifierFlags(KeyEvent event) {

        StringBuilder flagsBuilder = new StringBuilder();
        if( event.isCapsLockOn() ) {
            flagsBuilder.append("kMFCapsLock");
            flagsBuilder.append(", ");
        }
        if( event.hasModifiers(KeyEvent.META_SHIFT_LEFT_ON) ) {
            flagsBuilder.append("kMFLeftShift");
            flagsBuilder.append(", ");
        }
        if( event.hasModifiers(KeyEvent.META_CTRL_LEFT_ON) ) {
            flagsBuilder.append("kMFControl");
            flagsBuilder.append(", ");
        }
        if( event.hasModifiers(KeyEvent.META_ALT_LEFT_ON) ) {
            flagsBuilder.append("kMFLeftOption");
            flagsBuilder.append(", ");
        }
        if( event.hasModifiers(KeyEvent.META_ALT_RIGHT_ON) ) {
            flagsBuilder.append("kMFRightOption");
            flagsBuilder.append(", ");
        }
        if( event.hasModifiers(KeyEvent.META_SHIFT_RIGHT_ON) ) {
            flagsBuilder.append("kMFRightShift");
            flagsBuilder.append(", ");
        }

        if(flagsBuilder.length() > 1) {
            flagsBuilder.setLength(flagsBuilder.length() - 2);
        }

        return flagsBuilder.toString();
    }
}
