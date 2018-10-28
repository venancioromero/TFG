package com.venoty.laura.fragments;

import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.venoty.laura.adapters.GlucoseAdapter;
import com.venoty.laura.databases.GlucoseDBHelper;
import com.venoty.laura.models.Glucose;
import com.venoty.laura.runnables.NFCReaderThread;
import com.venoty.laura.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private final String TAG  = "Laura || HomeFragment";
    private final int TIMEOUT = 3000; // miliseconds
    private GlucoseDBHelper db;

    // RecyclerView
    private List<Glucose> glucoseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GlucoseAdapter glucoseAdapter;
    private static TabLayout tabLayout;

    public void setTabLayout(TabLayout tabLayout) { this.tabLayout = tabLayout;  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Instance dbHelper
        db = new GlucoseDBHelper(getContext());

        // Scroll down of current glucose block
        final ScrollView scrollViewCurrentGlucose = view.findViewById(R.id.scrollCurrentGlucose);
        scrollViewCurrentGlucose.post(new Runnable() {
            public void run() {
                scrollViewCurrentGlucose.fullScroll(View.FOCUS_DOWN);
            }
        });

        Intent intent = getActivity().getIntent();

        if (intent != null && "android.nfc.action.TECH_DISCOVERED".equals(intent.getAction())){

            NFCReaderThread nfcReader = new NFCReaderThread(NfcV.get((Tag) intent.getParcelableExtra("android.nfc.extra.TAG")));
            nfcReader.run();

            try {
                nfcReader.join(TIMEOUT); // wait for thread

                Glucose[] measures = nfcReader.getCurrentGlucoseMeasures();

                if (measures != null){
                    ((TextView) view.findViewById(R.id.currentGlucose))
                            .setText(String.valueOf(measures[0].getCalculateGlucose()));
                    ((TextView) view.findViewById(R.id.sevenMinsAgo))
                            .setText(String.valueOf(measures[1].getCalculateGlucose()));
                    ((TextView) view.findViewById(R.id.fourteenMinsAgo))
                            .setText(String.valueOf(measures[2].getCalculateGlucose()));

                    saveCurrentMeasure(measures[0]);
                    saveHistoryMeasures(nfcReader.getHistoryGlucoseMeasures());
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Recycler
        recyclerView   = view.findViewById(R.id.recycler_view);
        glucoseAdapter = new GlucoseAdapter(getContext(),glucoseList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //set adapter to recyclerview
        recyclerView.setAdapter(glucoseAdapter);
        //call method to fetch data from db and add to recyclerview
        prepareGlucoseData();

        return view;
    }

    private void saveCurrentMeasure(Glucose g)   {db.insertMeasure(g);}
    private void saveHistoryMeasures(Glucose[] g){db.insertHistory(g);}

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if (visible && getActivity() != null && tabLayout != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            tabLayout.setVisibility(View.VISIBLE);
        }
    }

    private void prepareGlucoseData() {

        glucoseList.clear();
        List<Glucose> g = db.getAllMeasures();

        if (g.size() > 0)
            for (int i = 0; i < g.size(); i++) {
                glucoseList.add(g.get(i));
                glucoseAdapter.notifyDataSetChanged();
            }
    }

}