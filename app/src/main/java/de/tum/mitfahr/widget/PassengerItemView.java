package de.tum.mitfahr.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import de.tum.mitfahr.R;
import de.tum.mitfahr.networking.models.User;

/**
 * Created by abhijith on 03/10/14.
 */
public class PassengerItemView extends RelativeLayout implements View.OnClickListener {

    public static final int TYPE_ACCEPTED = 0;
    public static final int TYPE_PENDING = 1;

    private final CircularImageView mProfileImage;
    private final TextView mPassengerName;
    private final ImageButton mRemoveButton;
    private final ImageButton mActionButton;
    private int mItemType = TYPE_ACCEPTED;

    private User mPassenger;
    private boolean owner;

    private PassengerItemClickListener mListener;

    public interface PassengerItemClickListener {

        void onRemoveClicked(User passenger);

        void onActionClicked(User passenger);

        void onUserClicked(User passenger);
    }

    public PassengerItemView(Context context) {
        this(context, null, 0);
    }

    public PassengerItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PassengerItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.details_passenger_composite_view, this, true);
        mProfileImage = (CircularImageView) findViewById(R.id.passenger_image_view);
        mPassengerName = (TextView) findViewById(R.id.passenger_name);
        mRemoveButton = (ImageButton) findViewById(R.id.remove_image_button);
        mActionButton = (ImageButton) findViewById(R.id.action_image_button);

        mListener = sDummyListener;

        mActionButton.setBackgroundResource(android.R.color.holo_green_light);
        //mActionButton.setImageResource(R.drawable.placeholder);

        mRemoveButton.setBackgroundResource(android.R.color.holo_red_light);
        //mRemoveButton.setImageResource(R.drawable.placeholder);

        mRemoveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRemoveClicked(mPassenger);
            }
        });

        mActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onActionClicked(mPassenger);
            }
        });
        showHideActionButtons();

    }

    private void showHideActionButtons() {
        if (!owner) {
            mActionButton.setVisibility(GONE);
            mRemoveButton.setVisibility(GONE);
        } else {
            mActionButton.setVisibility(VISIBLE);
            mRemoveButton.setVisibility(VISIBLE);
        }
    }

    public void setPassenger(User passenger) {
        this.mPassenger = passenger;
        updateView();
    }

    public void isOwner(boolean owner){
        this.owner = owner;
        showHideActionButtons();
    }

    public void setListener(PassengerItemClickListener listener) {
        this.mListener = listener;
    }

    private void updateView() {
        mPassengerName.setText(mPassenger.getFirstName() + " " + mPassenger.getLastName());
        if (mItemType == TYPE_PENDING) {
            mActionButton.setBackgroundResource(android.R.color.holo_green_light);
            // mActionButton.setImageResource(R.drawable.placeholder);
        } else {
            mActionButton.setBackgroundResource(android.R.color.holo_orange_light);
            // mActionButton.setImageResource(R.drawable.placeholder);
        }
    }

    public void setItemType(int type) {
        this.mItemType = type;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    private PassengerItemClickListener sDummyListener = new PassengerItemClickListener() {
        @Override
        public void onRemoveClicked(User passenger) {

        }

        @Override
        public void onActionClicked(User passenger) {

        }

        @Override
        public void onUserClicked(User passenger) {

        }
    };
}