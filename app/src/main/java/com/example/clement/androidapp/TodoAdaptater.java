package com.example.clement.androidapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by clement on 13/01/17.
 */

public class TodoAdaptater extends ArrayAdapter<Todo> {

    public TodoAdaptater(Context context, List<Todo> todos) throws JSONException {
        super(context, 0, todos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_row,parent, false);
        }

        TodoViewHolder viewHolder = (TodoViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TodoViewHolder();
            viewHolder.content = (TextView) convertView.findViewById(R.id.content);
            viewHolder.priority = (TextView) convertView.findViewById(R.id.priority);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        Todo todo = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.content.setText(todo.getContent());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        viewHolder.priority.setText(format.format(todo.getDueDate()));

        switch(todo.getPriority()) {
            case 4:
                viewHolder.avatar.setColorFilter(0xffBB1515, PorterDuff.Mode.MULTIPLY );
                break;

            case 3:
                viewHolder.avatar.setColorFilter(0xffFF8D12, PorterDuff.Mode.MULTIPLY );
                break;

            case 2:
                viewHolder.avatar.setColorFilter(0xffFFE812, PorterDuff.Mode.MULTIPLY );
                break;

            case 1:
                viewHolder.avatar.setColorFilter(0xffE5E5E5, PorterDuff.Mode.MULTIPLY );
                break;
        }

        return convertView;
    }

    private class TodoViewHolder{
        public TextView content;
        public TextView priority;
        public ImageView avatar;
    }
}
