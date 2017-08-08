package click.remotely.android.filters;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by michzio on 06/08/2017.
 */

public class IPv4InputFilter implements InputFilter {

    public static final String TYPING_IPv4_REGEX_PATTERN = "^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?";
    public static final String IPv4_REGEX_PATTERN ="((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        if(end > start) {
            String destTxt = dest.toString();
            String resultingTxt = destTxt.substring(0, dstart)
                    + source.subSequence(start, end)
                    + destTxt.substring(dend);
            if(!resultingTxt.matches(TYPING_IPv4_REGEX_PATTERN)) {
                return "";
            } else {
                String[] splits = resultingTxt.split("\\.");
                for(int i=0; i<splits.length; i++) {
                    if(Integer.valueOf(splits[i]) > 255) {
                        return "";
                    }
                }
            }
        }
        return null;
    }
}
