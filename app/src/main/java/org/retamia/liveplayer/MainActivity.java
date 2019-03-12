package org.retamia.liveplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

import org.retamia.lalafell.LalafellVideoView;

public class MainActivity extends AppCompatActivity {

    private LalafellVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.video_view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        videoView.setLiveUrl("rtmp://58.200.131.2:1935/livetv/", "hunantv");
        videoView.open();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.release();
    }
}
