package org.retamia.lalafell.record.scene;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.retamia.lalafell.record.media.Frame;
import org.retamia.lalafell.record.output.Output;
import org.retamia.lalafell.record.source.Source;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class Scene {

    private static final String TAG = Scene.class.getName();

    private LinkedList<Source> sources;
    private LinkedList<Output> outputs;

    private HandlerThread sceneHandlerThread;
    private SceneHandler sceneHandler;

    private SceneListener listener = null;

    private Context context;

    public Scene(Context context) {
        sources = new LinkedList<>();
        outputs = new LinkedList<>();

        this.context = context;
    }

    public void init()
    {
        sceneHandlerThread = new HandlerThread("Scene Handler Thread");
        sceneHandlerThread.start();
        sceneHandler = new SceneHandler(sceneHandlerThread.getLooper(), this);

        for (Source source: sources) {
            source.init(sceneHandler);
        }

        for (Output output: outputs) {
            output.init(sceneHandler);
        }
    }

    public void load()
    {
        for (Output output: outputs) {
            output.open();
        }

        for (Source source: sources) {
            source.open();
        }
    }

    public void remove()
    {
        for (Output output: outputs) {
            output.close();
        }

        for (Source source: sources) {
            source.close();
        }
    }


    public void release()
    {
        for (Output output: outputs) {
            output.release();
        }

        for (Source source: sources) {
            source.release();
        }

        sceneHandlerThread.quitSafely();
    }

    public void addSource(Source source)
    {
        source.setListener(sourceListener);
        this.sources.add(source);
    }

    public void setListener(SceneListener listener) {
        this.listener = listener;
    }

    public void addOutput(Output output)
    {
        output.setListener(outputListener);
        this.outputs.add(output);
    }

    private final Source.SourceListener sourceListener = new Source.SourceListener() {
        @Override
        public void onImageDataAvailable(Source source, Frame image) {
            Message msg = Message.obtain();

            msg.what = Output.FRAME_PRODUCED;
            msg.obj = image;
            Scene.this.sceneHandler.sendMessage(msg);
        }

        @Override
        public void onOpenError(Source source, String reason) {

        }
    };

    private final Output.OutputListener outputListener = new Output.OutputListener() {
        @Override
        public void onOpenError(Output output, String reason) {

        }
    };

    private final static class SceneHandler extends Handler {

        private WeakReference<Scene> sceneWeakReference;

        SceneHandler(Looper looper, Scene scene) {
            super(looper);
            sceneWeakReference = new WeakReference<>(scene);
        }

        @Override
        public void handleMessage(Message msg) {
            Scene scene = sceneWeakReference.get();

            if (scene == null) {
                return;
            }

            if (msg.what == Output.FRAME_PRODUCED) {
                for(Output output: scene.outputs) {
                    output.handleFrame((Frame)msg.obj);
                }
            }
        }
    }
}
