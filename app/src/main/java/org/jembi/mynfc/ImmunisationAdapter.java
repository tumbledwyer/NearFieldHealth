package org.jembi.mynfc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.jembi.mynfc.models.Immunisation;

import java.util.ArrayList;

public class ImmunisationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Immunisation> items;
    private final LayoutInflater inflater;

    public ImmunisationAdapter(Context context, ArrayList<Immunisation> items) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.immunisation_list_item, parent, false);
        TextView date = (TextView) rowView.findViewById(R.id.immDate);
        TextView type = (TextView) rowView.findViewById(R.id.immType);
        TextView hcw = (TextView) rowView.findViewById(R.id.immHcw);
        Immunisation immunisation = (Immunisation) getItem(position);
        date.setText(immunisation.Date.toString());
        type.setText(immunisation.Type);

        return rowView;
    }
}
