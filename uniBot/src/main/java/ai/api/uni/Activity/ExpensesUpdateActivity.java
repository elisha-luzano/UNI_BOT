package ai.api.uni.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ai.api.uni.Model.Expense;
import ai.api.uni.Model.ExpenseCategory;
import ai.api.uni.Adapter.ExpensesAdapter;
import ai.api.uni.R;

import static ai.api.uni.Activity.ChatActivity.database;
import static ai.api.uni.Activity.ChatActivity.TAG;

/**
 * Created by Lenovo-G4030 on 4/21/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class ExpensesUpdateActivity extends BaseActivity {
    private ListView listView;
    private ExpensesAdapter mAdapter;
    private ArrayList<Expense> expenses_list;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses_update_layout);


        listView = (ListView) findViewById(R.id.expenses_list);

        final Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        final Integer id = Integer.valueOf(bundle.getString("id"));

        if(ExpenseCategory.getOne(name, database).getId() == null){
            finish();
            Toast.makeText(this, "Oops, that has already been deleted.", Toast.LENGTH_LONG).show();
        }

        expenses_list = Expense.getAll(id, database);

        mAdapter = new ExpensesAdapter(this,expenses_list);
        listView.setAdapter(mAdapter);

        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText(name.toUpperCase() + " EXPENSES");

        ImageButton add = (ImageButton) findViewById(R.id.add);
        final EditText amount = (EditText) findViewById(R.id.amount);
        final EditText date = (EditText) findViewById(R.id.date);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Expense.insert(Float.parseFloat(amount.getText().toString()), date.getText().toString(), id, database);
                expenses_list.clear();
                ArrayList<Expense> expenses = Expense.getAll(id, database);
                for(int i=0; i<expenses.size(); i++) {
                    expenses_list.add(expenses.get(i));
                }
                Log.i(TAG, expenses_list + "");
                mAdapter.notifyDataSetChanged();

                amount.setText("0.00");
                date.setText("01/01/2018");
            }
        });
    }
}
