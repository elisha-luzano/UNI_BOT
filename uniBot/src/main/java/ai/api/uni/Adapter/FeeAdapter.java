package ai.api.uni.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ai.api.uni.Model.Fee;
import ai.api.uni.R;

import static ai.api.uni.Activity.ChatActivity.database;

/**
 * Created by Lenovo-G4030 on 4/21/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class FeeAdapter extends ArrayAdapter<Fee> {
    private Context mContext;
    private List<Fee> fees = new ArrayList<>();
    private String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};


    public FeeAdapter(@NonNull Context context, /*@LayoutRes */ArrayList<Fee> list) {
        super(context, 0 , list);
        mContext = context;
        fees = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.fee_update_each_layout,parent,false);

        final Fee currentFee = fees.get(position);

        TextView content = (TextView) listItem.findViewById(R.id.content);
        ImageButton check = (ImageButton) listItem.findViewById(R.id.check);

        content.setText(monthNames[currentFee.getBillMonth()-1]);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            fees.remove(position);
                            Fee.delete(currentFee.getId(), database);
                            notifyDataSetChanged();
                            Toast.makeText(mContext, "Good job! You paid your bill!", Toast.LENGTH_SHORT).show();
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

        return listItem;
    }
}
