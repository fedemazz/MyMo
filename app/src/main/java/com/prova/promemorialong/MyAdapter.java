package com.prova.promemorialong;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Cursor tasks;
    private Activity activity;

    public MyAdapter(Cursor tasks, Activity activity) {
        this.tasks = tasks;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        if(!tasks.moveToPosition(position)){
            return;
        }

        //setta la variabile titolo della classe viewHolder
        //uguale al titolo della variabile tasks alla stessa posizione
        viewHolder.title.setText(tasks.getString(tasks.getColumnIndex("NAME")));
        long dateTime = tasks.getLong(tasks.getColumnIndex("DATETIME"));
        int id = tasks.getInt(tasks.getColumnIndex("ID"));
        int prior = tasks.getInt(tasks.getColumnIndex("PRIORITY"));
        String taskClass = tasks.getString(tasks.getColumnIndex("CLASS"));

        viewHolder.itemView.setTag(id);

        if (dateTime != 0) {
            Date date = new Date(dateTime);
            DateFormat formatter1 = new SimpleDateFormat("dd.MM.yyyy");
            DateFormat formatter2 = new SimpleDateFormat("HH:mm");
            viewHolder.date.setText(formatter1.format(date));
            viewHolder.time.setText(formatter2.format(date));
        } else {
            viewHolder.date.setText("");
            viewHolder.time.setText("");
        }

        switch(prior){
            case 0:
                viewHolder.priority.setText("");
                break;
            case 1:
                viewHolder.priority.setText("!");
                break;
            case 2:
                viewHolder.priority.setText("!!");
                break;
            case 3:
                viewHolder.priority.setText("!!!");
                break;
            default:
                viewHolder.priority.setText("");
                break;

        }

        switch(taskClass){
            case "Private":
                viewHolder.imageView.setImageResource (R.drawable.ic_image_class);
                viewHolder.imageView.setColorFilter(Color.MAGENTA);
                //#EC407A
                break;
            case "School":
                viewHolder.imageView.setImageResource (R.drawable.ic_image_class);
                viewHolder.imageView.setColorFilter(Color.YELLOW);
                break;
            case "Work":
                viewHolder.imageView.setImageResource (R.drawable.ic_image_class);
                viewHolder.imageView.setColorFilter(Color.GREEN);
                break;
            case "Family":
                viewHolder.imageView.setImageResource (R.drawable.ic_image_class);
                viewHolder.imageView.setColorFilter(Color.BLUE);
                break;
            default:
                viewHolder.imageView.setImageResource (R.drawable.ic_image_class);
        }


        //set on click listener for each element
        viewHolder.container.setOnClickListener(onClickListener(position,id));
    }

    private View.OnClickListener onClickListener(int position,final int id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ModifyTaskActivity.class);
                intent.putExtra("id",id);
                Log.d("id trovato: ", "" + id);
                activity.startActivity(intent);
            }

        };
    }


    @Override
    public int getItemCount() {
        return tasks.getCount();
    }



    public void updateCursor(Cursor newCursor) {
        if (tasks != null) {
            tasks.close();
        }
        tasks = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView title;
        private TextView date;
        private TextView time;
        private View container;
        private TextView priority;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
            title = view.findViewById(R.id.tvTitle);
            date = view.findViewById(R.id.tvDate);
            time = view.findViewById(R.id.tvTime);
            priority = view.findViewById(R.id.tvPrior);
            container = view.findViewById(R.id.card_view);
        }
    }


}



