package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.ui.AboutActivity;
import de.tum.mitfahr.ui.CarSharingActivity;
import de.tum.mitfahr.ui.FeedbackActivity;
import de.tum.mitfahr.ui.LicensesActivity;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.ui.TeamActivity;
import de.tum.mitfahr.ui.TermsOfUseActivity;

/**
 * Created by abhijith on 22/05/14.
 */
public class SettingsFragment extends AbstractNavigationFragment {


    public SettingsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SettingsFragment newInstance(int sectionNumber) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.inject(this, rootView);
        changeActionBarColor(getResources().getColor(R.color.gray));
        return rootView;
    }

    @OnClick(R.id.settings_licenses)
    public void onLicensesClicked() {
        Intent intent = new Intent(getActivity(), LicensesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.settings_about)
    public void onAboutClicked() {
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.settings_team)
    public void onTeamClicked() {
        Intent intent = new Intent(getActivity(), TeamActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.settings_report_problem)
    public void onReportProblemClicked() {
        User user = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
        StringBuilder emailText = new StringBuilder();
        emailText.append("User:" + user.getFirstName() + " " + user.getLastName());
        emailText.append(System.getProperty("line.separator"));
        emailText.append("UserId:" + user.getId());
        emailText.append(System.getProperty("line.separator"));
        emailText.append("Email:" + user.getEmail());
        emailText.append(System.getProperty("line.separator"));
        emailText.append("--------------------------------");
        emailText.append(System.getProperty("line.separator"));
        emailText.append(System.getProperty("line.separator"));
        emailText.append(System.getProperty("line.separator"));
        emailText.append("<---Describe your problem here--->");

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "tumitfahrer@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[REPORT_PROBLEM]");
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText.toString());
        startActivity(Intent.createChooser(emailIntent, "Send email..."));

    }

    @OnClick(R.id.settings_feedback)
    public void onFeedbackClicked() {
        Intent intent = new Intent(getActivity(), FeedbackActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.settings_terms_of_use)
    public void onTermsOfUseClicked() {
        Intent intent = new Intent(getActivity(), TermsOfUseActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.settings_carsharing)
    public void onCarSharingClicked() {
        Intent intent = new Intent(getActivity(), CarSharingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).
                    setTitle("Logout?").
                    setMessage("Are you sure you want to logout?").
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TUMitfahrApplication.getApplication(getActivity()).getProfileService().logout();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                        }
                    }).
                    setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }


}
