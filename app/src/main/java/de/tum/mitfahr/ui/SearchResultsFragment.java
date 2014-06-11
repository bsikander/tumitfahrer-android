package de.tum.mitfahr.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;

import de.tum.mitfahr.networking.adapters.SearchAdapter;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.dummy.DummyContent;


public class SearchResultsFragment extends ListFragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String RIDES = "rides";

    // TODO: Rename and change types of parameters
    private String mFrom;
    private String mTo;
    private ArrayList<Ride> mRides;

    private OnFragmentInteractionListener mListener;


    @InjectView(R.id.fromResultsEditText)
    EditText fromText;

    @InjectView(R.id.toResultsEditText)
    EditText toText;

    //@InjectView(R.id.searchResultList)
    //ListView mListView;

    private QuickReturnListView mListView;
    private View mHeader;
    private LinearLayout mQuickReturnView;
    private View mPlaceHolder;

    private int mCachedVerticalScrollRange;
    private int mQuickReturnHeight;

    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;
    private static final int STATE_EXPANDED = 3;
    private int mState = STATE_ONSCREEN;
    private int mScrollY;
    private int mMinRawY = 0;
    private int rawY;
    private boolean noAnimation = false;

    private TranslateAnimation anim;

    /**
     * The fragment's ListView/GridView.
     */
    //private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SearchAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static SearchResultsFragment newInstance(ArrayList<Ride> rides, String from, String to) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(FROM, from);
        args.putString(TO, to);
        args.putSerializable(RIDES, rides);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchResultsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mRides = (ArrayList<Ride>) getArguments().getSerializable(RIDES);
            mFrom = getArguments().getString(FROM);
            mTo = getArguments().getString(TO);
        }

        mAdapter = new SearchAdapter(this.getActivity(), mRides);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchresults, container, false);
        ButterKnife.inject(this, view);

        mHeader = inflater.inflate(R.layout.header, null);
        mQuickReturnView = (LinearLayout) view.findViewById(R.id.sticky);
        mPlaceHolder = mHeader.findViewById(R.id.placeholder);
        //mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        //mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView = (QuickReturnListView) getListView();

        fromText.setText(mFrom);
        toText.setText(mTo);
        mListView.addHeaderView(mHeader);


        mListView.setAdapter(mAdapter);

        mListView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mQuickReturnHeight = mQuickReturnView.getHeight();
                        mListView.computeScrollY();
                        mCachedVerticalScrollRange = mListView.getListHeight();
                    }
                });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @SuppressLint("NewApi")
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                mScrollY = 0;
                int translationY = 0;

                if (mListView.scrollYIsComputed()) {
                    mScrollY = mListView.getComputedScrollY();
                }

                rawY = mPlaceHolder.getTop()
                        - Math.min(
                        mCachedVerticalScrollRange
                                - mListView.getHeight(), mScrollY);

                switch (mState) {
                    case STATE_OFFSCREEN:
                        if (rawY <= mMinRawY) {
                            mMinRawY = rawY;
                        } else {
                            mState = STATE_RETURNING;
                        }
                        translationY = rawY;
                        break;

                    case STATE_ONSCREEN:
                        if (rawY < -mQuickReturnHeight) {
                            System.out.println("test3");
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                        translationY = rawY;
                        break;

                    case STATE_RETURNING:

                        if (translationY > 0) {
                            translationY = 0;
                            mMinRawY = rawY - mQuickReturnHeight;
                        }

                        else if (rawY > 0) {
                            mState = STATE_ONSCREEN;
                            translationY = rawY;
                        }

                        else if (translationY < -mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;

                        } else if (mQuickReturnView.getTranslationY() != 0
                                && !noAnimation) {
                            noAnimation = true;
                            anim = new TranslateAnimation(0, 0,
                                    -mQuickReturnHeight, 0);
                            anim.setFillAfter(true);
                            anim.setDuration(250);
                            mQuickReturnView.startAnimation(anim);
                            anim.setAnimationListener(new Animation.AnimationListener() {

                                @Override
                                public void onAnimationStart(Animation animation) {
// TODO Auto-generated method stub

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
// TODO Auto-generated method stub

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    noAnimation = false;
                                    mMinRawY = rawY;
                                    mState = STATE_EXPANDED;
                                }
                            });
                        }
                        break;

                    case STATE_EXPANDED:
                        if (rawY < mMinRawY - 2 && !noAnimation) {
                            noAnimation = true;
                            anim = new TranslateAnimation(0, 0, 0,
                                    -mQuickReturnHeight);
                            anim.setFillAfter(true);
                            anim.setDuration(250);
                            anim.setAnimationListener(new Animation.AnimationListener() {

                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    noAnimation = false;
                                    mState = STATE_OFFSCREEN;
                                }
                            });
                            mQuickReturnView.startAnimation(anim);
                        } else if (translationY > 0) {
                            translationY = 0;
                            mMinRawY = rawY - mQuickReturnHeight;
                        }

                        else if (rawY > 0) {
                            mState = STATE_ONSCREEN;
                            translationY = rawY;
                        }

                        else if (translationY < -mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        } else {
                            mMinRawY = rawY;
                        }
                }

                    mQuickReturnView.setTranslationY(translationY);


            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
