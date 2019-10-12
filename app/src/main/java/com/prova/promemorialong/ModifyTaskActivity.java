package com.prova.promemorialong;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ModifyTaskActivity extends AppCompatActivity {

    DatabaseHelper db;
    EditText title;
    EditText date;
    EditText time;
    Spinner priority;
    Spinner classify;
    EditText note;
    private Cursor task;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private int id;

    private static final String TAG = "TaskActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.modify));
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);

        title = findViewById(R.id.ETtitle);
        date = findViewById(R.id.ETDate);
        time = findViewById(R.id.ETTime);
        priority = findViewById(R.id.SPNprior);
        classify = findViewById(R.id.SPNclass);
        note = findViewById(R.id.ETNote);

        //recupero l'id del task su cui l'utente ha cliccato
        id = getIntentTask();

        date.setOnClickListener(addDate());

        //serve a visualizzare nell'editText la data selezionata
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("aggiunta data", "dd-mm-yyy: " + month + "-" + day + "-" + year);
                String strDate = day + "-" + month + "-" + year;
                date.setText(strDate);
            }
        };

        time.setOnClickListener(addTime());

        //Serve a visualizzare nell'editText l'ora selezionata
        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d("aggiunta ora", "hh:mm " + hourOfDay + ":" + minute);
                String strTime;
                if (hourOfDay < 10 && minute >= 10) {
                    strTime = "0" + hourOfDay + ":" + minute;
                } else {
                    if (minute < 10 && hourOfDay >= 10) {
                        strTime = hourOfDay + ":" + "0" + minute;
                    } else if (hourOfDay < 10 && minute < 10) {
                        strTime = "0" + hourOfDay + ":" + "0" + minute;
                    } else {
                        strTime = hourOfDay + ":" + minute;
                    }
                }
                time.setText(strTime);
            }


        };
    }


    private int getIntentTask() {
        Log.d(TAG, "Intent ricevuto");
        int id = 0;
        if (getIntent().hasExtra("id")) {
            Log.d(TAG, "Ricevuto extra");
            id = getIntent().getIntExtra("id", 0);
            setTask(id);
        }
        return id;
    }


    //una volta recuperato l'id chiedo al database tutto il resto per quella specifica task
    private void setTask(int id) {

        task = db.getTask(id);
        Log.d(TAG, "setTask" + id);
        Log.d(TAG, "title: " + task);
        if (task.moveToFirst()) {
            title.setText(task.getString(task.getColumnIndex("NAME")));
            long dateTime = task.getLong(task.getColumnIndex("DATETIME"));
            int prior = task.getInt(task.getColumnIndex("PRIORITY"));
            String taskClass = (task.getString(task.getColumnIndex("CLASS")));

            if (dateTime != 0) {
                Date dates = new Date(dateTime);
                DateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
                DateFormat formatter2 = new SimpleDateFormat("HH:mm");
                date.setText(formatter1.format(dates));
                time.setText(formatter2.format(dates));
            } else {
                date.setText("");
                time.setText("");
            }


            switch (prior) {
                case 0:
                    priority.setSelection(0);
                    break;
                case 1:
                    priority.setSelection(1);
                    break;
                case 2:
                    priority.setSelection(2);
                    break;
                case 3:
                    priority.setSelection(3);
                    break;
                default:
                    priority.setSelection(0);
                    break;

            }

            switch(taskClass){
                case "Private":
                    classify.setSelection(0);
                    break;
                case "School":
                    classify.setSelection(1);
                    break;
                case "Work":
                    classify.setSelection(2);
                    break;
                case "Family":
                    classify.setSelection(3);
                    break;
                default:
                        classify.setSelection(0);

            }

            note.setText(task.getString(task.getColumnIndex("NOTE")));

        }


    }

    private View.OnClickListener addDate() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        ModifyTaskActivity.this,
                        mDateSetListener,
                        year, month, day);

                dialog.show();
            }
        };
    }

    private View.OnClickListener addTime() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal2 = Calendar.getInstance();
                int hour = cal2.get(Calendar.HOUR_OF_DAY);
                int minute = cal2.get(Calendar.MINUTE);
                TimePickerDialog dialog2 = new TimePickerDialog(
                        ModifyTaskActivity.this,
                        mTimeSetListener,
                        hour, minute, true);
                dialog2.show();

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idMenu = item.getItemId();
        switch (idMenu) {
            case R.id.save:
                modifyTask(id);
                finish();
                break;
        }
        return false;
    }

    //ci sono arrivato dopo aver premuto tasto di salva sull'action bar.
    //sto salvando
    private void modifyTask(int id) {

        boolean confirm;
        if (title.getText().toString().equals("")) {
            Toast.makeText(ModifyTaskActivity.this, "Inserisci almeno il titolo", Toast.LENGTH_SHORT).show();
        } else {
            try {
                String dateTimeString = (date.getText().toString().trim() + " " + time.getText().toString().trim());
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date date = df.parse(dateTimeString);
                long dateTime = date.getTime();

                confirm = db.updateTask(id, title.getText().toString().trim(), dateTime, priority.getSelectedItemPosition(),classAdapter(classify.getSelectedItemPosition()), note.getText().toString());
                //tornare alla main activity
                if (confirm) {
                    Toast.makeText(ModifyTaskActivity.this, "Promemoria modificato", Toast.LENGTH_SHORT).show();
                }
                modifyNotification(title.getText().toString().trim(),id, dateTime, priority.getSelectedItemPosition());
            } catch (java.text.ParseException e) {
                e.printStackTrace();

                confirm = db.updateTask(id, title.getText().toString().trim(), 0, priority.getSelectedItemPosition(),classAdapter(classify.getSelectedItemPosition()), note.getText().toString());
                if (confirm) {
                    Toast.makeText(ModifyTaskActivity.this, "Promemoria modificato", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String classAdapter(int i) {
        switch (i) {
            case 0: return "Private";
            case 1: return "School";
            case 2: return "Work";
            case 3: return "Family";
            default : return "Private";
        }
    }

    private void modifyNotification(String title, int id, Long dateTime, int priority) {
        NotificationHelper notificationHelper = new NotificationHelper(title,id,dateTime);
        notificationHelper.modifyNotification(this, notificationHelper, priority);
    }

}

