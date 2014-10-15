package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.tum.mitfahr.R;
import de.tum.mitfahr.ui.CarSharingActivity;
import de.tum.mitfahr.ui.LicensesActivity;

/**
 * Created by abhijith on 22/05/14.
 */
public class SettingsFragment extends AbstractNavigationFragment {


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

    public SettingsFragment() {
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
        Intent intent = new Intent(getActivity(), LicensesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.settings_team)
    public void onTeamClicked() {
        Intent intent = new Intent(getActivity(), LicensesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.settings_report_problem)
    public void onReportProblemClicked() {
        Intent intent = new Intent(getActivity(), LicensesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.settings_feedback)
    public void onFeedbackClicked() {
        Intent intent = new Intent(getActivity(), LicensesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.settings_terms_of_use)
    public void onTermsOfUseClicked() {
        Intent intent = new Intent(getActivity(), LicensesActivity.class);
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
}
