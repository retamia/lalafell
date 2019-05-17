package org.retamia.liveplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.retamia.lalafell.record.LalafellRecordView;

public class MainActivity extends AppCompatActivity {

    //private LalafellVideoView videoView;
    private LalafellRecordView recordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordView = findViewById(R.id.record_view);
        recordView.init();

        /*videoView = findViewById(R.id.video_view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        videoView.setLiveUrl("rtmp://58.200.131.2:1935/livetv/hunantv");
        videoView.prepare();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recordView.release();
    }
}
