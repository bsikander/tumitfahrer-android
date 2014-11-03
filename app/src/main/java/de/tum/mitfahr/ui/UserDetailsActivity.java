package de.tum.mitfahr.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.util.StringHelper;

/**
 * Created by abhijith on 02/10/14.
 */
public class UserDetailsActivity extends Activity {

    public static final String USER_INTENT_EXTRA = "selected_user";

    @InjectView(R.id.profile_image)
    CircularImageView profileImage;

    @InjectView(R.id.profile_name_text)
    TextView profileNameText;

    @InjectView(R.id.profile_email)
    TextView profileEmailText;

    @InjectView(R.id.profile_car)
    TextView profileCarText;

    @InjectView(R.id.profile_phone)
    TextView profilePhoneText;

    @InjectView(R.id.profile_department)
    TextView profileDepartmentText;

    private User mCurrentUser;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.fragment_profile);
        ButterKnife.inject(this);
        changeActionBarColor(android.R.color.transparent);

        Intent intent = getIntent();
        if (intent.hasExtra(USER_INTENT_EXTRA)) {
            mCurrentUser = (User) intent.getSerializableExtra(USER_INTENT_EXTRA);
        } else {
            finish();
        }
        showData();

    }

    private void showData() {
        profileNameText.setText(mCurrentUser.getFirstName() + " " + mCurrentUser.getLastName());
        profileEmailText.setText(mCurrentUser.getEmail());

        if (!StringHelper.isBlank(mCurrentUser.getPhoneNumber()))
            profilePhoneText.setText(mCurrentUser.getPhoneNumber());
        if (!StringHelper.isBlank(mCurrentUser.getCar()))
            profilePhoneText.setText(mCurrentUser.getCar());

        String profileImageUrl = TUMitfahrApplication.getApplication(this).getProfileService().getProfileImageURL(mCurrentUser.getId());
        Picasso.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_account_dark)
                .error(R.drawable.ic_account_dark)
                .into(profileImage);
    }

    public void changeActionBarColor(int newColor) {
        Drawable newDrawable = new ColorDrawable((newColor));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newDrawable.setCallback(drawableCallback);
        } else {
            getActionBar().setBackgroundDrawable(newDrawable);
        }
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            mHandler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            mHandler.removeCallbacks(what);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
