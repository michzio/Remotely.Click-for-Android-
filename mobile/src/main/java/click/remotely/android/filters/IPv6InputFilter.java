package click.remotely.android.filters;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by michzio on 06/08/2017.
 */

public class IPv6InputFilter implements InputFilter {

    // only full IPv6 address or its parts matches
    // ex. 2001:0db8:0a0b:12f0:0000:0000:0000:0001
    public static final String FULL_IPv6_REGEX_PATTERN =
            // 1:2:3:4:5:6:7:8
            "^[0-9a-fA-F]{1,4}(:([0-9a-fA-F]{1,4}(:([0-9a-fA-F]{1,4}(:([0-9a-fA-F]{1,4}(:([0-9a-fA-F]{1,4}(:([0-9a-fA-F]{1,4}(:([0-9a-fA-F]{1,4}(:([0-9a-fA-F]{1,4})?)?)?)?)?)?)?)?)?)?)?)?)?)?";

    public static final String IPv6_REGEX_PATTERN = "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        if(end > start) {
            String destTxt = dest.toString();
            String resultingTxt = destTxt.substring(0, dstart)
                    + source.subSequence(start, end)
                    + destTxt.substring(dend);


            if(!resultingTxt.matches("^[0-9a-fA-F:]{0,39}")) {
                return "";
            } else {
                String[] splits = resultingTxt.split(":", -1);
                int partsCount = splits.length;
                // do not take into account leading spaces
                if(splits.length > 0 && splits[0].isEmpty()) {
                    partsCount--;
                }
                // do not take into account trailing spaces
                if(splits.length > 2 && splits[splits.length-2].isEmpty()) {
                    partsCount--;
                }
                if(partsCount > 8) {
                    return "";
                }
                int emptyCount = 0;
                for(int i=0; i<splits.length; i++) {
                    if(!splits[i].matches("^[0-9a-fA-F]{0,4}")) {
                        return "";
                    }

                    if(splits[i].isEmpty() && i != 0 && i != (splits.length-1)) {
                        emptyCount++;
                    }
                }

                if(emptyCount > 1) {
                    return "";
                }
            }
        }
        return null;
    }
}
