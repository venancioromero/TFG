package com.venoty.laura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.venoty.laura.databases.GlucoseDBHelper;
import com.venoty.laura.fragments.MyDatePickerFragment;
import com.venoty.laura.models.Glucose;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ir.mahdi.mzip.zip.ZipArchive;

public class ExportData extends AppCompatActivity {

    private String TAG     ="Laura || ExportData";
    private String CSV_SEP = ",";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);
    }

    public void showDatePicker(View v) {
        DialogFragment newFragment = new MyDatePickerFragment();
        ((MyDatePickerFragment) newFragment).setView(v);
        newFragment.show(getSupportFragmentManager(), "date picker");
    }

    public void cancel(View v){ finish(); }

    public void exportData(View v) throws ParseException {
        EditText from = findViewById(R.id.dateFrom);
        EditText to   = findViewById(R.id.dateTo);
        EditText pass = findViewById(R.id.et_password);

        if (from.getText().toString().matches("") ||
                to.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Please select dates.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Please write password.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        Date date = df.parse(from.getText().toString());
        long from_epoch = date.getTime();

        date = df.parse(to.getText().toString());
        long to_epoch = date.getTime();

        if (to_epoch < from_epoch){
            Toast.makeText(getApplicationContext(), "Please, FROM date should be before that TO date.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        List<Glucose> records = retrieveData(from_epoch,to_epoch);
        String content        = generateCSVContent(records);
        String message        = "Data of glucose from: " +from.getText() + " to: " + to.getText();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("plain/text");
        File data;

        try {
            data = File.createTempFile("Report", ".csv",
                                        getApplicationContext().getExternalCacheDir());

            GenerateCsv.generateCsvFile(data,content);

            ZipArchive zipArchive = new ZipArchive();
            zipArchive.zip(data.getPath(),
                    data.getPath() + ".zip",
                            pass.getText().toString());

            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(data.getPath()+".zip")));
            i.putExtra(Intent.EXTRA_SUBJECT, "Laura data export" );
            i.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(i, "E-mail"));
            Toast.makeText(getApplicationContext(), "Data exported.",
                    Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    private String ts2Date(long ts){
        Timestamp stamp = new Timestamp(ts);
        Date date = new Date(stamp.getTime());
        DateFormat df = new SimpleDateFormat("HH:mm dd/MM");
        return df.format(date);
    }

    private String generateCSVContent(List<Glucose> list){
        String s = "date" + CSV_SEP + "glucose\n";

        for (Glucose g : list)
            s += ts2Date(g.getTimestamp()) + CSV_SEP + g.getMeasure() + "\n";

        return s;
    }

    private List<Glucose> retrieveData(long from_epoch, long to_epoch) {
        GlucoseDBHelper db    = new GlucoseDBHelper(this);
        List<Glucose> records = db.getIntervalHistory(from_epoch,to_epoch);
        db.close();
        return records;
    }

    public static class GenerateCsv {
        static FileWriter generateCsvFile(File sFileName, String fileContent) {
            FileWriter writer = null;

            try {
                writer = new FileWriter(sFileName);
                writer.append(fileContent);
                writer.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return writer;
        }
    }
}
