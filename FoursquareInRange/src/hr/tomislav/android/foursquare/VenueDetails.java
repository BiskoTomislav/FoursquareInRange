package hr.tomislav.android.foursquare;

import java.io.InputStream;
import java.net.URL;

import org.json.JSONObject;

import hr.tomislav.android.http.HttpRequestAPI;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Showing venue details and photo, if aviable.
 * @author Tomi
 *
 */
public class VenueDetails extends Activity {

	private static final String API_VENUE_URL_PREFIX = "https://api.foursquare.com/v2/venues/";
	private static final String API_VENUE_URL_SUFIX = "/photos?group=venue";
	private static final String TOKEN = "I22HW3HKVJKEEFSDFFVVITOANIZWOVFDJJKUJLBJGTYNUPE4";
	private static final String VERSION = "20121019";
	
	private static final String DISTANCE_UNIT = "m";
	
	private TextView name;
	private TextView phone;
	private TextView address;
	private TextView category;
	private TextView checkins;
	private TextView distanece;
	private ImageView venuePhoto;
	
	private JSONObject tokenJson;
	VenueModel choosenVenue;
	Drawable image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.venue_details);
		
		name = (TextView)findViewById(R.id.textViewName);
		phone = (TextView)findViewById(R.id.textViewVenuePhone);
		address = (TextView)findViewById(R.id.textViewVenueAddress);
		category = (TextView)findViewById(R.id.textViewVenueCategory);
		checkins = (TextView)findViewById(R.id.textViewVenueCheckins);
		distanece = (TextView)findViewById(R.id.textViewVenueDistance);
		
		Intent intent = getIntent();
		
		choosenVenue = (VenueModel) intent.getSerializableExtra("Venue");
		
		name.setText(choosenVenue.getName());
		phone.setText(choosenVenue.getPhone());
		address.setText(choosenVenue.getAddress());
		category.setText(choosenVenue.getCategory());
		checkins.setText(choosenVenue.getCheckinsCount().toString());
		distanece.setText(choosenVenue.getDistance().toString() + DISTANCE_UNIT);
		String uri = API_VENUE_URL_PREFIX + choosenVenue.getID() + API_VENUE_URL_SUFIX + "&oauth_token=" + TOKEN + "&v=" + VERSION;
		
		venuePhoto = (ImageView)findViewById(R.id.imageViewVenuePhoto);
		venuePhoto.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
		
		new GetPhotoFromFoursquareApi().execute(uri);
	}
	
	/**
	 * Requesting photo for venue.
	 * @author Tomi
	 *
	 */
	class GetPhotoFromFoursquareApi extends AsyncTask<String, Void, String> {

		// On a new thread!!!
		@Override
		protected String doInBackground(String... params) {
			String uri = params[0];
			try {
				tokenJson = HttpRequestAPI.executeHttpGet(uri);
				int numberOfPhotos = tokenJson.getJSONObject("response").getJSONObject("photos").getInt("count");
				if(numberOfPhotos > 0) {
					String prefix = ((JSONObject)(tokenJson.getJSONObject("response").getJSONObject("photos").getJSONArray("items").get(0))).getString("prefix");
					String suffix = ((JSONObject)(tokenJson.getJSONObject("response").getJSONObject("photos").getJSONArray("items").get(0))).getString("suffix");
					Object content = null;
				    try{
				      URL url = new URL(prefix + "original" + suffix);
				      content = url.getContent();
				    }
				      catch(Exception ex)
				    {
				        ex.printStackTrace();
				    }
				    InputStream is = (InputStream)content;
				    image = Drawable.createFromStream(is, "src");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return uri;
		}

		// UI thread
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			venuePhoto.clearAnimation();
			if(image == null)venuePhoto.setImageDrawable(getResources().getDrawable(R.drawable.not_avaiable));
			else venuePhoto.setImageDrawable(image);
		}
	}
}
