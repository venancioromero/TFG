package com.venoty.laura.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.venoty.laura.R;
import com.venoty.laura.databases.GlucoseDBHelper;
import com.venoty.laura.models.Glucose;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartFragment extends Fragment {

    private final String TAG = "Laura || ChartFragment";
    private final int DAYS   = 15;

    private static TabLayout tabLayout;
    public void setTabLayout(TabLayout tabLayout) {this.tabLayout = tabLayout;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        LineChart chart = (LineChart) view.findViewById(R.id.chart);

        // Data of
        LineDataSet dataSet = getLineDataSet();

        // Colors
        dataSet.setColor(Color.MAGENTA);
        dataSet.setCircleColor(Color.WHITE);

        LineData lineData = new LineData(dataSet);

        chart.setData(lineData);

        // Disable
        chart.getLegend().setEnabled(false);
        chart.getData().setHighlightEnabled(false);
        chart.getData().setDrawValues(false);
        chart.getData().setValueTextColor(Color.WHITE);
        chart.getXAxis().setTextSize(14);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextSize(14);
        chart.getAxisRight().setTextColor(Color.WHITE);
        chart.getAxisRight().setTextSize(14);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);


        IAxisValueFormatter formatterX = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Timestamp stamp = new Timestamp((long) value);
                Date date = new Date(stamp.getTime());
                DateFormat df = new SimpleDateFormat("HH:mm dd/MM");
                return df.format(date);
            }

        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(formatterX);

        xAxis.setLabelRotationAngle(25);

        chart.zoom(42,1,System.currentTimeMillis(),200);

        chart.moveViewTo(System.currentTimeMillis(),200,null);


        chart.invalidate(); // refresh
        return view;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if  (visible && getActivity() != null ) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            tabLayout.setVisibility(View.GONE);
        }
    }

    private LineDataSet getLineDataSet(){

        GlucoseDBHelper db = new GlucoseDBHelper(getContext());
        List<Glucose> hGlu = db.getHistory(DAYS);
        db.close();

        List<Entry> entries = new ArrayList<>();

        long initTs = System.currentTimeMillis() - ( DAYS * 86400000);
        int  j      = 0;
        Glucose g;

        for (int i = 0 ; i < DAYS * 96 ; i++){

            if (j < hGlu.size() && (hGlu.get(j).getTimestamp() - (initTs + i * 901000))< 0) {
                g = hGlu.get(j);
                j++;
            }else{
                g = new Glucose();
                g.setTimestamp(initTs + i * 900000);
            }
            //Log.d(TAG,g.toString());
            entries.add(new Entry(g.getTimestamp(),g.getMeasure()));
        }

        return new LineDataSet(entries,"");
    }
}