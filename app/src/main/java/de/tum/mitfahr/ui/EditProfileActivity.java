package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.dd.CircularProgressButton;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.User;

public class EditProfileActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);
        mCurrentUser = TUMitfahrApplication.getApplication(this).getProfileService().getUserFromPreferences();
        populateTheFields();
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
}
