package com.example.dbm0204.caltracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by dbm0204 on 7/20/17.
 */

public class CalenderDialogFragment extends DialogFragment {
    private long date=0;
    public static CalenderDialogFragment newInstance(long data) {
        CalenderDialogFragment d = new CalenderDialogFragment();
        d.date =data;
        return d;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        GregorianCalendar date = new GregorianCalendar();
        date.setTimeInMillis(this.date);
        DatePickerDialog d = new DatePickerDialog(getActivity(),null,date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DAY_OF_MONTH));
        final DatePicker picker = d.getDatePicker();
        d.setCancelable(true);
        d.setCanceledOnTouchOutside(true);
        picker.setSpinnersShown(false);
        picker.setCalendarViewShown(true);
        d.setButton(Dialog.BUTTON_NEUTRAL,"Cancel",(DialogInterface.OnClickListener) null);
        d.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((CalenderDialogListener) getActivity()).onCalenderDailogDateSelected(new GregorianCalendar(picker.getYear(), picker.getMonth(), picker.getDayOfMonth()));
            }
        });//Default
        d.setButton(Dialog.BUTTON_NEGATIVE, "Today", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((CalenderDialogListener)getActivity()).onCalenderDailogDateSelected(new GregorianCalendar());
            }
        });
        return d;
    }

    public interface CalenderDialogListener{
        void onCalenderDailogDateSelected(GregorianCalendar date);
    }
}
