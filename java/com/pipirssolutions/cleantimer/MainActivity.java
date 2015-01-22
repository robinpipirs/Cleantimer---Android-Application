package com.pipirssolutions.cleantimer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.pipirssolutions.cleantimer.DatePickerFragment.DatePickerDialogListener;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by robinpipirs on 15-01-04.
 */
public class MainActivity extends ActionBarActivity implements DatePickerDialogListener, PopupMenu.OnMenuItemClickListener {

    //Selected dates in datepicker
    public int daySelected, monthSelected, yearSelected;
    //current dates etc
    int thisYear, thisMonth, thisDay, thisHour;

    // Easter egg
    private int easterCounter;

    private boolean easterNotEnabled;
    private boolean easterSaved;

    Button teacupButton;

    // Calculated variables
    public  int years,months,days,hours,minutes,seconds;

    private String totaltime,name;

    //saved data
    private SharedPreferences sharedPref;

    private Days totaldays;

    //textview
    TextView serenityTime;

    TextView namefield;

    //handler
    final Handler myHandler = new Handler();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //easter Egg shit
        easterCounter =0;
        easterNotEnabled = true;
        //give the button an id
        teacupButton =(Button) findViewById(R.id.settings_button);
        teacupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //easter Egg counter ++
                easterCounter++;
                easter(v);
                showPopup(v);
            }
        });

        serenityTime = (TextView) findViewById(R.id.counter_view);
        namefield = (TextView) findViewById(R.id.name_text);


        //check if theres saved data
        //TODO bugged when loading some values

        sharedPref= getSharedPreferences("mypref", 0);
        yearSelected = sharedPref.getInt("year",2015);
        monthSelected = sharedPref.getInt("month", 1);

        easterSaved = sharedPref.getBoolean("easterEgg",false);

        if(easterSaved){
            teacupButton.setBackgroundResource(R.drawable.button_teacup_chihuahua);
        }

        if(monthSelected == 0)
        {
            monthSelected =1;
        }
        daySelected = sharedPref.getInt("day",1);
        name = sharedPref.getString("name", "click the teacup");
        namefield.setText(name);



        //Timer that updates the time.
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {UpdateGUI();}
        }, 0, 1000);

    }

    //easterEgg
    private void easter(View v)
    {
        if(easterCounter == 10 && easterNotEnabled)
        {
            easterCounter= 0;
            easterNotEnabled = false;
            //change button xml
            teacupButton.setBackgroundResource(R.drawable.button_teacup_chihuahua);

            //Save data

            //get Editor
            SharedPreferences.Editor editor= sharedPref.edit();
            //put your value
            editor.putBoolean("easterEgg", true);
            //commits your edits
            editor.commit();
        }
        else if(easterCounter == 10 && (easterNotEnabled == false))
        {
            easterCounter= 0;
            easterNotEnabled = true;
            teacupButton.setBackgroundResource(R.drawable.button_teacup);

            //Save data

            //get Editor
            SharedPreferences.Editor editor= sharedPref.edit();
            //put your value
            editor.putBoolean("easterEgg", false);
            //commits your edits
            editor.commit();
        }

    }
    //updater
    private void UpdateGUI() {
        //tv.setText(String.valueOf(i)); //This causes a runtime error.
        myHandler.post(myRunnable);
    }


    //time calculator timed!!
    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            if(yearSelected != 0){
                timeBetweenCalculator();
                serenityTime.setText(String.valueOf(totaltime));
            }
        }
    };





    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.set_time:
                setTime();
                return true;
            case R.id.set_name:
                setName();
                return true;
            case R.id.show_days:
                showDays();
                return true;
            default:
                return false;
        }

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.options_menu);
        popup.show();
    }

    //Set time variables
    private void setTime() {

        showFromDatePickerDialog();
        //dateField();
    }

    public void showFromDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFromDate", true);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //opens fragment to set name
    public void setName() {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        final EditText userinput = new EditText(this);
        builder.setView(userinput);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        //name = userinput.getText().toString();

        // Add the buttons
        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                name =  userinput.getText().toString();
                Toast.makeText(getApplicationContext(), "name is set to: " + name, Toast.LENGTH_LONG).show();

                //Save data

                //get Editor
                SharedPreferences.Editor editor= sharedPref.edit();
                //put your value
                editor.putString("name", name);
                //commits your edits
                editor.commit();
                //set name
                namefield.setText(name);

            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties


        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    public void timeBetweenCalculator(){

        //calendar current time
        Calendar c = Calendar.getInstance();
        //c.set(Calendar.ZONE_OFFSET,1);
        thisYear = c.get(Calendar.YEAR);
        //Month format is 0-11 thats why we add 1
        thisMonth = c.get(Calendar.MONTH) +1;
        thisDay = c.get(Calendar.DAY_OF_MONTH);
        thisHour = c.get(Calendar.HOUR_OF_DAY);

        //TODO remove these
        int thisMinute = c.get(Calendar.MINUTE);
        int thisSecond = c.get(Calendar.SECOND);


        // interval from start to end
        //calculates time between now and present
        DateTime start = new DateTime(yearSelected,monthSelected,daySelected, 0, 0);
        DateTime end = new DateTime(thisYear, thisMonth, thisDay, thisHour, thisMinute, thisSecond);

        //Period without weeks
        PeriodType monthDay = PeriodType.yearMonthDayTime();
        Period period = new Period(start,end, monthDay);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendYears()
                .appendLiteral(":")
                .appendMonths()
                .appendLiteral(":")
                .appendDays()
                .appendLiteral(":")
                .appendHours()
                .appendLiteral(":")
                .appendMinutes()
                .appendLiteral(":")
                .appendSeconds()
                .toFormatter();

        period.toString(formatter);
        //do not include this day

        // able to calculate whole months between two dates easily

        totaldays = Days.daysBetween(start, end);


        //display time between
        totaltime = period.toString(formatter);

        /*totaltime = period.getYears() +" : "+ period.getMonths()+" : " + period.getDays() +" : "+ period.getHours() + " : " + period.getMinutes();
*/

    }

    public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {


    }




    @Override
    @SuppressLint("SimpleDateFormat") public void onDatePicked(DialogFragment dialog, Calendar c,
                                                               boolean isFromDate, int year, int month, int day) {
        String strdate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");


        if (c != null) {
            //TODO find better solution then suppressing static warning for calendar c
            strdate = sdf.format(c.getTime());
            yearSelected = year;
            monthSelected = month;
            daySelected = day;

            //Save data

            //get Editor
            SharedPreferences.Editor editor= sharedPref.edit();
            //put your value
            editor.putInt("year", yearSelected);
            editor.putInt("month", monthSelected);
            editor.putInt("day", daySelected);

            //commits your edits
            editor.commit();


        }
        if (isFromDate) {
            Toast.makeText(getApplicationContext(), strdate, Toast.LENGTH_LONG).show();

            //serenityTime.setText(strdate);
        }
    }


    private void showDays() {
        // TODO Auto-generated method stub
        int totaldaysint = totaldays.getDays();
        Toast.makeText(getApplicationContext(), "You have been clean for: " + totaldaysint + " days", Toast.LENGTH_LONG).show();

    }


    /*
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_main, container, false);
            return rootView;
        }
    }*/




}
