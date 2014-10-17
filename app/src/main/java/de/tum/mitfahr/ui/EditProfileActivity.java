package de.tum.mitfahr.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.dd.CircularProgressButton;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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

    @InjectView(R.id.edit_profile_department)
    Spinner departmentSpinner;

    @InjectView(R.id.edit_profile_image)
    ImageView userImageView;

    @InjectView(R.id.edit_profile_button)
    CircularProgressButton editProfileButton;

    private boolean changed;
    private User mCurrentUser;
    private ArrayAdapter<CharSequence> mDepartmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.inject(this);
        mCurrentUser = TUMitfahrApplication.getApplication(this).getProfileService().getUserFromPreferences();
        populateTheFields();
    }

    private void populateTheFields() {
        firstNameEditText.setText(mCurrentUser.getFirstName());
        lastNameEditText.setText(mCurrentUser.getLastName());
        carEditText.setText(mCurrentUser.getCar());
        phoneNumberEditText.setText(mCurrentUser.getPhoneNumber());
        mDepartmentAdapter = ArrayAdapter.createFromResource(this,
                R.array.department_array, R.layout.spinner_item_white);
        departmentSpinner.setAdapter(mDepartmentAdapter);
        int deptIndex = Integer.parseInt(mCurrentUser.getDepartment());
        departmentSpinner.setSelection(deptIndex);
        URL profileImageUrl = TUMitfahrApplication.getApplication(this).getProfileService().getProfileImageURL();
        Picasso.with(this)
                .load(profileImageUrl.toString())
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
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            userImageView.setImageBitmap(bitmap);

        }
    }
}
