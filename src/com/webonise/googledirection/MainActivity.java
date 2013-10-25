package com.webonise.googledirection;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {
	private GoogleMap map;
	LatLng fromPosition = new LatLng(18.698285, 73.761349);
	LatLng toPosition = new LatLng(17.811456, 74.019527);
	ArrayList<LatLng> directionPoint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeMap();
	}

	private void initializeMap() {
		if (map == null) {
			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			// map.moveCamera(CameraUpdateFactory.newLatLngZoom(new
			// LatLng(41.889,
			// -87.622), 16));

			// You can customize the marker image using images bundled with
			// map.addMarker(new MarkerOptions()
			// .icon(BitmapDescriptorFactory
			// .fromResource(R.drawable.ic_launcher))
			// .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
			// .position(new LatLng(41.889, -87.622)));

	
			// map.addMarker(new MarkerOptions()
			// .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
			// .position(mapCenter)
			// .flat(true)
			// .rotation(245));

			
			// // Animate the change in camera view over 2 seconds
			// map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
			// 2000, null);

			new WebserviceTask(this).execute();

			/* Polylines are useful for marking paths and routes on the map. */
			// map.addPolyline(new PolylineOptions().geodesic(true)
			// .add(new LatLng(18.51209, 73.78156)) // bhavdan cafeday
			// .add(new LatLng(18.50871, 73.79038)) // Fiji
			//
			// );

		}
	}

	public void setDirectionPoints(ArrayList<LatLng> result) {
		directionPoint = new ArrayList<LatLng>();
		directionPoint = result;
	}

	protected void onResume() {
		super.onResume();
		initializeMap();
	}

	public class WebserviceTask extends
			AsyncTask<Void, Void, ArrayList<LatLng>> {
		MainActivity mContext;
		PolylineOptions rectline;

		public WebserviceTask(MainActivity context) {
			this.mContext = context;
		}

		@Override
		protected void onPostExecute(ArrayList<LatLng> result) {
			super.onPostExecute(result);
			if (result != null) {
				rectline = new PolylineOptions().width(3).color(Color.BLUE);

				for (int i = 0; i < result.size(); i++)
					rectline.add(result.get(i));
				map.addPolyline(rectline);
			}
		}

		@Override
		protected ArrayList<LatLng> doInBackground(Void... params) {
			GMapV2Direction md = new GMapV2Direction();
			Document doc = md.getDocument(fromPosition, toPosition,
					GMapV2Direction.MODE_DRIVING);
			if (doc != null) {
				ArrayList<LatLng> directionPoint = md.getDirection(doc);

				rectline = new PolylineOptions().width(3).color(Color.BLUE);

				for (int i = 0; i < directionPoint.size(); i++)
					rectline.add(directionPoint.get(i));

				return directionPoint;
			} else
				return null;
		}

	}
}
