package org.retamia.liveplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("liveplayer");
    }

    private long playerPointer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        playerPointer = allocLivePlayer();

        preparePlayer(playerPointer, "rtmp://58.200.131.2:1935/livetv/hunantv");
    }

    @Override
    protected void onDestroy() {
        releaseLivePlayer(playerPointer);
        super.onDestroy();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    private native long allocLivePlayer();

    public native void releaseLivePlayer(long pointer);

    public native void preparePlayer(long pointer, String url);
}
