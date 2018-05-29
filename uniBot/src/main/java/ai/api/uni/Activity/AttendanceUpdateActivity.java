package ai.api.uni.Activity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import ai.api.uni.R;
import ai.api.uni.Model.Subject;
import ai.api.uni.Adapter.SubjectsAdapter;

import static ai.api.uni.Activity.ChatActivity.database;

/**
 * Created by Lenovo-G4030 on 4/23/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class AttendanceUpdateActivity extends BaseActivity {
    private ListView listView;
    private SubjectsAdapter mAdapter;
    private ArrayList<Subject> subjects;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_update_layout);

        listView = (ListView) findViewById(R.id.attendance_list);

        subjects = Subject.getAll(database);

        mAdapter = new SubjectsAdapter(this, subjects);
        listView.setAdapter(mAdapter);

    }
}
