package de.tum.mitfahr.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.util.RSGaussianBlur;
import de.tum.mitfahr.widget.CircularImageView;

/**
 * Created by abhijith on 02/10/14.
 */
public class UserDetailsActivity extends Activity {

    public static final String USER_INTENT_EXTRA = "selected_user";

    @InjectView(R.id.profile_big_blurred)
    ImageView blurredProfileImage;

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

    @InjectView(R.id.profile_password)
    TextView profilePasswordText;

    private User mCurrentUser;
    private RenderScript rs;
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

    @Override
    protected void onPause() {
        super.onPause();
        if (rs != null) {
            rs.destroy();
            rs = null;
        }
    }

    private RenderScript getRs() {
        if (rs == null) {
            rs = RenderScript.create(this);
        }
        return rs;
    }

    public class BlurTask extends AsyncTask<Void, Void, Bitmap> {

        Bitmap originalBitmap;

        public BlurTask(Bitmap originalBitmap) {
            this.originalBitmap = originalBitmap;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap sampledBitmap = null;
            if (originalBitmap != null) {

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 100;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                originalBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                // sampledBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
                sampledBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_placeholder);
            }
            Bitmap blurredBitmap = null;
            try {
                blurredBitmap = new RSGaussianBlur(getRs()).blur(16, sampledBitmap);
            } catch (Exception e) {
                Log.e("BlurTask", "Cannot blur\n" + e);
            }
            return blurredBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                blurredProfileImage.setImageBitmap(bitmap);
            }
            super.onPostExecute(bitmap);
        }
    }
}
