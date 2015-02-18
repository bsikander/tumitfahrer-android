package de.tum.mitfahr.ui;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.dd.CircularProgressButton;
import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.UpdateUserEvent;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.ui.fragments.PasswordPromptDialogFragment;
import de.tum.mitfahr.util.BitmapUtils;
import de.tum.mitfahr.util.StringHelper;

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
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Updating Profile");
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
        if (imageChanged) {
            if (!StringHelper.isBlank(changedImageUri))
                new UploadImageTask(this).execute(changedImageUri);
        }
        if (detailsChanged) {
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
            Log.d("EditProfileActivity", "" + selectedImage);
            Picasso.with(this)
                    .load(selectedImage.toString())
                    .placeholder(R.drawable.ic_account_dark)
                    .error(R.drawable.placeholder)
                    .resize(350, 350)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            userImageView.setImageBitmap(bitmap);
                            File path = Environment.getExternalStorageDirectory();
                            File dirFile = new File(path, "/" + "tumitfahr");
                            File imageFile = new File(dirFile, "profile_image_temp.png");
                            BitmapUtils.save(bitmap, Uri.fromFile(imageFile));
                            imageChanged = true;
                            changedImageUri = imageFile.getAbsolutePath();
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            Toast.makeText(EditProfileActivity.this, "Cannot load image", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

        }
    }

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

    }

    @Subscribe
    public void onUpdateUserResult(UpdateUserEvent result) {
        mProgressDialog.dismiss();
        if (result.getType() == UpdateUserEvent.Type.USER_UPDATED) {
            Toast.makeText(this, "User Updated", Toast.LENGTH_LONG).show();
            TUMitfahrApplication.getApplication(this).getProfileService().addUserToSharedPreferences(mCurrentUser);
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

    private class UploadImageTask extends AsyncTask<String, Void, Boolean> {

        Context mContext;

        public UploadImageTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            TUMitfahrApplication.getApplication(mContext).getProfileService().uploadImage(params[0], new ProgressListener() {
                @Override
                public void progressChanged(ProgressEvent progressEvent) {
                    Log.d("AMZPRogress", "Progress:" + progressEvent.getBytesTransferred());
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            File path = Environment.getExternalStorageDirectory();
            File dirFile = new File(path, "/" + "tumitfahr");
            File tempFile = new File(dirFile, "profile_image_temp.png");
            tempFile.delete();
            File originalFile = new File(dirFile, "profile_image.png");
            originalFile.delete();
            mProgressDialog.dismiss();
            finish();
        }
    }


}
