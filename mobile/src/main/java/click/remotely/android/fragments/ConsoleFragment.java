package click.remotely.android.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import click.remotely.android.MainDrawerActivity;
import click.remotely.android.R;
import click.remotely.android.services.RemoteControllerClientService;

/**
 * Created by michzio on 15/07/2017.
 */

public class ConsoleFragment extends Fragment {

    private static final String CONSOLE_TEXT_KEY = "CONSOLE TEXT KEY";
    private static final String CONSOLE_INPUT_TEXT_KEY = "CONSOLE INPUT TEXT KEY";
    private static final String ACTIVITY_TITLE_KEY = "ACTIVITY TITLE KEY";

    private TextView mConsoleTextView;
    private EditText mConsoleEditText;

    private String mConsoleText = "";

    public ConsoleFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_console, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mConsoleTextView = (TextView) getActivity().findViewById(R.id.console_text_view);
        mConsoleEditText = (EditText) getActivity().findViewById(R.id.console_edit_text);
        mConsoleEditText.setOnEditorActionListener(editTextActionListener);

        if(savedInstanceState != null) {
            mConsoleText = savedInstanceState.getString(CONSOLE_TEXT_KEY, "");
            mConsoleTextView.setText(Html.fromHtml(mConsoleText));
            mConsoleEditText.setText(savedInstanceState.getCharSequence(CONSOLE_INPUT_TEXT_KEY));
            getActivity().setTitle(savedInstanceState.getCharSequence(ACTIVITY_TITLE_KEY));
        } else {
            String deviceName = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("pref_device_identity", "Client");
            mConsoleText = "<b>client > </b>" + deviceName;
            mConsoleTextView.setText(Html.fromHtml(mConsoleText));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the fragment's state here
        outState.putCharSequence(CONSOLE_INPUT_TEXT_KEY, mConsoleEditText.getText());
        outState.putString(CONSOLE_TEXT_KEY, mConsoleText);
        // Save the activity's title here
        outState.putCharSequence(ACTIVITY_TITLE_KEY, getActivity().getTitle());
    }

    private TextView.OnEditorActionListener editTextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ( (actionId == EditorInfo.IME_ACTION_GO) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))){

                CharSequence consoleInputText = mConsoleEditText.getText();

                mConsoleText += "<br/><b>client > </b>" + consoleInputText;
                mConsoleTextView.setText( Html.fromHtml( mConsoleText ));
                hideKeyboard(mConsoleEditText);
                clearEditText(mConsoleEditText);

                RemoteControllerClientService clientService = ((MainDrawerActivity) getActivity()).getClientService();
                if(clientService != null) {
                    clientService.sendRawMessage(consoleInputText.toString());
                }

                return true;
            }
            else{
                return false;
            }
        }
    };

    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void clearEditText(EditText editText) {
        editText.setText("");
    }
}
