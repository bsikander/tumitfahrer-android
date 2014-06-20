package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.util.RSGaussianBlur;
import de.tum.mitfahr.widget.CircularImageView;

/**
 * Created by abhijith on 22/05/14.
 */
public class ProfileFragment extends AbstractNavigationFragment {

    @InjectView(R.id.profile_big_blurred)
    ImageView blurredProfileImage;

    @InjectView(R.id.profile_image)
    CircularImageView profileImage;

    @InjectView(R.id.profile_faculty_text)
    TextView facultyText;

    @InjectView(R.id.profile_name_text)
    TextView profileNameText;

    private RenderScript rs;
    private ColorDrawable defaultDrawable;


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
