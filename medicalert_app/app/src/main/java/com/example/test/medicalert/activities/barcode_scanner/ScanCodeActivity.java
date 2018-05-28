package com.example.test.medicalert.activities.barcode_scanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.test.medicalert.Traitement;
import com.example.test.medicalert.activities.AjouterMedicamentDansTraitementActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCodeActivity extends Activity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView scannerView;
    private ArrayList<HashMap<String, String>> medicamentList;
    private  Traitement traitement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            medicamentList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("medicamentList");
            traitement = getIntent().getParcelableExtra(Traitement.CLASS_TAG);
        } else {
            Log.e("Error", "No bundle", new Exception("AjouterMedicamentDansTraitementActivity needs a Traitement object and a ArrayList<HashMap<String, String>> as Extras."));
        }

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        scannerView = new ZXingScannerView(this);
        List formats = new ArrayList<BarcodeFormat>();
        formats.add(BarcodeFormat.DATA_MATRIX);
        scannerView.setFormats(formats);
        scannerView.setAutoFocus(true);
        setContentView(scannerView);
    }

    @Override
    public void onPause(){
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    /**
     * Parse le resultat du scanner sous forme de "cip", "date" et "lot" et est envoye à l'intention de l'activity <code>nextActivity</code>.
     * Si le parsing échoue, alors tous les champs sont égaux à <code>null</code>
     * @param result
     */
    @Override
    public void handleResult(Result result){
        String resultCode = result.getText();
        Toast.makeText(ScanCodeActivity.this, resultCode, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(ScanCodeActivity.this, AjouterMedicamentDansTraitementActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        BarcodeParser p = new BarcodeParser(resultCode.substring(1)); //Parse datamatrix separator FNC1
        try {
            p.parse();
            i.putExtra("cip", p.getCip());
            i.putExtra("date", p.getDatePeremption());
            i.putExtra("lot", p.getNumLot());
        } catch (Exception e) {
            i.putExtra("cip", (String) null);
            i.putExtra("date", (Calendar) null);
            i.putExtra("lot", (String) null);
        }

        i.putExtra(Traitement.medicamentListKey, medicamentList);
        i.putExtra(Traitement.CLASS_TAG, traitement);
        startActivity(i);
        finish();
    }
}
