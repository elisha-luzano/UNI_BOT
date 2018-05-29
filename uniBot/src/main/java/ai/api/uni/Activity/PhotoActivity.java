package ai.api.uni.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ai.api.uni.Model.Photo;
import ai.api.uni.Adapter.PhotoAdapter;
import ai.api.uni.R;

import static ai.api.uni.Activity.ChatActivity.database;

/**
 * Created by Lenovo-G4030 on 3/8/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class PhotoActivity extends BaseActivity {
    private ListView listView;
    private PhotoAdapter mAdapter;
    private ArrayList<Photo> photoList;
    private Integer folder_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);


        listView = (ListView) findViewById(R.id.gallery);

        final Bundle bundle = getIntent().getExtras();
        folder_id = bundle.getInt("id");
        photoList = Photo.getAllInFolder(folder_id, database);
        Log.i("TO BE DELETED", "FOLDERS" + photoList);

        Log.i("LIST VIEW", "LISTVIEW MEHN" + photoList);
        mAdapter = new PhotoAdapter(this,photoList);
        listView.setAdapter(mAdapter);

        if(photoList.size() == 0)  Toast.makeText(this, "You don't have photos inside this folder yet.", Toast.LENGTH_LONG).show();
        else Toast.makeText(this, "Click on the photo to open it.", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        ArrayList<Photo> photoList = Photo.getAllInFolder(folder_id, database);

        Log.i("LIST VIEW", "LISTVIEW MEHN" + photoList);
        mAdapter = new PhotoAdapter(this,photoList);
        listView.setAdapter(mAdapter);
    }
}
