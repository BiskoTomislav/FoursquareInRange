package hr.tomislav.android.foursquare;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

/**
 * Adapter for list in main activity.
 * @author Tomi
 *
 */
public class VenuesAdapter extends ArrayAdapter<VenueModel> {

	private static final String DISTANCE_UNIT = "m";
	private final List<VenueModel> list;
	private final Activity context;

	public VenuesAdapter(Activity context, List<VenueModel> list) {
		super(context, R.layout.row_layout, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder {
		protected TextView name;
		protected TextView distance;
		protected TextView checkins;
		protected CheckBox checkbox;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.row_layout, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) view.findViewById(R.id.name);
			viewHolder.distance = (TextView) view.findViewById(R.id.distance);
			viewHolder.checkins = (TextView) view.findViewById(R.id.checkins);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							VenueModel element = (VenueModel) viewHolder.checkbox.getTag();
							element.setChecked(buttonView.isChecked());
						}
					});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.name.setText(list.get(position).getName());
		holder.distance.setText(list.get(position).getDistance().toString() + DISTANCE_UNIT);
		holder.checkins.setText(list.get(position).getCheckinsCount().toString());
		holder.checkbox.setChecked(list.get(position).isChecked());
		return view;
	}
}
