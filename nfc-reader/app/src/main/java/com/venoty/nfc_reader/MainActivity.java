package com.venoty.nfc_reader;

import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.bouncycastle.util.encoders.Hex;
import java.io.IOException;
import java.util.Arrays;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;


public class MainActivity extends AppCompatActivity {

    private final String TAG              = "NFC-Reader";
    private final String URL_SERVER       = "http://venoty.tk:5000";
    private final String TECH_DISC_ACTION = "android.nfc.action.TECH_DISCOVERED";
    private final String NAME_TAG         = "android.nfc.extra.TAG";
    private final int    NUM_MAX_BLOCKS   = 40;

    private Intent myIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            if (TECH_DISC_ACTION.equals(getIntent().getAction())) {
                myIntent = getIntent();
                Thread t = new NFCsensor();
                t.start();
                t.join();
            }
        } catch (Exception e) {
            Log.d(this.TAG, Log.getStackTraceString(e));
        }
        finish();
    }

    public class NFCsensor extends Thread {

        byte[] cmd;
        byte[] block;

        public void run() {

            try {

                NfcV tag = NfcV.get((Tag) myIntent.getParcelableExtra(NAME_TAG));
                StringBuilder stringBuilder = new StringBuilder();

                tag.connect();

                for (int i = 0; i <= NUM_MAX_BLOCKS; i++) {
                    // 0x23 Read Multiples block
                    // little endian --> 0x32
                    cmd   = new byte[]{(byte) 0, (byte) 32, (byte) i};
                    block = tag.transceive(cmd);
                    block = Arrays.copyOfRange(block, 1, 8);

                    stringBuilder.append(i * 8)
                            .append(" - ")
                            .append(Hex.toHexString(block)
                                   .replaceAll("(.{2})", "$1 "))
                            .append("\n");
                }

                Log.d(TAG, stringBuilder.toString());
                sendData(stringBuilder.toString());

            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        private void sendData(String data) throws IOException {
            MediaType mediaType = MediaType.parse("text/plain");
            new OkHttpClient().newCall( new Builder().url(URL_SERVER)
                                        .post(RequestBody.create(mediaType, data))
                                        .build())
                                        .execute();
        }
    }
}