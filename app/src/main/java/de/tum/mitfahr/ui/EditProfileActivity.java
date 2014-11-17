package de.tum.mitfahr.ui;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.UpdateUserEvent;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.ui.fragments.PasswordPromptDialogFragment;

public class EditProfileActivity extends ActionBarActivity implements PasswordPromptDialogFragment.PasswordPromptDialogListener {

    public static final int PICK_IMAGE_INTENT = 1;

    @InjectView(R.id.edit_profile_first_name)
    EditText firstNameEditText;

    @InjectView(R.id.edit_profile_last_name)
    EditText lastNameEditText;

    @InjectView(R.id.edit_profile_phone_number)
    EditText phoneNumberEditText;

    @InjectView(R.id.edit_profile_car)
    EditText carEditText;

    @InjectView(R.id.edit_profile_image)
    CircularImageView userImageView;

    @InjectView(R.id.edit_profile_button)
    CircularProgressButton editProfileButton;

    private boolean detailsChanged;
    private boolean imageChanged;
    private String changedImageUri;
    private User mCurrentUser;
    private ProgressDialog mProgressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ButterKnife.inject(this);
        mCurrentUser = TUMitfahrApplication.getApplication(this).getProfileService().getUserFromPreferences();
        populateTheFields();
        firstNameEditText.addTextChangedListener(textWatcher);
        lastNameEditText.addTextChangedListener(textWatcher);
        phoneNumberEditText.addTextChangedListener(textWatcher);
        carEditText.addTextChangedListener(textWatcher);
        mProgressDialog = new ProgressDialog(this);
    }

    private void populateTheFields() {
        firstNameEditText.setText(mCurrentUser.getFirstName());
        lastNameEditText.setText(mCurrentUser.getLastName());
        carEditText.setText(mCurrentUser.getCar());
        phoneNumberEditText.setText(mCurrentUser.getPhoneNumber());
        String profileImageUrl = TUMitfahrApplication.getApplication(this).getProfileService().getProfileImageURL(this);
        Picasso.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_account_dark)
                .error(R.drawable.ic_account_dark)
                .into(userImageView);
    }

    @OnClick(R.id.edit_profile_image)
    public void onProfileImageClicked() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_INTENT);
    }


    @OnClick(R.id.edit_profile_button)
    public void onEditProfileButtonClicked() {
        if (detailsChanged || imageChanged) {
            PasswordPromptDialogFragment dialogFragment = PasswordPromptDialogFragment.newInstance();
            dialogFragment.show(getFragmentManager(), "password_prompt");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_INTENT && resultCode == RESULT_OK
                && null != data) {

            Uri selectedImage = data.getData();

            Picasso.with(this)
                    .load(selectedImage.toString())
                    .placeholder(R.drawable.ic_account_dark)
                    .error(R.drawable.placeholder)
                    .into(userImageView);
            imageChanged = true;
            changedImageUri = selectedImage.toString();
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            detailsChanged = true;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (detailsChanged || imageChanged) {
                    AlertDialog dialog = new AlertDialog.Builder(this).
                            setTitle("Discard Changes?").
                            setMessage("Are you sure you want to discard changes?").
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).
                            setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String password) {
        //Start the edit process
        mProgressDialog.show();
        if (detailsChanged) {
            mCurrentUser.setFirstName(firstNameEditText.getText().toString());
            mCurrentUser.setLastName(lastNameEditText.getText().toString());
            mCurrentUser.setPhoneNumber(phoneNumberEditText.getText().toString());
            mCurrentUser.setCar(carEditText.getText().toString());
            TUMitfahrApplication.getApplication(this).getProfileService().
                    updateUser(mCurrentUser, mCurrentUser.getEmail(), password, password);
        }
        if (imageChanged) {
            TUMitfahrApplication.getApplication(this).getProfileService().uploadImage(changedImageUri);
        }
    }

    @Subscribe
    public void onUpdateUserResult(UpdateUserEvent result) {
        mProgressDialog.dismiss();
        if (result.getType() == UpdateUserEvent.Type.USER_UPDATED) {
            TUMitfahrApplication.getApplication(this).getProfileService().addUserToSharedPreferences(mCurrentUser);
            Toast.makeText(this, "User Updated", Toast.LENGTH_LONG).show();
            finish();
        } else if (result.getType() == UpdateUserEvent.Type.UPDATE_FAILED) {
            Toast.makeText(this, "Failed to update User", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }
}
