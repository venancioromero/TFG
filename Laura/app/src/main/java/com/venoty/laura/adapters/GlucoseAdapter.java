package com.venoty.laura.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.venoty.laura.R;
import com.venoty.laura.models.Glucose;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GlucoseAdapter extends RecyclerView.Adapter<GlucoseAdapter.MyViewHolder> {

    private final String TAG  = "Laura || GlucoseAdapter";
    private List<Glucose> measureList;
    public Context mcontext;

    public GlucoseAdapter(Context ctx, List<Glucose> meas){
        mcontext    = ctx;
        measureList = meas;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView timestamp,measure;

        public MyViewHolder(View view) {
            super(view);
            timestamp = view.findViewById(R.id.timestamp);
            measure   = view.findViewById(R.id.measure);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.measure_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glucose glu = measureList.get(position);
        Timestamp stamp = new Timestamp(glu.getTimestamp());
        Date date = new Date(stamp.getTime());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM");

        holder.timestamp.setText(dateFormat.format(date));
        holder.measure.setText(glu.getMeasure()+"");

        // onclick
        holder.measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mcontext, "calibrate", Toast.LENGTH_SHORT).show();
            }
        });
        holder.timestamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mcontext, "calibrate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return measureList.size();
    }


}
