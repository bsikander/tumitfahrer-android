package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import de.tum.mitfahr.R;

public class LoginRegisterActivity extends Activity implements LoginFragment.RegisterClickListener, RegisterFragment.RegistrationFinishedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public void onRegisterClicked() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRegistrationFinished(String email) {
        LoginFragment loginFragment = LoginFragment.newInstance(email);
        getFragmentManager().popBackStack();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, loginFragment)
                .commit();
    }
}
