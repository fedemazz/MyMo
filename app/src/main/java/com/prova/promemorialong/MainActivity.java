package com.prova.promemorialong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private FloatingActionButton fab;
    private NotificationManagerCompat notificationManager;
    DatabaseHelper db;
    public static final String SHARED_PREFS = "sharedPrefs";
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        recyclerView=findViewById(R.id.recylerView);
        fab=findViewById(R.id.fab);
        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        db = new DatabaseHelper(this);

        notificationManager = NotificationManagerCompat.from(this);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(readData(), this);
        recyclerView.setAdapter(adapter);

        //swipe verso sinistra per eliminare un promemoria
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                deleteTask((int)viewHolder.itemView.getTag());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                Drawable icon;
                final ColorDrawable background;
                icon = ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.ic_delete_white_24dp);
                background = new ColorDrawable(Color.RED);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 100;

                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX < 0) { //swipe verso sinistra
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                }else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
                icon.draw(c);
            }
        }).attachToRecyclerView(recyclerView);

        //swipe verso destra per completare un promemoria
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                completeTask((int)viewHolder.itemView.getTag());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


                Drawable icon;
                final ColorDrawable background;
                icon = ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.ic_check_box_white_24dp);
                background = new ColorDrawable(Color.BLUE);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;

                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX > 0) { // swipe verso destra
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = iconLeft+ icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                }else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
                icon.draw(c);
            }

        }).attachToRecyclerView(recyclerView);

        fab.setOnClickListener(addTask());
    }


    public Cursor readData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int choiceType = sharedPreferences.getInt("sortType", 0);
        String choiceClass =sharedPreferences.getString("sortClass","%");
        Log.d("sort per tipo","" + choiceType);
        Log.d("sort per classe","" + choiceClass);
        return db.getAllTask(choiceType,choiceClass);
    }

    private View.OnClickListener addTask() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MainActivity.this, AddTaskActivity.class);
                MainActivity.this.startActivity(intent);
            }
        };
    }

    private void completeTask(int id){
        Cursor task = db.getTask(id);
        task.moveToPosition(0);
        String title = task.getString(task.getColumnIndex("NAME"));
        Long dateTime = task.getLong(task.getColumnIndex("DATETIME"));
        int priority = task.getInt(task.getColumnIndex("PRIORITY"));
        //metodo sul db
        db.updateStateComplete(id);
        //aggiorno recycler view
        adapter.updateCursor(readData());
        Toast.makeText(MainActivity.this,getString(R.string.task_completed),Toast.LENGTH_LONG).show();
        if (dateTime != 0) {
            deleteNotificationChannel1(title, id, dateTime, priority);
        }
    }

    private void deleteTask(int id){
        Cursor task = db.getTask(id);
        task.moveToPosition(0);
        String title = task.getString(task.getColumnIndex("NAME"));
        Long dateTime = task.getLong(task.getColumnIndex("DATETIME"));
        int priority = task.getInt(task.getColumnIndex("PRIORITY"));
        //metodo sul db
        db.deleteTask(id);
        //aggiorno recycler view
        adapter.updateCursor(readData());
        Toast.makeText(MainActivity.this,getString(R.string.task_deleted),Toast.LENGTH_LONG).show();
        if (dateTime != 0) {
            deleteNotificationChannel1(title, id, dateTime, priority);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        adapter.updateCursor(readData());
        Log.d(TAG, "onResume: ");
    }


    private void deleteNotificationChannel1(String title, int id, Long dateTime, int priority ) {

        NotificationHelper notificationHelper = new NotificationHelper(title,id,dateTime);
        notificationHelper.deleteNotification(this,notificationHelper,priority);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idMenu = item.getItemId();
        switch (idMenu) {
            case R.id.sortAll:
                classSort("%");
                adapter.updateCursor(readData());
                break;
            case R.id.sortPrivate:
                classSort("Private");
                adapter.updateCursor(readData());
                break;
            case R.id.sortSchool:
                classSort("School");
                adapter.updateCursor(readData());
                break;
            case R.id.sortWork:
                classSort("Work");
                adapter.updateCursor(readData());
                break;
            case R.id.sortFamily:
                classSort("Family");
                adapter.updateCursor(readData());
                break;
            case R.id.sort0:
                typeSort(0);
                adapter.updateCursor(readData());
                break;
            case R.id.sort1:
                typeSort(1);
                adapter.updateCursor(readData());
                break;
            case R.id.sort2:
                typeSort(2);
                adapter.updateCursor(readData());
                break;
            case R.id.sort3:
                typeSort(3);
                adapter.updateCursor(readData());
                break;
            case R.id.stats:
                Intent intent = new Intent(this,StatsActivity.class);
                this.startActivity(intent);
                break;
            case (R.id.settings):
                // Creare un'altra voce per aprire l'app nelle impostazioni del telefono
                Intent intent2 = new Intent();
                intent2.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                intent2.setData(uri);
                this.startActivity(intent2);
                break;
        }
        return false;
    }

    public void typeSort(int choice) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("sortType", choice);
        Log.d("stampo tipo ordinamento", "" + choice);
        editor.apply();
    }

    public void classSort(String choice) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sortClass", choice);
        Log.d("stampo sort su classe","" + choice);
        editor.apply();
    }

}
