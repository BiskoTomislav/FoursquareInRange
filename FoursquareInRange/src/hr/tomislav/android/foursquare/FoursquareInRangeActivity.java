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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Main activity.
 * 
 * @author Tomi
 * 
 */
public class FoursquareInRangeActivity extends ListActivity {

	private static final String API_VENUE_SEARCH_URL = "https://api.foursquare.com/v2/venues/search?";
	private static final String CATEGORY_SEARCH_URL = "https://api.foursquare.com/v2/venues/categories?oauth_token=I22HW3HKVJKEEFSDFFVVITOANIZWOVFDJJKUJLBJGTYNUPE4&v=20121028";
	private static final String TOKEN = "I22HW3HKVJKEEFSDFFVVITOANIZWOVFDJJKUJLBJGTYNUPE4";// Auto
																							// generated
	private static final String VERSION = "20121016";

	private static final Integer RADIUS = 1000;// in meters

	private Location location;
	private JSONObject tokenJson;
	private String coordinates;
	private ArrayList<VenueModel> venuesInRange;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Criteria criteria;
	//private String provider;
	private ArrayAdapter<VenueModel> adapter;
	private Button btnCalculateRoute;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		//provider = locationManager.getBestProvider(criteria, false);

		venuesInRange = new ArrayList<VenueModel>();
		adapter = new VenuesAdapter(this, venuesInRange);
		setListAdapter(adapter);

		// Get last location if known
		location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (savedInstanceState != null) {
			// If we saved our location, use it. Rotating screen is on case when
			// we need this.
			location = savedInstanceState.getParcelable("location");
		}
		if (location != null) {
			// If we have location, fill adapter with venues from response.
			new Networking().execute("");
		}

		context = this;
		btnCalculateRoute = (Button) findViewById(R.id.btnCalculateRoute);
		btnCalculateRoute.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ArrayList<VenueModel> choosenVenues = new ArrayList<VenueModel>();
				ArrayList<Coordinate> coordinatesOfChoosenValues = new ArrayList<Coordinate>();

				// Find venues that user selected to include in route.
				for (VenueModel venueModel : venuesInRange) {
					if (venueModel.isChecked()) {
						choosenVenues.add(venueModel);
						coordinatesOfChoosenValues.add(new Coordinate(
								venueModel.getGeoPosition().x, venueModel
										.getGeoPosition().y));
					}
				}

				// Calculeted shortest route and send JSONArray to map activity.
				if (choosenVenues.size() > 0) {

					IShortestRouteAlgorithm calculatingAlgorithm = new GreedyAlgorithm();
					Coordinate startingPoint = new Coordinate(location
							.getLatitude(), location.getLongitude());
					ArrayList<Integer> orderedVenues = calculatingAlgorithm
							.calculateShortestRoute(startingPoint,
									coordinatesOfChoosenValues);
					// Building JSONarray with venue names and coordinates
					StringBuilder orderedVenuesSB = new StringBuilder("[");
					orderedVenuesSB.append("{ \"name\":\"Start \","
							+ "\"lat\":" + location.getLatitude() + ","
							+ "\"lng\":" + location.getLongitude() + "},");
					for (Integer index : orderedVenues) {
						orderedVenuesSB.append("{ \"name\":\""
								+ choosenVenues.get(index).getName() + "\","
								+ "\"lat\":"
								+ choosenVenues.get(index).getGeoPosition().x
								+ "," + "\"lng\":"
								+ choosenVenues.get(index).getGeoPosition().y
								+ "},");
					}
					orderedVenuesSB.deleteCharAt(orderedVenuesSB.length() - 1);
					orderedVenuesSB.append("]");
					Intent intent = new Intent(context, GoogleMap.class);
					intent.putExtra("Coordiantes", orderedVenuesSB.toString());

					startActivity(intent);

				} else {
					Toast.makeText(context, "Choose at least one venue!", 3)
							.show();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// register for location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		locationManager.removeUpdates(locationListener);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putParcelable("location", location);
		super.onSaveInstanceState(outState);
	}

	class Networking extends AsyncTask<String, Void, String> {

		private static final String FOOD_CATEGORY_ID = "4d4b7105d754a06374d81259"; // temp food category id, app checks for newest version.

		// On a new thread!!!
		@Override
		protected String doInBackground(String... params) {
			String response = "{}";
			try {
				coordinates = location.getLatitude() + "," + location.getLongitude();
				Log.d("TEST SPEED", "Requesting categories ...");
				JSONObject responseCategory = new JSONObject(HttpRequestAPI.executeHttpGet(CATEGORY_SEARCH_URL));
				JSONArray categories = (JSONArray) responseCategory.getJSONObject("response").getJSONArray("categories");
				String foodCategoryID = FOOD_CATEGORY_ID;
				Log.d("TEST SPEED", "Searching ...");
				for (int i = 0; i < categories.length(); i++) {
					if(((JSONObject)categories.get(i)).getString("name").equals("Food")) {
						foodCategoryID = ((JSONObject)categories.get(i)).getString("id");
						break;
					}
				}
				Log.d("TEST SPEED", "Found ...");
				response = HttpRequestAPI.executeHttpGet(API_VENUE_SEARCH_URL
						+ "ll=" + coordinates 
						+ "&categoryId=" + foodCategoryID  
						+ "&radius=" + RADIUS 
						+ "&oauth_token=" + TOKEN 
						+ "&v=" + VERSION);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		// UI thread
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			extractVenuesFromResponse(result);
		}
	}

	/**
	 * Location listener class.
	 * 
	 * @author Tomi
	 * 
	 */
	public class MyLocationListener implements LocationListener {

		public void onLocationChanged(final Location loc) {
			location = loc;
			new Networking().execute("");
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
	
	/**
	 * Calling activity with venues details.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intentForVenueDetails = new Intent(this, VenueDetails.class);
		intentForVenueDetails.putExtra("Venue", adapter.getItem(position));
		startActivity(intentForVenueDetails);
	}

	/**
	 * Method for parsing JSON response from foursquareAPI.
	 * 
	 * @param loc
	 */
	private void extractVenuesFromResponse(String response) {
		
		Log.i("LOG", response);
		if (venuesInRange != null)
			adapter.clear();
		try {
			tokenJson = new JSONObject(response);

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
				} else
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

	

}