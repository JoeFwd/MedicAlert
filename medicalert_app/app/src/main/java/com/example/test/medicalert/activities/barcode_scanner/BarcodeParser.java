package com.example.test.medicalert.activities.barcode_scanner;

import java.util.GregorianCalendar;

public class BarcodeParser {
    private final int cipLength = 13;
    private final int datePreventionLength = 6;
    private String barcode;
    private String cip;
    private GregorianCalendar datePeremption;
    private String numLot;

    public BarcodeParser(String barcode){
        this.barcode = barcode;
        setAllToNull();
    }

    public void parse() throws Exception {
        Exception e = new Exception("Cannot parse" + barcode);
        if(!isSameCharAtPos(this.barcode, '0', 0)){ setAllToNull(); throw e;}
        if(!isSameCharAtPos(this.barcode, '1', 1)){ setAllToNull(); throw e;}
        if(!isSameCharAtPos(this.barcode, '0', 2)){ setAllToNull(); throw e;}
        if(this.barcode.length() > 3 + cipLength){
            this.cip = this.barcode.substring(3, 3 + cipLength);
        } else {
            setAllToNull();
            throw e;
        }
        if(!isSameCharAtPos(this.barcode, '1', 3 + cipLength)){ setAllToNull(); throw e;}
        if(!isSameCharAtPos(this.barcode, '7', 3 + cipLength + 1)){ setAllToNull(); throw e;}
        if(this.barcode.length() > 3 + cipLength + 2 + datePreventionLength){
            int year = Integer.parseInt(this.barcode.substring(3 + cipLength + 2, 3 + cipLength + 4)) + 2000;
            int month = Integer.parseInt(this.barcode.substring(3 + cipLength + 4, 3 + cipLength + 6));
            this.datePeremption = new GregorianCalendar(year, month - 1, 1);
        } else {
            setAllToNull();
            throw e;
        }
        if(!isSameCharAtPos(this.barcode, '1', 3 + cipLength + 2 + datePreventionLength)){ setAllToNull(); throw e;}
        if(!isSameCharAtPos(this.barcode, '0', 3 + cipLength + 2 + datePreventionLength + 1)){ setAllToNull(); throw e;}
        if(this.barcode.length() > 3 + cipLength + 2 + datePreventionLength + 2 + 1){
            this.numLot = this.barcode.substring(3 + cipLength + 2 + datePreventionLength + 2, this.barcode.length());
        } else {
            setAllToNull();
            throw e;
        }
    }

    private boolean isSameCharAtPos(String word, char character, int pos){
        if(word.length() < pos) return false;
        if(word.charAt(pos) == character) return true;
        return false;
    }

    private void setAllToNull(){
        this.cip = null;
        this.datePeremption = null;
        this.numLot = null;
    }

    public GregorianCalendar getDatePeremption() {
        return datePeremption;
    }

    public String getCip() {
        return this.cip;
    }

    public String getNumLot() {
        return numLot;
    }
}
