package com.hsl_firebase.alves.pap_firebase_hsl;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserTasksAdapter extends ArrayAdapter<usertasks> {

    private Activity context;

    private List<usertasks> usertaskss;

    public UserTasksAdapter(Activity context, List<usertasks>usertasks){
        super(context, R.layout.list_view, usertasks);
        this.context = context;
        this.usertaskss = usertasks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listview = inflater.inflate(R.layout.list_view,null,true);

        TextView taskname = (TextView)listview.findViewById(R.id.name_task);

        TextView tasklocal = (TextView)listview.findViewById(R.id.task_local);

        TextView taskdate = (TextView)listview.findViewById(R.id.task_date);

        usertasks usertasks = usertaskss.get(position);

        taskname.setText(usertasks.getTaskname());

        tasklocal.setText(usertasks.getTasklocal());

        taskdate.setText(usertasks.getTaskdate());

        return listview;
    }

}
