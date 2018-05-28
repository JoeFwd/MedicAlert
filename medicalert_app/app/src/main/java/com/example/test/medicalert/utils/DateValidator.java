package com.example.test.medicalert.utils;

import android.util.Log;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateValidator {
    public static String DEFAULT_DATE = "01/01/1970";
    public static String inputFormat = "dd/MM/yyyy";
    public static String dbFormat = "yyyy-MM-dd";

    private DateValidator(){}

    public static String formatDate (String date, String initialFormat, String parseToFormat) throws ParseException {
        Date initDate = new SimpleDateFormat(initialFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(parseToFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }

    public static boolean isDateValid(String date, String format){
        if(date.length() != 10) return false;
        if(!EditTextToolbox.hasOnlyDigits(date.substring(0, 2))) return false;
        if(!date.substring(2, 3).equals("/")) return false;
        if(!EditTextToolbox.hasOnlyDigits(date.substring(3, 5))) return false;
        if(!date.substring(5, 6).equals("/")) return false;
        if(!EditTextToolbox.hasOnlyDigits(date.substring(6, 10))) return false;

        try {
            DateFormat df = new SimpleDateFormat(format);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean hasDatePassed(String date){
        Calendar dateCal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        int day = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(3, 5));
        int year = Integer.parseInt(date.substring(6, 10));

        dateCal.set(Calendar.DAY_OF_MONTH, day);
        dateCal.set(Calendar.MONTH, month - 1); //months are indexed from 0 to 11.
        dateCal.set(Calendar.YEAR, year);

        if(now.get(Calendar.YEAR) > dateCal.get(Calendar.YEAR)) return true;
        if(now.get(Calendar.YEAR) < dateCal.get(Calendar.YEAR)) return false;
        if(now.get(Calendar.MONTH) > dateCal.get(Calendar.MONTH)) return true;
        if(now.get(Calendar.MONTH) < dateCal.get(Calendar.MONTH)) return false;
        if(now.get(Calendar.DAY_OF_MONTH) > dateCal.get(Calendar.DAY_OF_MONTH)) return true;

        return false;
    }
}
