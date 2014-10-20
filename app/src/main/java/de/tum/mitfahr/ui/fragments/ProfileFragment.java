package de.tum.mitfahr.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.ui.EditProfileActivity;
import de.tum.mitfahr.util.StringHelper;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCurrentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, rootView);
        //defaultDrawable = new ColorDrawable(R.color.gray);
        changeActionBarColor(getResources().getColor(R.color.transparent));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileNameText.setText(mCurrentUser.getFirstName() + " " + mCurrentUser.getLastName());
        profileEmailText.setText(mCurrentUser.getEmail());

        if (!StringHelper.isBlank(mCurrentUser.getPhoneNumber()))
            profilePhoneText.setText(mCurrentUser.getPhoneNumber());
        if (!StringHelper.isBlank(mCurrentUser.getCar()))
            profilePhoneText.setText(mCurrentUser.getCar());

        String profileImageUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getProfileImageURL(getActivity());
        Picasso.with(getActivity())
                .load(profileImageUrl.toString())
                .placeholder(R.drawable.ic_account_dark)
                .error(R.drawable.placeholder)
                .into(profileImage);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_profile) {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
