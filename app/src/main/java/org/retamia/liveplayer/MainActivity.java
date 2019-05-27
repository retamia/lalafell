package org.retamia.liveplayer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.retamia.lalafell.record.LalafellRecordActivity;

public class MainActivity extends AppCompatActivity {

    //private LalafellVideoView videoView;
    //private LalafellRecordActivity recordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LalafellRecordActivity.Start(this);
        /*videoView = findViewById(R.id.video_view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        videoView.setLiveUrl("rtmp://58.200.131.2:1935/livetv/hunantv");
        videoView.prepare();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
