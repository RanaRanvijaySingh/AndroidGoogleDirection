AndroidGoogleDorection
======================
This is a demo project to use the google map to show the direction in google map .
There are few sets of requirements and topics that you need to understand before you begin to play with the google map .

Ployline : This term is not so clear . Let me rephrase it , line joining two points.
		In google map when you have to show the direction between two points then you have to use multiple points and then join all the points to show a complete curved path . 

To obtain these points you have to use the webservice to get the list of point or list of LatLong .

There are few steps to guide you throughout the project .

Step 1: First you need to generate the API key to use the Google map .
	this i have explained in the basic tutorial to use the google map : 

Step 2: If you have not modified the manifest file yet then add the following permissions to the the manifest file. 

Step 3: Create the layout for the map .

Step 4: Create a class with the name “GMapV2Direction.java” . This class will get all the latitude and longitude points that are needed to draw the line on the google map .

Step 5: Create the main Class to use the google map .  This class also contain the AsyncTask  which waits for the server to give back the response.
____________________________________________________________________________________________________________________________________________________________
Step 1: First you need to generate the API key to use the Google map .
	this i have explained in the basic tutorial to use the google map : 
	https://docs.google.com/a/weboniselab.com/document/d/1Zn6KJLHe6H87WXAHcIyTH3bt_vT4cUE2uCWAVK3yMTY/edit

____________________________________________________________________________________________________________________________________________________________
Step 2: If you have not modified the manifest file yet then add the following permissions to the the manifest file. 

    <permission
        android:name="com.webonise.googledirection.permission.MAPS_RECEIVE"
        android:protectionLevel="signature" />

    <uses-permission android:name="com.webonise.googledirection.permission.MAPS_RECEIVE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES" />
    <!-- External storage for caching. -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <!-- My Location -->
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <!-- Maps API needs OpenGL ES 2.0. -->
    <uses-feature
        android:glEsVersion="0x00020000"
        android:required="true" />

    <application
       .....
        <activity
           .....
        </activity>

        <meta-data
            android:name="com.google.android.maps.v2.API_KEY"
            android:value="AIzaSyADAam57g9fED_jKxTKyHP6jzALPSlYybc" />
    </application>

____________________________________________________________________________________________________________________________________________________________

Step 3: Create the layout for the map .

    <fragment
        android:id="@+id/map"
        android:name="com.google.android.gms.maps.SupportMapFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >
    </fragment>
____________________________________________________________________________________________________________________________________________________________
Step 4: Create a class with the name “GMapV2Direction.java” . 
This class will get all the latitude and longitude points that are needed to draw the line on the google map .

For this we hit the google api with the url having the latitude and longitude .
The response received is the Encrypted data.
This encrypted data is then decrypted and the points obtained is given back to the main
activity to use this data to draw the polyline on the map.

Function to get the data from the url with the specific longitude and latitude .

	public Document getDocument(LatLng start, LatLng end, String mode) {
		String url = "http://maps.googleapis.com/maps/api/directions/xml?"
				+ "origin=" + start.latitude + "," + start.longitude
				+ "&destination=" + end.latitude + "," + end.longitude
				+ "&sensor=false&units=metric&mode=driving";
		Log.v("", "**************** " + url);
		try {

			URL weburl = new URL(url);
			InputStream in = weburl.openStream();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(in);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}


The function to get the direction or the list of longitude and latitude .


	public ArrayList<LatLng> getDirection(Document doc) {
		NodeList nl1, nl2, nl3;
		ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
		nl1 = doc.getElementsByTagName("step");
		if (nl1.getLength() > 0) {
			for (int i = 0; i < nl1.getLength(); i++) {
				Node node1 = nl1.item(i);
				nl2 = node1.getChildNodes();

				Node locationNode = nl2
						.item(getNodeIndex(nl2, "start_location"));
				nl3 = locationNode.getChildNodes();
				Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
				double lat = Double.parseDouble(latNode.getTextContent());
				Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
				double lng = Double.parseDouble(lngNode.getTextContent());
				listGeopoints.add(new LatLng(lat, lng));

				locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
				nl3 = locationNode.getChildNodes();
				latNode = nl3.item(getNodeIndex(nl3, "points"));
				ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
				for (int j = 0; j < arr.size(); j++) {
					listGeopoints.add(new LatLng(arr.get(j).latitude, arr
							.get(j).longitude));
				}

				locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
				nl3 = locationNode.getChildNodes();
				latNode = nl3.item(getNodeIndex(nl3, "lat"));
				lat = Double.parseDouble(latNode.getTextContent());
				lngNode = nl3.item(getNodeIndex(nl3, "lng"));
				lng = Double.parseDouble(lngNode.getTextContent());
				listGeopoints.add(new LatLng(lat, lng));
			}
		}

		return listGeopoints;
	}


This is the function to decode the received data.


	private ArrayList<LatLng> decodePoly(String encoded) {
		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			poly.add(position);
		}
		return poly;
	}

____________________________________________________________________________________________________________________________________________________________
Step 5: Create the main Class to use the google map .
This class also contain the AsyncTask  which waits for the server to give back the response

			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			  new WebserviceTask(this).execute();


AsyncTask should be somthing like this . 

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

____________________________________________________________________________________________________________________________________________________________

