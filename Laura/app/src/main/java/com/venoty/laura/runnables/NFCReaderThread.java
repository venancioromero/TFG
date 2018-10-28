package com.venoty.laura.runnables;

import android.nfc.tech.NfcV;
import android.util.Log;

import com.google.common.primitives.Bytes;
import com.venoty.laura.models.Glucose;
import com.venoty.laura.models.Payload;

import java.util.Arrays;

public class NFCReaderThread extends Thread {

    private final String TAG = "Laura || NFCReaderThread";

    private NfcV nfcvTag;
    private Glucose[] currentGlucoseMeasures;
    private Glucose[] historyGlucoseMeasures;

    private byte[] cmd;
    private byte[] block;
    private byte[] payloadBytes = new byte[0];

    // Setters and getters
    public Glucose[] getCurrentGlucoseMeasures(){return currentGlucoseMeasures;}
    public Glucose[] getHistoryGlucoseMeasures(){return historyGlucoseMeasures;}

    public NFCReaderThread(NfcV tag){
        nfcvTag = tag;
    }

    public void run() {
        try {
            nfcvTag.connect();
            cmd = new byte[]{(byte) 0, (byte) 32, (byte) 0};

            for (int i = 3 ; i <= 40; i++) {
                cmd[2] = (byte) i;
                block  = nfcvTag.transceive(cmd);

                // first position always is 00... drop
                block   = Arrays.copyOfRange(block, 1, block.length);
                payloadBytes = Bytes.concat(payloadBytes,block);
            }
            payloadBytes           = Arrays.copyOfRange(payloadBytes, 2 , payloadBytes.length);
            Payload payload        = new Payload(payloadBytes);

            currentGlucoseMeasures = payload.getCurrentMeasures();
            historyGlucoseMeasures = payload.getHistoryMeasures();

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            // TODO launch toast

        }
    }

}
