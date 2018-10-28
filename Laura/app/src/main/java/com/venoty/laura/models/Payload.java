package com.venoty.laura.models;

import android.util.Log;

public class Payload {

    private final String TAG = "Laura || Payload";

    private String payload;

    private Glucose[] historyMeasures;
    private Glucose[] currentMeasures;

    // Getters & Setters
    public Glucose[] getHistoryMeasures() {return historyMeasures;}
    public Glucose[] getCurrentMeasures() {return currentMeasures;}

    public Payload(byte[] payloadBytes){
        payload = bytesToHex(payloadBytes);
        //Log.d(TAG,payload);
        extractCurrentMeasures(payload);
        extractHistoryMeasures(payload);

    }

    private String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[(bytes.length * 2)];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[(j * 2) + 1] = hexArray[v & 15];
        }
        return new String(hexChars);
    }


    private void extractCurrentMeasures(String payload){

        // nextPointer is the fartest measure (15 mins)
        int nextPointer  = Integer.parseInt(payload.substring(0,2),16);

        int currentPointer = nextPointer - 1;
        if (currentPointer < 0) currentPointer = 15 + currentPointer;

        int sevenAgoPointer  = currentPointer - 7;
        if (sevenAgoPointer < 0) sevenAgoPointer = 15 + sevenAgoPointer;

        int cuPosit     = (currentPointer  * 12) + 4;
        int sevenPos    = (sevenAgoPointer * 12) + 4;
        int fivetPos    = (nextPointer     * 12) + 4;

        String current  = payload.substring(cuPosit,cuPosit + 12);
        String sevenAgo = payload.substring(sevenPos,sevenPos + 12);
        String fivetAgo = payload.substring(fivetPos,fivetPos   + 12);


        currentMeasures = new Glucose[]{new Glucose(current,System.currentTimeMillis()),
                                        new Glucose(sevenAgo),
                                        new Glucose(fivetAgo)};
    }

    private void extractHistoryMeasures(String payload){

        historyMeasures = new Glucose[32];

        // split history block
        int p           = Integer.parseInt(payload.substring(2,4),16);
        payload         = payload.substring(196,580);

        String aux;

        for(int i=0;i<32;i++) {
            if (p == 32) p = 0; // reset
            aux = payload.substring(p * 12, (p + 1) * 12);
            historyMeasures[i] = new Glucose(aux, System.currentTimeMillis() - (900000 * (31 - i)));
            p++;
        }
    }

}
