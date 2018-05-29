package ai.api.uni.Activity;

import android.os.Bundle;
import android.widget.TextView;

import ai.api.uni.Activity.BaseActivity;
import ai.api.uni.R;

/**
 * Created by Lenovo-G4030 on 4/19/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class HelpActivity3 extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout_3);

        TextView header = (TextView) findViewById(R.id.header);
        TextView content = (TextView) findViewById(R.id.content);

        final Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            final String type = bundle.getString("type");

            if(type.equals("are_there_classes")) {
                header.setText("Show class suspensions");
                content.setText("'Are there classes today?'\n" +
                        "'Show class suspensions in Laguna.'\n" +
                        "'Are classes cancelled today?'\n" +
                        "'Is there school today?'");
            }

            else if(type.equals("schedule")) {
                header.setText("Show schedule and attendance");
                content.setText("Get your schedule from SAIS\n" +
                        "\t\t\t'Show my schedule.'\n" +
                        "\t\t\t'Load my classes from SAIS'\n" +
                        "\t\t\t'How many absences do I have?'\n\n" +
                        "Record your absences\n" +
                        "\t\t\t'I missed my class today!'\n" +
                        "\t\t\t'I have 2 absences in ENG1.'\n" +
                        "\t\t\t'I didn't go to ENG2 today.'");
            }

            else if(type.equals("grades")) {
                header.setText("Show your grades easily");
                content.setText("Get your grades from SAIS\n" +
                        "\t\t\t'Show my grades.'\n" +
                        "\t\t\t'Please load my grades from SAIS.'\n" +
                        "\t\t\t'Grades.'");
            }

            else if(type.equals("tasks")) {
                header.setText("Manage your tasks");
                content.setText("Add to your calendar\n" +
                        "\t\t\t'I have an exam tomorrow at 1PM.'\n" +
                        "\t\t\t'Schedule a meeting.'\n" +
                        "\t\t\t'Add a task.'");
            }

            else if(type.equals("expenses")) {
                header.setText("Keep track of your expenses");
                content.setText("Manage your expenses\n" +
                        "\t\t\t'I spent 200 on food today!'\n" +
                        "\t\t\t'Add transportation to my expenses.'\n" +
                        "\t\t\t'Delete my water expenses.'");
            }

            else if(type.equals("folders")) {
                header.setText("Manage your photos");
                content.setText("File your photos inside a folder easily\n" +
                        "\t\t\t'Open camera.'\n" +
                        "\t\t\t'Add a photo to my ENG2 folder.'\n" +
                        "\t\t\t'Create a new folder.'");
            }

            else if(type.equals("bills")) {
                header.setText("Remind you of your bills");
                content.setText("Never forget to pay them again!\n" +
                        "\t\t\t'My water bill comes every 2nd day of the month.'\n" +
                        "\t\t\t'Add rent to my bills.'\n" +
                        "\t\t\t'I paid my water bills.'");
            }

            else if(type.equals("events")) {
                header.setText("Show you upcoming events");
                content.setText("Easily add them on your calendar\n" +
                        "\t\t\t'Show events in Santa Rosa'\n" +
                        "\t\t\t'Are there any upcoming events?'\n" +
                        "\t\t\t'Upcoming events in Taguig'");
            }

            else if(type.equals("where_to_buy")) {
                header.setText("Show you the nearest places to buy something");
                content.setText("And easily navigate going there\n" +
                        "\t\t\t'Where is the nearest coffee shop?'\n" +
                        "\t\t\t'Where to buy chicken?'\n" +
                        "\t\t\t'I want to buy flowers.'");
            }
        }
    }

}
