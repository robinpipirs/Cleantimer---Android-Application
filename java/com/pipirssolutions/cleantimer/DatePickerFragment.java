package com.pipirssolutions.cleantimer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements
DatePickerDialog.OnDateSetListener {

	public interface DatePickerDialogListener {
		public void onDatePicked(DialogFragment dialog, Calendar c,
                                 boolean isFromDate, int year, int month, int day);
	}

	// Use this instance of the interface to deliver action events
	DatePickerDialogListener mListener;

	boolean isFromDate;

	public static DatePickerFragment newInstance(boolean isFromDate) {
		DatePickerFragment instance = new DatePickerFragment();

		instance = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putBoolean("isFromDate", isFromDate);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromDate = getArguments().getBoolean("isFromDate");
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);

        mListener.onDatePicked(this, c, isFromDate, year, month+1, day);
	}

	// Override the Fragment.onAttach() method to instantiate the
	// NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (DatePickerDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
	}
}