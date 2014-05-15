package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.R;

public class RegisterFragment extends Fragment {

    private RegistrationFinishedListener mListener;

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
    }

    @OnClick(R.id.registerButton)
    public void onRegisterPressed(Button button) {

        if (emailText.getText().toString() != ""
                && firstNameText.getText().toString() != ""
                && lastNameText.getText().toString() != ""){



        }

            if (mListener != null) {
                if (emailText.getText().toString() != "")
                    mListener.onRegistrationFinished(emailText.getText().toString());
            }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RegistrationFinishedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * See the Android Training lesson
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * Communicating with Other Fragments for more information.
     */
    public interface RegistrationFinishedListener {
        // TODO: Update argument type and name
        public void onRegistrationFinished(String email);
    }

}
