package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.util.RSGaussianBlur;
import de.tum.mitfahr.util.StringHelper;
import de.tum.mitfahr.widget.CircularImageView;

/**
 * Created by abhijith on 22/05/14.
 */
public class ProfileFragment extends AbstractNavigationFragment {

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

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public void onPause() {
        super.onPause();
        if (rs != null) {
            rs.destroy();
            rs = null;
        }
    }

    private RenderScript getRs() {
        if (rs == null) {
            rs = RenderScript.create(getActivity());
        }
        return rs;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, rootView);
        blurredProfileImage.setImageResource(R.drawable.profile_placeholder);
        //defaultDrawable = new ColorDrawable(R.color.gray);
        changeActionBarColor(getResources().getColor(R.color.transparent));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_placeholder);
        new BlurTask(originalBitmap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        profileNameText.setText(mCurrentUser.getFirstName() + " " + mCurrentUser.getLastName());
        profileEmailText.setText(mCurrentUser.getEmail());

        if (!StringHelper.isBlank(mCurrentUser.getPhoneNumber()))
            profilePhoneText.setText(mCurrentUser.getPhoneNumber());
        if (!StringHelper.isBlank(mCurrentUser.getCar()))
            profilePhoneText.setText(mCurrentUser.getCar());

        int deptIndex = Integer.parseInt(mCurrentUser.getDepartment());
        String department = getResources().getStringArray(R.array.department_array)[deptIndex];

        profileDepartmentText.setText(department);

        URL profileImageUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getProfileImageURL();
        Picasso.with(getActivity())
                .load(profileImageUrl.toString())
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.placeholder)
                .into(profileImage);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
                sampledBitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.profile_placeholder);
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
