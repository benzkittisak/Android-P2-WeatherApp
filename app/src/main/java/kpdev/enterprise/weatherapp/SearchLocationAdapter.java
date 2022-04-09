package kpdev.enterprise.weatherapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import kpdev.enterprise.weatherapp.Location;

public class SearchLocationAdapter  extends ArrayAdapter implements Filterable {
    ArrayList<Location> list;
    ArrayList<Location> listAll;
    Activity context;
    int resource;

    public SearchLocationAdapter(Activity context, int resource, ArrayList<Location> list) {

        super(context, resource,list);
        this.context = context;
        this.list = list;
        this.resource = resource;
        this.listAll = new ArrayList<>(list);

    }

    @NonNull
    @Override
    public Filter getFilter() {
        return super.getFilter();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Location w = (Location) getItem(position);

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.search_listview_item,null,true);

        String[] city = w.getName().split(",");

        TextView tvW = rowView.findViewById(R.id.tvSearchResult);
        TextView tvC = rowView.findViewById(R.id.tvCountry);

        tvC.setText(w.getRegion()+" "+w.getCountry());
        tvW.setText(city[0]);

        return rowView;
    }
}
