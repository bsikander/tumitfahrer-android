package de.tum.mitfahr.ui.fragments;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.util.ActionBarColorChangeListener;

/**
 * Created by abhijith on 22/05/14.
 */
public abstract class AbstractNavigationFragment extends Fragment {

    protected static final String ARG_SECTION_NUMBER = "section_number";
    protected ActionBarColorChangeListener mListener;
    private Handler mHandler = new Handler();
    private int mCurrentColor = 0xFF666666;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ActionBarColorChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ActionBarColorChangeListener");
        }
        mCurrentColor = ((MainActivity) activity).getCurrentActionBarColor();
    }

    public void changeActionBarColor(int newColor) {

        Drawable newDrawable = new ColorDrawable(newColor);
        Drawable oldBackground = new ColorDrawable(mCurrentColor);
        if (oldBackground == null) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                newDrawable.setCallback(drawableCallback);
            } else {
                getActivity().getActionBar().setBackgroundDrawable(newDrawable);
            }

        } else {

            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, newDrawable});

            // workaround for broken ActionBarContainer drawable handling on
            // pre-API 17 builds
            // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                td.setCallback(drawableCallback);
            } else {
                getActivity().getActionBar().setBackgroundDrawable(td);
            }

            td.startTransition(200);

        }
        // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-mHandler
        getActivity().getActionBar().setDisplayShowTitleEnabled(false);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);

        mCurrentColor = newColor;
        ((MainActivity) getActivity()).onActionBarColorChanged(newColor);
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

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActivity().getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            mHandler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            mHandler.removeCallbacks(what);
        }
    };

}
