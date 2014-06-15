package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.timepicker.TimePickerBuilder;
import com.doomonafireball.betterpickers.timepicker.TimePickerDialogFragment;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.SearchEvent;

/**
 * Created by abhijith on 22/05/14.
 */
public class SearchFragment extends AbstractNavigationFragment implements CalendarDatePickerDialog.OnDateSetListener, TimePickerDialogFragment.TimePickerDialogHandler {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private static final String TAG_DATE_PICKER_FRAGMENT = "date_picker_fragment";

    private int mHourOfDeparture;
    private int mMinuteOfDeparture;
    private int mYearOfDeparture;
    private int mMonthOfDeparture;
    private int mDayOfDeparture;

    @InjectView(R.id.fromSearchEditText)
    EditText fromText;

    @InjectView(R.id.toSearchEditText)
    EditText toText;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.inject(this, rootView);
        changeActionBarColor(getResources().getColor(R.color.blue3));
        return rootView;
    }


    @OnClick(R.id.searchButton)
    public void onSearchPressed(Button button) {
        String from = fromText.getText().toString();
        String to = toText.getText().toString();
        String dateTime = getFormattedDate();
        if (from != "" && to != "") {
            TUMitfahrApplication.getApplication(getActivity()).getSearchService()
                    .search(from, to, dateTime);
        }
    }

    @OnClick(R.id.pickTimeSearchButton)
    public void showTimePickerDialog() {
        TimePickerBuilder timePickerBuilder = new TimePickerBuilder()
                .setFragmentManager(getChildFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                .setTargetFragment(SearchFragment.this);
        timePickerBuilder.show();
    }

    @OnClick(R.id.pickDateSearchButton)
    public void showDatePickerDialog() {
        FragmentManager fm = getChildFragmentManager();
        DateTime now = DateTime.now();
        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(this, now.getYear(), now.getMonthOfYear() - 1,
                        now.getDayOfMonth());
        calendarDatePickerDialog.show(fm, TAG_DATE_PICKER_FRAGMENT);
    }

    public String getFormattedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        calendar.set(Calendar.HOUR_OF_DAY, mHourOfDeparture);
        calendar.set(Calendar.MINUTE, mMinuteOfDeparture);
        calendar.set(Calendar.SECOND, 0); // I just set them to 0
        calendar.set(Calendar.YEAR, mYearOfDeparture);
        calendar.set(Calendar.MONTH, mMonthOfDeparture);
        calendar.set(Calendar.DAY_OF_MONTH, mDayOfDeparture);
        outputFormat.setTimeZone(TimeZone.getDefault());
        return outputFormat.format(calendar.getTime());
    }

    @Subscribe
    public void onSearchResults(SearchEvent event) {
        if (event.getType() == SearchEvent.Type.SEARCH_SUCCESSFUL) {
            Toast.makeText(getActivity(), "Search Succeeded",
                    Toast.LENGTH_SHORT).show();
//            mSearchListener.showSearchResults(event.getResponse().getRides(),
//                    fromText.getText().toString(), toText.getText().toString());
        } else if (event.getType() == SearchEvent.Type.SEARCH_FAILED) {
            Toast.makeText(getActivity(), "Search Failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        mYearOfDeparture = year;
        mMonthOfDeparture = monthOfYear;
        mDayOfDeparture = dayOfMonth;
    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        mHourOfDeparture = hourOfDay;
        mMinuteOfDeparture = minute;
    }
}
