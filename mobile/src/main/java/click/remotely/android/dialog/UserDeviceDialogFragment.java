package click.remotely.android.dialog;


import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import click.remotely.android.R;
import click.remotely.android.filters.IPv4IPv6InputFilter;
import click.remotely.android.filters.IPv4InputFilter;
import click.remotely.android.filters.IPv6InputFilter;
import click.remotely.android.filters.PortNumberInputFilter;
import click.remotely.common.Utils;
import click.remotely.database.UserDeviceInfoProvider;
import click.remotely.model.UserDeviceInfo;

/**
 * Created by michzio on 04/08/2017.
 */

public class UserDeviceDialogFragment extends DialogFragment {

    private static final String TAG = UserDeviceDialogFragment.class.getName();
    private static final String DIALOG_TITLE_EXTRA = "DIALOG_TITLE_EXTRA";
    private static final String FORM_SUBMISSION_ERRORS_TEXT_EXTRA = "FORM_SUBMISSION_ERRORS_TEXT_EXTRA";
    private static final String FORM_SUBMISSION_ERRORS_VISIBILITY_EXTRA = "FORM_SUBMISSION_ERRORS_VISIBILITY_EXTRA";

    private String mDialogTitle;
    private Toolbar mToolbar;

    private TextInputLayout mDeviceNameTextInputLayout;
    private TextInputLayout mDeviceHostTextInputLayout;
    private TextInputLayout mDevicePortNumberTextInputLayout;

    private TextView mFormSubmissionErrorsTextView;

    public UserDeviceDialogFragment() { }

    public static UserDeviceDialogFragment newInstance(String title) {


        UserDeviceDialogFragment fragment = new UserDeviceDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_EXTRA, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mDialogTitle = args.getString(DIALOG_TITLE_EXTRA, mDialogTitle);
        }

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putCharSequence(FORM_SUBMISSION_ERRORS_TEXT_EXTRA, mFormSubmissionErrorsTextView.getText());
        outState.putInt(FORM_SUBMISSION_ERRORS_VISIBILITY_EXTRA, mFormSubmissionErrorsTextView.getVisibility());

        super.onSaveInstanceState(outState);
    }

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dialog_fragment_user_device, container, false);

        mDeviceNameTextInputLayout = (TextInputLayout) rootView.findViewById(R.id.dialog_user_device_name_text_input_layout);
        mDeviceHostTextInputLayout = (TextInputLayout) rootView.findViewById(R.id.dialog_user_device_host_text_input_layout);
        mDevicePortNumberTextInputLayout = (TextInputLayout) rootView.findViewById(R.id.dialog_user_device_port_number_text_input_layout);

        mFormSubmissionErrorsTextView = (TextView) rootView.findViewById(R.id.dialog_user_device_form_submission_errors);

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        configureToolbar();
        configureInputFilters();

        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle inState) {
        super.onViewStateRestored(inState);

        if(inState != null) {
            // get form submission errors view text
            CharSequence formSubmissionErrorsText =
                    inState.getCharSequence(FORM_SUBMISSION_ERRORS_TEXT_EXTRA,
                            getString(R.string.dialog_user_device_form_submission_errors));
            // get form submission errors view visibility
            int formSubmissionErrorsVisibility =
                    inState.getInt(FORM_SUBMISSION_ERRORS_VISIBILITY_EXTRA, View.GONE);

            mFormSubmissionErrorsTextView.setText(formSubmissionErrorsText);
            mFormSubmissionErrorsTextView.setVisibility(formSubmissionErrorsVisibility);
        }
    }

    private void configureToolbar() {

        mToolbar.setTitle(mDialogTitle);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            Drawable drawable = getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.WHITE);
            actionBar.setHomeAsUpIndicator(drawable);
        }
        setHasOptionsMenu(true);
    }

    private void configureInputFilters() {

        InputFilter[] ipFilters = {new IPv4IPv6InputFilter()};
        InputFilter[] portNumberFilters = { new PortNumberInputFilter()};

        mDeviceHostTextInputLayout.getEditText().setFilters(ipFilters);
        mDevicePortNumberTextInputLayout.getEditText().setFilters(portNumberFilters);

        mDeviceNameTextInputLayout.getEditText().setOnFocusChangeListener((v, hasFocus) -> editTextFocusChanged(v, hasFocus));
        mDeviceHostTextInputLayout.getEditText().setOnFocusChangeListener((v, hasFocus) -> editTextFocusChanged(v, hasFocus));
        mDevicePortNumberTextInputLayout.getEditText().setOnFocusChangeListener((v, hasFocus) -> editTextFocusChanged(v, hasFocus));
        mDevicePortNumberTextInputLayout.getEditText().setOnEditorActionListener(editorActionListener);
    }

    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {

            if(tv.getId() == R.id.dialog_user_device_port_number_edit_text) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateUserDevicePortNumberInput();
                    Utils.hideSoftKeyboard(getActivity(), tv);
                    return true;
                }
            }
            return false;
        }
    };

   private void editTextFocusChanged(View v, boolean hasFocus) {
       if(!hasFocus) {
           switch (v.getId()) {
               case R.id.dialog_user_device_name_edit_text:
                   validateUserDeviceNameInput();
                   break;
               case R.id.dialog_user_device_host_edit_text:
                   validateUserDeviceHostInput();
                   break;
               case R.id.dialog_user_device_port_number_edit_text:
                   validateUserDevicePortNumberInput();
                   break;
               default:
                   break;
           }
       }
   }

   private void validateUserDeviceNameInput() {

        Log.d(TAG, "Validating entered Device Name input.");

        String input = mDeviceNameTextInputLayout.getEditText().getText().toString();

        // check if device name is not empty
       if(input.isEmpty()) {
           mDeviceNameTextInputLayout.setError(getString(R.string.dialog_user_device_name_error_empty));
       } else if(isUserDeviceNameAlreadyInDB(input)) {
           mDeviceNameTextInputLayout.setError(getString(R.string.dialog_user_device_name_error_duplicate));
       } else {
           mDeviceNameTextInputLayout.setError(null);
       }
   }

    private boolean isUserDeviceNameAlreadyInDB(String userDeviceName) {

        final String[] projection = {UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_USER_DEVICE_INFO_ID};
        final String selection = UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_DEVICE_NAME + " = ?";
        final String[] selectionArgs = { userDeviceName };

        Cursor cursor = getActivity().getContentResolver()
                .query(UserDeviceInfoProvider.CONTENT_URI, null, selection, selectionArgs, null);

        return cursor.getCount() > 0;

    }

    private void validateUserDeviceHostInput() {

        Log.d(TAG, "Validating entered Device Host input.");

        String input = mDeviceHostTextInputLayout.getEditText().getText().toString();
        Boolean isIPv4Address = input.matches(IPv4InputFilter.IPv4_REGEX_PATTERN);
        Boolean isIPv6Address = input.matches(IPv6InputFilter.IPv6_REGEX_PATTERN);

        // check if input has correct ip address format (IPv4 or IPv6)
        if (!isIPv4Address && !isIPv6Address) {
            mDeviceHostTextInputLayout.setError(getString(R.string.dialog_user_device_host_error));
        } else {
            mDeviceHostTextInputLayout.setError(null);
        }
    }

    private void validateUserDevicePortNumberInput() {

        Log.d(TAG, "Validating entered Device Port Number input.");

        String input = mDevicePortNumberTextInputLayout.getEditText().getText().toString();
        Boolean isPortNumber = input.matches(PortNumberInputFilter.PORT_NUMBER_REGEX_PATTERN);

        // check if input is correct port number 0-65535 and
        // it is not the one of well known port numbers (0-1024)
        if(!isPortNumber || (Integer.valueOf(input) < 1025)) {
            mDevicePortNumberTextInputLayout.setError(getString(R.string.dialog_user_device_port_number_error));
        } else {
            mDevicePortNumberTextInputLayout.setError(null);
        }
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     **/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnDismissListener(this);
        dialog.setOnCancelListener(this);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menu.clear();
        menuInflater.inflate(R.menu.dialog_user_device, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.dialog_user_device_action_save:
                // handle confirmation button click here
                clickedAddUserDevice();
                return true;
            case android.R.id.home:
                // handle close button click here
                dismiss();
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void clickedAddUserDevice() {

        View focusView = getActivity().getCurrentFocus();

        if(focusView != null) {
            focusView.clearFocus();
            // hide soft keyboard and clear input fields focus
            Utils.hideSoftKeyboard(getActivity(), focusView);
        }

        // validate form and grab user device data
        ArrayList<String> errorInputs = new ArrayList<>();


        if( TextUtils.isEmpty(mDeviceNameTextInputLayout.getEditText().getText()) )  {
            mDeviceNameTextInputLayout.setError(getString(R.string.dialog_user_device_name_error_empty));
        }
        if( TextUtils.isEmpty(mDeviceHostTextInputLayout.getEditText().getText()) ) {
            mDeviceHostTextInputLayout.setError(getString(R.string.dialog_user_device_host_error_empty));
        }
        if( TextUtils.isEmpty(mDevicePortNumberTextInputLayout.getEditText().getText()) ) {
            mDevicePortNumberTextInputLayout.setError(getString(R.string.dialog_user_device_port_number_error_empty));
        }

        if( !TextUtils.isEmpty(mDeviceNameTextInputLayout.getError()) ) {
            errorInputs.add(getString(R.string.dialog_user_device_name));
        }
        if( !TextUtils.isEmpty(mDeviceHostTextInputLayout.getError()) ) {
            errorInputs.add(getString(R.string.dialog_user_device_host));
        }
        if( !TextUtils.isEmpty(mDevicePortNumberTextInputLayout.getError()) ) {
            errorInputs.add(getString(R.string.dialog_user_device_port_number));
        }

        if(errorInputs.size() > 0) {
            // display form submission errors alert
            showFormSubmissionErrorsAlert(errorInputs);
        } else {
            // animate disappearance of errors view
            if(mFormSubmissionErrorsTextView.getVisibility() == View.VISIBLE) {
                Utils.hideSlideUpAnimatingView(mFormSubmissionErrorsTextView);
            }

            // save new UserDeviceInfo object in database
            String deviceName = mDeviceNameTextInputLayout.getEditText().getText().toString();
            String host = mDeviceHostTextInputLayout.getEditText().getText().toString();
            Integer portNumber = Integer.valueOf(mDevicePortNumberTextInputLayout.getEditText().getText().toString());

            UserDeviceInfo userDeviceInfo = new UserDeviceInfo(deviceName, host, portNumber);
            saveUserDeviceInfoInDB(userDeviceInfo);
        }
    }

    private void showFormSubmissionErrorsAlert(List<String> errorInputs) {

        mFormSubmissionErrorsTextView.setText(getString(R.string.dialog_user_device_form_submission_errors));

        mFormSubmissionErrorsTextView.append(" ");
        for(int i = 0; i<errorInputs.size(); i++) {
            mFormSubmissionErrorsTextView.append(errorInputs.get(i));
            if(i == errorInputs.size()-1) {
                mFormSubmissionErrorsTextView.append(".");
            } else {
                mFormSubmissionErrorsTextView.append(", ");
            }
        }

        if(mFormSubmissionErrorsTextView.getVisibility() == View.GONE) {
            // animate appearance of errors view
            Utils.showSlideDownAnimatingView(mFormSubmissionErrorsTextView);
        }
    }

    private void saveUserDeviceInfoInDB(UserDeviceInfo userDeviceInfo) {

        // defines an object to contain the new user device info's row values  to insert
        ContentValues userDeviceInfoValues = new ContentValues();

        // sets the values of each column and inserts the user device info row
        // arguments to the "put" method are "column name" and "value"
        userDeviceInfoValues.put(UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_DEVICE_NAME, userDeviceInfo.getDeviceName());
        userDeviceInfoValues.put(UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_HOST, userDeviceInfo.getHost());
        userDeviceInfoValues.put(UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_PORT_NUMBER, userDeviceInfo.getPortNumber());

        Uri lastInsertedUri = getContext().getContentResolver()
                .insert(UserDeviceInfoProvider.CONTENT_URI, userDeviceInfoValues);

        Log.d(TAG, "Last inserted user device info row Uri: " + lastInsertedUri.toString() );

        long lastInsertedId = ContentUris.parseId(lastInsertedUri);
        if(lastInsertedId > 0) {
            showSnackBarUserDeviceAdded();
        } else {

        }

        dismiss();
    }

    private void showSnackBarUserDeviceAdded() {

        View coordinatorLayout = getActivity().findViewById(R.id.activity_remote_devices_coordinator_layout);

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Custom User Device added", Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { }
                });
        snackbar.show();
    }

    /****************************************************
     * Dialog Event Listener
     ****************************************************/
    public interface OnDialogEventListener {
        public void onDialogClose();
    }

    private OnDialogEventListener mOnDialogEventListener;

    public void setOnDialogEventListener(OnDialogEventListener listener) {
        mOnDialogEventListener = listener;
    }

    public OnDialogEventListener getOnDialogEventListener() {

        return mOnDialogEventListener;
    }

    /*****************************************************/

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        Log.d(TAG, "onDismiss method called.");

        if(mOnDialogEventListener != null) {
            mOnDialogEventListener.onDialogClose();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        Log.d(TAG, "onCancel method called.");


    }
}
