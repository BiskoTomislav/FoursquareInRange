package hr.tomislav.android.foursquare;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class GoogleMap extends MapActivity {

	MapView mapView;
	MapController mc;
    GeoPoint pStart, pEnd;
	JSONArray coordinates;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);

		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);

		mapView.displayZoomControls(true);

		mc = mapView.getController();
		Intent intent = getIntent();

		try {
			// Parse JSON array with venues
			coordinates = new JSONArray(intent.getStringExtra("Coordiantes"));
			Log.d("BROJ ZA SPOJIT: ", "BROJ: " + coordinates.length());
			GeoPoint center = new GeoPoint((int)(((JSONObject)coordinates.get(0)).getDouble("lat") * 1E6), 
					  (int)(((JSONObject)coordinates.get(0)).getDouble("lng") * 1E6));
			
			mc.animateTo(center);
			mc.setZoom(17);
			
			// ---Add a location marker---
			MapOverlay mapOverlay = new MapOverlay();
			List<Overlay> listOfOverlays = mapView.getOverlays();
			listOfOverlays.clear();
			listOfOverlays.add(mapOverlay);
	
			mapView.invalidate();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	class MapOverlay extends com.google.android.maps.Overlay {
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

            Point screenPtsStart = new Point();
            
            Bitmap bmpStart = BitmapFactory.decodeResource(
                getResources(), R.drawable.starts);              

            Point screenPtsEnd = new Point();
            
            Bitmap bmpRestaurant = BitmapFactory.decodeResource(
                getResources(), R.drawable.restaurant); 

			Paint mPaint = new Paint();
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color.RED);
			mPaint.setAntiAlias(true);
			mPaint.setStrokeWidth(4);
			
			//Drawing icons and lines, from last venue to our position.
			for (int i = coordinates.length() - 1; i > 0; i--) {

				try {
					pStart = new GeoPoint((int)(((JSONObject)coordinates.get(i)).getDouble("lat") * 1E6), 
										  (int)(((JSONObject)coordinates.get(i)).getDouble("lng") * 1E6));
					pEnd = new GeoPoint((int)(((JSONObject)coordinates.get(i - 1)).getDouble("lat") * 1E6), 
							  			(int)(((JSONObject)coordinates.get(i - 1)).getDouble("lng") * 1E6));
					
					mapView.getProjection().toPixels(pStart, screenPtsStart);
					mapView.getProjection().toPixels(pEnd, screenPtsEnd);
					// Line
		            canvas.drawLine(screenPtsStart.x + 15, screenPtsStart.y +15, screenPtsEnd.x + 15, screenPtsEnd.y + 15, mPaint);
		            // Icon and text
		            canvas.drawBitmap(bmpRestaurant, screenPtsStart.x, screenPtsStart.y, null);
		            canvas.drawText(((JSONObject)coordinates.get(i)).getString("name"), screenPtsStart.x + i, screenPtsStart.y + i, mPaint);
		            // Icon and text
		            canvas.drawBitmap(bmpStart, screenPtsEnd.x, screenPtsEnd.y, null); 
		            
		            
				} catch (JSONException e) {
					e.printStackTrace();
				}						  	
			}
			canvas.drawText("Start", screenPtsEnd.x, screenPtsEnd.y, mPaint);
			super.draw(canvas, mapView, shadow);

			return true;

		}
	}
}
