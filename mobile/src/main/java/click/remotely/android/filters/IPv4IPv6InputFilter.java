package click.remotely.android.filters;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by michzio on 06/08/2017.
 */

public class IPv4IPv6InputFilter implements InputFilter {

    private InputFilter mIPv4InputFilter;
    private InputFilter mIPv6InputFilter;

    public IPv4IPv6InputFilter() {
        mIPv4InputFilter = new IPv4InputFilter();
        mIPv6InputFilter = new IPv6InputFilter();
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        CharSequence ipv4Replacement = mIPv4InputFilter.filter(source, start, end, dest, dstart, dend);
        CharSequence ipv6Replacement = mIPv6InputFilter.filter(source, start, end, dest, dstart, dend);
        if(ipv4Replacement == null) {
            // IPv4 address found, replacement is not needed.
            return null;
        }
        if(ipv6Replacement == null) {
            // IPv6 address found, replacement is not needed.
            return null;
        }
        // else discard input and replace it with empty string
        return "";
    }
}
