package ai.api.uni.Activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ai.api.uni.Model.Fee;
import ai.api.uni.Adapter.FeeAdapter;
import ai.api.uni.Model.FeeCategory;
import ai.api.uni.R;

import static ai.api.uni.Activity.ChatActivity.database;

/**
 * Created by Lenovo-G4030 on 4/21/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class FeeUpdateActivity extends BaseActivity {
    private ListView listView;
    private FeeAdapter mAdapter;
    private ArrayList<Fee> feeList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fee_update_layout);


        listView = (ListView) findViewById(R.id.fee_list);

        final Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        Integer id = Integer.valueOf(bundle.getString("id"));

        if(FeeCategory.getByName(name, database).getId() == null){
            finish();
            Toast.makeText(this, "Oops, that has already been deleted.", Toast.LENGTH_LONG).show();
        }
        else {
            feeList = Fee.getUsingCategory(id, database);

            TextView nameView = (TextView) findViewById(R.id.name);
            nameView.setText(name.toUpperCase() + " BILLS");

            mAdapter = new FeeAdapter(this,feeList);
            listView.setAdapter(mAdapter);

            if(feeList.size() == 0) Toast.makeText(this, "You don't have unpaid " + name.toLowerCase() + " bills yet.", Toast.LENGTH_LONG).show();
        }
    }
}
