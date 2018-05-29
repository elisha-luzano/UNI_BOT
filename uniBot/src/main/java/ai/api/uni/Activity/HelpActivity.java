package ai.api.uni.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ai.api.uni.Activity.BaseActivity;
import ai.api.uni.R;

/**
 * Created by Lenovo-G4030 on 4/18/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);

        Button cont = (Button) findViewById(R.id.cont);

        cont.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
