package ai.api.uni.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import ai.api.uni.Model.Subject;
import ai.api.uni.R;

import static ai.api.uni.Activity.ChatActivity.database;

/**
 * Created by Lenovo-G4030 on 4/23/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class SubjectsAdapter extends ArrayAdapter<Subject> {
    private Context mContext;
    private List<Subject> subjects = new ArrayList<>();

    public SubjectsAdapter(@NonNull Context context, /*@LayoutRes */ArrayList<Subject> list) {
        super(context, 0 , list);
        mContext = context;
        subjects = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.attendance_update_each_layout,parent,false);

        final Subject currentSubject = subjects.get(position);

        TextView subject = (TextView) listItem.findViewById(R.id.subject);
        final EditText absences = (EditText) listItem.findViewById(R.id.absences);
        ImageButton update = (ImageButton) listItem.findViewById(R.id.update);

        subject.setText(currentSubject.getName());
        absences.setText(String.valueOf(currentSubject.getAbsences()));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!absences.isEnabled()) {
                    absences.setEnabled(true);
                } else {
                    absences.setEnabled(false);
                    //absences.setBackgroundResource(android.R.drawable.ic_menu_edit);
                    currentSubject.setAbsences(Integer.valueOf(absences.getText().toString()));
                    Subject.updateAbsence(currentSubject.getId(), Integer.valueOf(absences.getText().toString()), database);
                    Toast.makeText(mContext, "Updated.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return listItem;
    }
}
