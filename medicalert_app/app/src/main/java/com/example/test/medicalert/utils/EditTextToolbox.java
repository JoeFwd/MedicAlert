package com.example.test.medicalert.utils;

import android.text.method.TextKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

public final class EditTextToolbox {

    private EditTextToolbox(){}

    public static void setEditTextToBlank(EditText editText){
        TextKeyListener.clear(editText.getText());
    }

    public static boolean areEmptyFields(int id, View view) {
        RelativeLayout layout = (RelativeLayout) view.findViewById(id);
        for (int i = 0; i < layout.getChildCount(); i++){
            if (layout.getChildAt(i) instanceof EditText) {
                EditText editText = (EditText) layout.getChildAt(i);
                if (editText.getText().toString().equals(""))
                    return true;
            }
        }
        return false;
    }

    public static boolean isEmailValid(String email){
        email = email.trim();
        char emailArray[] = email.toCharArray();
        for(int index=0; index<emailArray.length; index++){
            if(emailArray[index] == '@'){
                if(index == 0 || index == emailArray.length - 1)
                    return false;
                else
                    return true;
            }
            if(emailArray[index] == ' '){
                return false;
            }
        }
        return false;
    }

    public static boolean hasOnlyDigits(String str){
        return str.matches("[0-9]+");
    }
}
