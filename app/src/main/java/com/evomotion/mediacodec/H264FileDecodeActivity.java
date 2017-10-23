package com.evomotion.mediacodec;


import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.evomotion.modules.MediaCodecThread;
import com.evomotion.modules.MediaCodecUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class H264FileDecodeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.test_surface_view)
    SurfaceView testSurfaceView;
    private String TAG = "H264FileDecodeActivity";
    private SurfaceHolder holder;
    //解码器
    private MediaCodecUtil codecUtil;
    //读取文件解码线程
    private MediaCodecThread thread;
    //文件路径
//    private String path = Environment.getExternalStorageDirectory().toString() + "/UVCResource/1K.h264";
//    private int width = 1280;
//    private int height = 720;
//    private String path = Environment.getExternalStorageDirectory().toString() + "/UVCResource/2K.h264";
//    private int width = 2048;
//    private int height = 1024;
    private String path = Environment.getExternalStorageDirectory().toString() + "/UVCResource/video.h264";
    private int width = 3040;
    private int height = 1520;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,path);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h264_file_decodec);
        ButterKnife.bind(this);
        initSurface();
    }

    //初始化播放相关
    private void initSurface() {
        holder = testSurfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (codecUtil == null) {
//                    codecUtil = new MediaCodecUtil(holder);
                    codecUtil = new MediaCodecUtil(holder,width,height);
                    codecUtil.startCodec();
                }
                if (thread == null) {
                    //解码线程第一次初始化
                    thread = new MediaCodecThread(codecUtil, path);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (codecUtil != null) {
                    codecUtil.stopCodec();
                    codecUtil = null;
                }
                if (thread != null && thread.isAlive()) {
                    thread.stopThread();
                    thread = null;
                }
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_h264_file:
                if (thread != null) {
                    thread.start();
                }
                break;
        }
    }
}