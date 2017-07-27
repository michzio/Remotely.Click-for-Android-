package click.remotely.model;

/**
 * Created by michzio on 21/07/2017.
 */

public enum KeyboardFlags {

    CAPS_LOCK  (0b00000001),
    LEFT_SHIFT (0b00000010),
    LEFT_CTRL  (0b00000100),
    LEFT_ALT   (0b00001000),
    LEFT_CMD   (0b00010000), // on Windows keyboard it is Left Windows key
    RIGHT_CMD  (0b00100000), // on Windows keyboard it is Right Windows key
    RIGHT_ALT  (0b01000000),
    RIGHT_SHIFT(0b10000000);

    private final int value;

    private KeyboardFlags(int val) {
        this.value = val;
    }

    public int value() {
        return value;
    }
}
