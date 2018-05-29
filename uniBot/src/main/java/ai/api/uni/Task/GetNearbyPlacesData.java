package ai.api.uni.Task;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import ai.api.uni.Activity.MapsActivity;

import static ai.api.uni.Activity.ChatActivity.database;

/**
 * Created by navneet on 23/7/16.
 */
/* REFERENCES:
    https://www.codeproject.com/Articles/1121102/Google-Maps-Search-Nearby-Displaying-Nearby-Places
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/
public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    MapsActivity activity;
    String keyword;

    public GetNearbyPlacesData(MapsActivity activity, String keyword) {
        this.activity = activity;
        this.keyword = keyword;
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            //Toast.makeText(this.activity, "Hold on. I'm loading the places...", Toast.LENGTH_LONG).show();
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result, this.activity);
        ShowNearbyPlaces(nearbyPlacesList);
        if(nearbyPlacesList.size() != 0) Toast.makeText(this.activity,"You can buy " + this.keyword + " on the pinned places.", Toast.LENGTH_LONG).show();
        else Toast.makeText(this.activity,"Oops, I found no nearby places to buy " + keyword + ".", Toast.LENGTH_LONG).show();
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
        if(database.getHelpValue("LAST") == 0) {
            database.setHelpValue("LAST");
        }
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute","Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            mMap.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }
}