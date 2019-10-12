package com.prova.promemorialong;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    DatabaseHelper db;
    EditText title;
    EditText date;
    EditText time;
    Spinner priority;
    Spinner classify;
    EditText note;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private static final String TAG = "AddTaskActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.create));
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



        date.setOnClickListener(addDate());

        //serve a visualizzare nell'editText la data selezionata
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "dd-mm-yyy: " + month + "-" + day + "-" + year);
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



    private View.OnClickListener addDate() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        AddTaskActivity.this,
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
                        AddTaskActivity.this,
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
                Boolean confirm = addTask();
                if (confirm) finish();
                break;
        }
        return false;
    }

    //ci sono arrivato dopo aver premuto tasto di salva sull'action bar.
    //sto salvando
    private boolean addTask() {

        boolean confirm = false;
        if (title.getText().toString().equals("")) {
            Toast.makeText(AddTaskActivity.this, getString(R.string.control_on_title), Toast.LENGTH_SHORT).show();
        } else {
            try {
                //qui entra se l'utente ha inserito la data
                String dateTimeString = (date.getText().toString().trim() + " " + time.getText().toString().trim());
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date date = df.parse(dateTimeString);
                long dateTime = date.getTime();

                confirm = addToDb(title.getText().toString().trim(), dateTime, priority.getSelectedItemPosition(), classAdapter(classify.getSelectedItemPosition()),note.getText().toString());

                int idNotify = db.getLastId();
                Log.d("ultimo id inserito add", "" + idNotify);

                createNotification(title.getText().toString().trim(), idNotify, dateTime, priority.getSelectedItemPosition());

            } catch (java.text.ParseException e) {
                //qui entra se non l'ha inserita
                e.printStackTrace();
                confirm = addToDb(title.getText().toString().trim(), 0, priority.getSelectedItemPosition(), classAdapter(classify.getSelectedItemPosition()),note.getText().toString());
            }
        }
        return confirm;
    }

    private boolean addToDb(String title, long dateTime,int priority,String classify,String note){
        boolean isInserted = db.insertTask(title,dateTime,priority,classify,note);
        if(isInserted == true){
            Toast.makeText(AddTaskActivity.this,getString(R.string.task_added),Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(AddTaskActivity.this,getString(R.string.task_not_added),Toast.LENGTH_LONG).show();
        return isInserted;
    }

    private String classAdapter(int i) {
        switch (i) {
            case 0:
                return "Private";
            case 1:
                return "School";
            case 2:
                return "Work";
            case 3:
                return "Family";
            default:
                return "Private";
        }
    }

    private void createNotification(String title, int id, Long dateTime, int priority ) {
        //Creazione della notifica
        //Per prima cosa creo un ogetto della classe NotificationHelper, questa classe possiede
        //metodi per la creazione e l'eliminazione delle notifiche
        //Qui per crearla chiamo infatti createNotification sull'oggetto notificationHelper appena creato
        NotificationHelper notificationHelper = new NotificationHelper(title,id,dateTime);
        notificationHelper.createNotification(this,notificationHelper,priority);

    }
}
