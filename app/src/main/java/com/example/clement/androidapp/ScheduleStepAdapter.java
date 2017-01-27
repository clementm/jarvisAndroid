package com.example.clement.androidapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clement on 13/01/17.
 */

public class ScheduleStepAdapter extends ArrayAdapter<ScheduleStep> {

    public ScheduleStepAdapter(Context context, List<ScheduleStep> steps) throws JSONException {
        super(context, 0, steps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.step_row,parent, false);
        }

        TodoViewHolder viewHolder = (TodoViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TodoViewHolder();
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);
            viewHolder.effects = (TextView) convertView.findViewById(R.id.effects);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        ScheduleStep step = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        switch (step.getType()) {
            case 0:
                viewHolder.type.setText("Repos");
                viewHolder.avatar.setImageResource(R.drawable.ic_sleep);
                break;

            case 1:
                viewHolder.type.setText("Tâche : "+step.getTask());
                viewHolder.avatar.setImageResource(R.drawable.ic_work);
        }

        viewHolder.effects.setText(step.getEffects());
        viewHolder.duration.setText(String.valueOf(step.getDuration()) + " minutes");

        return convertView;
    }

    private class TodoViewHolder{
        public TextView duration;
        public TextView effects;
        public TextView type;
        public ImageView avatar;
    }
}
