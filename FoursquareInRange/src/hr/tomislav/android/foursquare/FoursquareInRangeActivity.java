package hr.tomislav.android.foursquare;

import hr.tomislav.algorithms.Coordinate;
import hr.tomislav.algorithms.GreedyAlgorithm;
import hr.tomislav.algorithms.IShortestRouteAlgorithm;
import hr.tomislav.android.http.HttpRequestAPI;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class FoursquareInRangeActivity extends ListActivity {

	private static final String API_VENUE_SEARCH_URL = "https://api.foursquare.com/v2/venues/search?";
	private static final String TOKEN = "I22HW3HKVJKEEFSDFFVVITOANIZWOVFDJJKUJLBJGTYNUPE4";// Auto
																							// generated
																							// by
																							// Foursquare
																							// API
	private static final String VERSION = "20121016";
	private static final String INTENT = "browse";// radius attribute works only
													// with intent=browse or intent=checkin, refer
													// to Foursquare API
													// documentation
	private static final Integer RADIUS = 1000;// in meters

	Location location;
	
	private JSONObject tokenJson;
	private String coordinates;
	private ArrayList<VenueModel> venuesInRange;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Criteria criteria;
	private String provider;
	private ArrayAdapter<VenueModel> adapter;
	private Button btnCalculateRoute;
	private Context context;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		outState.putParcelable("location", location);
		
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);	
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);

		venuesInRange = new ArrayList<VenueModel>();
		adapter = new VenuesAdapter(this, venuesInRange);
		setListAdapter(adapter);

		location = locationManager.getLastKnownLocation(provider);
		if(savedInstanceState != null) {
			location = savedInstanceState.getParcelable("location");
		}
		if (location != null)
			extractVenuesFromResponse(location);
		setContentView(R.layout.main);
		
		context = this;
		btnCalculateRoute = (Button)findViewById(R.id.btnCalculateRoute);
		btnCalculateRoute.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				ArrayList<VenueModel> choosenVenues = new ArrayList<VenueModel>();
				ArrayList<Coordinate> coordinatesOfChoosenValues = new ArrayList<Coordinate>();
				
				for (VenueModel venueModel : venuesInRange) {
					if(venueModel.isChecked()) {
						choosenVenues.add(venueModel);
						coordinatesOfChoosenValues.add(new Coordinate(venueModel.getGeoPosition().x, venueModel.getGeoPosition().y));
					}
				}
				
				if(choosenVenues.size() > 0) {
					
					IShortestRouteAlgorithm calculatingAlgorithm = new GreedyAlgorithm();
					Coordinate startingPoint = new Coordinate(location.getLatitude(), location.getLongitude());
					ArrayList<Integer> orderedVenues = calculatingAlgorithm.calculateShortestRoute(startingPoint, coordinatesOfChoosenValues);
					StringBuilder orderedVenuesSB = new StringBuilder("[");
					orderedVenuesSB.append("{ \"name\":\"Start \"," +
							"\"lat\":" + location.getLatitude() + "," +
							"\"lng\":" + location.getLongitude() + "},");
					for (Integer index : orderedVenues) {
						orderedVenuesSB.append("{ \"name\":\"" + choosenVenues.get(index).getName() + "\"," +
								"\"lat\":" + choosenVenues.get(index).getGeoPosition().x + "," +
								"\"lng\":" + choosenVenues.get(index).getGeoPosition().y + "},");
					}
					orderedVenuesSB.deleteCharAt(orderedVenuesSB.length()-1);
					orderedVenuesSB.append("]");
					Intent intent = new Intent(context, GoogleMap.class);
					Log.d("JSON", orderedVenuesSB.toString());
					intent.putExtra("Coordiantes", orderedVenuesSB.toString());
					
					startActivity(intent);

				}else {
					Toast.makeText(context, "Choose at least one venue!", 3).show();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		locationManager.removeUpdates(locationListener);
	}

	public class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			extractVenuesFromResponse(loc);
		}

		public void onProviderDisabled(String provider) {
			Log.i("Provider disabled", provider);
		}

		public void onProviderEnabled(String provider) {
			Log.i("Provider enabled", provider);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i("Status changed", provider + ": " + status);
		}
	}

	private void extractVenuesFromResponse(Location loc) {
		location = loc;
		coordinates = loc.getLatitude() + "," + loc.getLongitude();
		if (venuesInRange != null)
			adapter.clear();
		try {
			tokenJson = HttpRequestAPI.executeHttpGet(API_VENUE_SEARCH_URL + "ll="
					+ coordinates + "&intent=" + INTENT + "&radius=" + RADIUS
					+ "&oauth_token=" + TOKEN + "&v=" + VERSION);

			JSONArray venues = (JSONArray) tokenJson.getJSONObject("response")
					.getJSONArray("venues");
			for (int i = 0; i < venues.length(); i++) {
				JSONObject venue = (JSONObject) venues.get(i);
				VenueModel model = new VenueModel();

				if (venue.has("id"))
					model.setID(venue.getString("id"));
				else
					model.setID(null);

				if (venue.has("name"))
					model.setName(venue.getString("name"));
				else
					model.setName(null);

				if (venue.getJSONObject("contact").has("phone"))
					model.setPhone(venue.getJSONObject("contact").getString(
							"phone"));
				else
					model.setPhone(null);

				if (venue.getJSONObject("location").has("address"))
					model.setAddress(venue.getJSONObject("location").getString(
							"address"));
				else
					model.setAddress(null);

				if (venue.getJSONArray("categories").length() > 0) {
					if (((JSONObject) (venue.getJSONArray("categories").get(0)))
							.has("name"))
						model.setCategory(((JSONObject) (venue
								.getJSONArray("categories").get(0)))
								.getString("name"));
					else
						model.setCategory(null);
				}
				else
					model.setCategory(null);
				
				if (venue.getJSONObject("stats").has("checkinsCount"))
					model.setCheckinsCount(venue.getJSONObject("stats").getInt(
							"checkinsCount"));
				else
					model.setCheckinsCount(null);

				if (venue.getJSONObject("location").has("distance"))
					model.setDistance(venue.getJSONObject("location").getInt(
							"distance"));
				else
					model.setDistance(null);
				
				Double lat = venue.getJSONObject("location").getDouble("lat");
				Double lng = venue.getJSONObject("location").getDouble("lng");
				
				model.setGeoPosition(new Coordinate(lat, lng));

				adapter.add(model);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intentForVenueDetails = new Intent(this, VenueDetails.class);
		intentForVenueDetails.putExtra("Venue", adapter.getItem(position));
		startActivity(intentForVenueDetails);
	}

}