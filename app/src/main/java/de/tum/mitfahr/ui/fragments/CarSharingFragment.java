package de.tum.mitfahr.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;

/**
 * Created by abhijith on 08/10/14.
 */
public class CarSharingFragment extends Fragment {

    @InjectView(R.id.webView)
    WebView webView;

    private static final String CAR_SHARING_URL = "https://carsharing.mvg-mobil.de/";

    public static CarSharingFragment newInstance() {
        CarSharingFragment fragment = new CarSharingFragment();
        return fragment;
    }

    public CarSharingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_car_sharing, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(CAR_SHARING_URL);
    }
}
