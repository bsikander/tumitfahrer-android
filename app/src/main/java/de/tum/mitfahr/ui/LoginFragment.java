package de.tum.mitfahr.ui;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;

public class LoginFragment extends Fragment {

    private RegisterClickListener mListener;
    private Context mContext;

    @InjectView(R.id.emailEditText)
    EditText emailText;

    @InjectView(R.id.passwordEditText)
    EditText passwordText;

    @InjectView(R.id.loginButton)
    Button loginButton;

    @InjectView(R.id.registerButton)
    Button registerButton;

    public static LoginFragment newInstance(String email) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String emailArg = getArguments() != null ? getArguments().getString("email") : "";
        if (!emailArg.equals("")) {
            emailText.setText(emailArg);
        }

    }

    @OnClick(R.id.loginButton)
    public void onLoginPressed(Button button) {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        if(email != ""){
            TUMitfahrApplication.getApplication(mContext).getProfileService().login(email,password);
        }
    }

    @OnClick(R.id.registerButton)
    public void onRegisterPressed(Button button) {
        if (mListener != null) {
            mListener.onRegisterClicked();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RegisterClickListener) activity;
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface RegisterClickListener {
        // TODO: Update argument type and name
        public void onRegisterClicked();
    }

}
