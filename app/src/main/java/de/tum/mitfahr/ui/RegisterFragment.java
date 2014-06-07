package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.RegisterEvent;

public class RegisterFragment extends Fragment {

    private RegistrationFinishedListener mListener;
    private Context mContext;
    private String mDepartment = "departmentNo(0)";

    @InjectView(R.id.emailEditText)
    EditText emailText;

    @InjectView(R.id.firstNameEditText)
    EditText firstNameText;

    @InjectView(R.id.lastNameEditText)
    EditText lastNameText;

    @InjectView(R.id.departmentSpinner)
    Spinner departmentSpinner;

    @InjectView(R.id.registerButton)
    Button registerButton;

    private ArrayAdapter<CharSequence> mDepartmentAdapter;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDepartmentAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.department_array, android.R.layout.simple_spinner_item);
        departmentSpinner.setAdapter(mDepartmentAdapter);
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDepartment = "departmentNo(" + position + ")";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.registerButton)
    public void onRegisterPressed(Button button) {

        if (emailText.getText().toString() != ""
                && firstNameText.getText().toString() != ""
                && lastNameText.getText().toString() != "") {
            String email = emailText.getText().toString();
            String firstName = firstNameText.getText().toString();
            String lastName = lastNameText.getText().toString();
            String department = "departmentNo(" + departmentSpinner.getSelectedItemPosition() + ")";

            TUMitfahrApplication.getApplication(mContext).getProfileService().register(email, firstName, lastName, department);

        }
    }

    @Subscribe
    public void onRegister(RegisterEvent event) {
        if (event.getType() == RegisterEvent.Type.REGISTER_SUCCESSFUL && mListener != null) {
            if (emailText.getText().toString() != "")
                mListener.onRegistrationFinished(emailText.getText().toString());
        } else if (event.getType() == RegisterEvent.Type.REGISTER_FAILED)
            Toast.makeText(mContext, "Registration failed! Please check credentials and try again.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RegistrationFinishedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RegistrationFinishedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    public interface RegistrationFinishedListener {
        // TODO: Update argument type and name
        public void onRegistrationFinished(String email);
    }

}
