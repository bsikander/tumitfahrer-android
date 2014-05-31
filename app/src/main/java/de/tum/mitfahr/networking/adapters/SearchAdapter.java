package de.tum.mitfahr.networking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.tum.mitfahr.R;
import de.tum.mitfahr.networking.models.Ride;

/**
 * Created by amr on 31/05/14.
 */
public class SearchAdapter extends ArrayAdapter<Ride> {

    private Context mContext;
    private ArrayList<Ride> searchResults;

    public SearchAdapter(Context context, ArrayList<Ride> searchResults) {

        super(context, R.layout.search_item, searchResults);
        this.mContext = context;
        this.searchResults = searchResults;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.searchItemTextView);
        textView.setText("" + searchResults.get(position).getId());
        return rowView;
    }
}
