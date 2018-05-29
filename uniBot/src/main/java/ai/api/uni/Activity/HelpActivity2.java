package ai.api.uni.Activity;

/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ai.api.uni.R;

public class HelpActivity2 extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout_2);

        Button cont = (Button) findViewById(R.id.cont);
        cont.setOnClickListener(
            new View.OnClickListener(){

            public void onClick(View v){
                finish();
            }
        });

        final TextView are_there_classes = (TextView) findViewById(R.id.are_there_classes);
        final TextView are_there_classes_next = (TextView) findViewById(R.id.are_there_classes_next);
        final TextView schedule = (TextView) findViewById(R.id.schedule);
        final TextView schedule_next = (TextView) findViewById(R.id.schedule_next);

        final TextView grades = (TextView) findViewById(R.id.grades);
        final TextView grades_next = (TextView) findViewById(R.id.grades_next);

        final TextView tasks = (TextView) findViewById(R.id.tasks);
        final TextView tasks_next = (TextView) findViewById(R.id.tasks_next);

        final TextView expenses = (TextView) findViewById(R.id.expenses);
        final TextView expenses_next = (TextView) findViewById(R.id.expenses_next);

        final TextView folders = (TextView) findViewById(R.id.folders);
        final TextView folders_next = (TextView) findViewById(R.id.folders_next);

        final TextView bills = (TextView) findViewById(R.id.bills);
        final TextView bills_next = (TextView) findViewById(R.id.bills_next);

        final TextView events = (TextView) findViewById(R.id.events);
        final TextView events_next = (TextView) findViewById(R.id.events_next);

        final TextView where_to_buy = (TextView) findViewById(R.id.where_to_buy);
        final TextView where_to_buy_next = (TextView) findViewById(R.id.where_to_buy_next);

        View.OnClickListener atc_lis = new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(HelpActivity2.this, HelpActivity3.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "are_there_classes");
                intent.putExtras(bundle);
                HelpActivity2.this.startActivity(intent);
            }

        };

        View.OnClickListener sched_lis = new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(HelpActivity2.this, HelpActivity3.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "schedule");
                intent.putExtras(bundle);
                HelpActivity2.this.startActivity(intent);
            }

        };

        View.OnClickListener grades_lis = new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(HelpActivity2.this, HelpActivity3.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "grades");
                intent.putExtras(bundle);
                HelpActivity2.this.startActivity(intent);
            }

        };

        View.OnClickListener tasks_lis = new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(HelpActivity2.this, HelpActivity3.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "tasks");
                intent.putExtras(bundle);
                HelpActivity2.this.startActivity(intent);
            }

        };

        View.OnClickListener exp_lis = new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(HelpActivity2.this, HelpActivity3.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "expenses");
                intent.putExtras(bundle);
                HelpActivity2.this.startActivity(intent);
            }

        };

        View.OnClickListener folders_lis = new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(HelpActivity2.this, HelpActivity3.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "folders");
                intent.putExtras(bundle);
                HelpActivity2.this.startActivity(intent);
            }

        };

        View.OnClickListener bills_lis = new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(HelpActivity2.this, HelpActivity3.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "bills");
                intent.putExtras(bundle);
                HelpActivity2.this.startActivity(intent);
            }

        };

        View.OnClickListener events_lis = new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(HelpActivity2.this, HelpActivity3.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "events");
                intent.putExtras(bundle);
                HelpActivity2.this.startActivity(intent);
            }

        };

        View.OnClickListener wtb_lis = new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(HelpActivity2.this, HelpActivity3.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "where_to_buy");
                intent.putExtras(bundle);
                HelpActivity2.this.startActivity(intent);
            }

        };

        are_there_classes.setOnClickListener(atc_lis);
        are_there_classes_next.setOnClickListener(atc_lis);

        schedule.setOnClickListener(sched_lis);
        schedule_next.setOnClickListener(sched_lis);

        grades.setOnClickListener(grades_lis);
        grades_next.setOnClickListener(grades_lis);

        tasks.setOnClickListener(tasks_lis);
        tasks_next.setOnClickListener(tasks_lis);

        expenses.setOnClickListener(exp_lis);
        expenses_next.setOnClickListener(exp_lis);

        folders.setOnClickListener(folders_lis);
        folders_next.setOnClickListener(folders_lis);

        bills.setOnClickListener(bills_lis);
        bills_next.setOnClickListener(bills_lis);

        events.setOnClickListener(events_lis);
        events_next.setOnClickListener(events_lis);

        where_to_buy.setOnClickListener(wtb_lis);
        where_to_buy_next.setOnClickListener(wtb_lis);
    }
}
