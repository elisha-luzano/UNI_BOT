package ai.api.uni.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ai.api.uni.Model.Expense;
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

public class ExpensesAdapter extends ArrayAdapter<Expense> {
    private Context mContext;
    private List<Expense> expenses = new ArrayList<>();


    public ExpensesAdapter(@NonNull Context context, /*@LayoutRes */ArrayList<Expense> list) {
        super(context, 0 , list);
        mContext = context;
        expenses = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.expenses_update_each_layout,parent,false);

        final Expense currentExpense = expenses.get(position);

        TextView content = (TextView) listItem.findViewById(R.id.content);
        ImageButton delete = (ImageButton) listItem.findViewById(R.id.delete);
        ImageButton edit = (ImageButton) listItem.findViewById(R.id.update);

        final EditText amount = (EditText) listItem.findViewById(R.id.amount);
        final EditText date = (EditText) listItem.findViewById(R.id.date);

        amount.setText(currentExpense.getAmount().toString());
        date.setText(currentExpense.getDate().toString());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                expenses.remove(position);
                                Expense.delete(currentExpense.getId(), database);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, "Deleted.", Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you'd like to mark this as paid? I'll delete it from your list after.")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("Cancel", dialogClickListener).show();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, amount.isEnabled() + "EN");
                if(!amount.isEnabled()) {
                    date.setEnabled(true);
                    amount.setEnabled(true);
                } else {
                    date.setEnabled(false);
                    amount.setEnabled(false);

                    currentExpense.setAmount(Float.parseFloat(amount.getText().toString()));
                    currentExpense.setDate(date.getText().toString());

                    Expense.updateAmount(currentExpense.getId(), Float.parseFloat(amount.getText().toString()), database);
                    Expense.updateDate(currentExpense.getId(), date.getText().toString(), database);
                    Toast.makeText(mContext, "Updated.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return listItem;
    }
}
