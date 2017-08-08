package click.remotely.android.filters;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by michzio on 06/08/2017.
 */

public class PortNumberInputFilter implements InputFilter {

    public static final int MIN_PORT_NUMBER = 1025;
    public static final int MAX_PORT_NUMBER = 65535;
    public static final String PORT_NUMBER_REGEX_PATTERN = "^0$|^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";
    private static final String TYPING_PORT_NUMBER_REGEX_PATTERN = "^0$|^(?!0{1,})\\d{1,5}$";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        if(end > start) {
            String destTxt = dest.toString();
            String resultingTxt = destTxt.substring(0, dstart)
                    + source.subSequence(start, end)
                    + destTxt.substring(dend);

            if(!resultingTxt.matches(TYPING_PORT_NUMBER_REGEX_PATTERN)) {
                return "";
            } else {
                Integer portNumber = Integer.valueOf(resultingTxt);
                if(portNumber > MAX_PORT_NUMBER) {
                    return "";
                }
            }
        }
        return null;
    }
}
