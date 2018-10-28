package com.venoty.laura.models;

public class Glucose {
    private final String TAG = "Laura || Glucose";

    private final    int       BITMASK = 0x3FFF;
    private final double W_RAW_GLUCOSE = 0.11;//0.11761;
    private final double W_RAW_TEMPERA = 0.0121;
    private final double     INTERCEPT = -91.8109;

    private int rawMeasure;
    private int rawTemperature;

    private long timestamp;
    private int measure;

    // Getters & Setters

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getMeasure() { return measure; }
    public void setMeasure(int measure) { this.measure = measure; }

    public int getRawMeasure() {
        return rawMeasure;
    }

    public int getRawTemperature() {
        return rawTemperature;
    }

    // Constructors

    public Glucose(){}

    public Glucose(int measure){
        this.timestamp = System.currentTimeMillis();
        this.measure   = measure;
    }
    public Glucose(String raw){
        processRaw(raw);
    }

    public Glucose(String raw, long ts){
        processRaw(raw);
        setTimestamp(ts);
    }

    public int getCalculateGlucose(){
        return (int) ((W_RAW_GLUCOSE * rawMeasure) + (W_RAW_TEMPERA * rawTemperature) + INTERCEPT);

    }

    private void processRaw(String raw){
        String mea  = raw.substring(2,4)  + raw.substring(0,2);
        String temp = raw.substring(8,10) + raw.substring(6,8);

        rawMeasure     = Integer.parseInt(mea,16);
        rawTemperature = Integer.parseInt(temp,16) & BITMASK;
    }

    @Override
    public String toString() {
        return timestamp + "," + measure;
    }


}
