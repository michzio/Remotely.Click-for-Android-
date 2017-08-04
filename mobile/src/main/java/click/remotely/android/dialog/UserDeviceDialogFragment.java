package click.remotely.android.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import click.remotely.android.R;

/**
 * Created by michzio on 04/08/2017.
 */

public class UserDeviceDialogFragment extends DialogFragment {

    private EditText mDeviceNameEditText;
    private EditText mDeviceHostEditText;
    private EditText mDevicePortNumberEditText;

    public UserDeviceDialogFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_fragment_user_device, container);
        mDeviceNameEditText = (EditText) view.findViewById(R.id.dialog_user_device_name_edit_text);

        getDialog().setTitle(R.string.create_user_device_title);

        return view;
    }
}
