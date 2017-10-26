package com.evomotion.mediacodec;


import android.media.MediaCodec;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.evomotion.modules.FrameWriter;
import com.evomotion.modules.MediaCodecThread;
import com.evomotion.modules.MediaCodecUtil;
import com.evomotion.modules.MediaEncodeInfo;
import com.evomotion.modules.OnVideoDecodeListener;
import com.evomotion.modules.OnVideoEncodeListener;
import com.evomotion.modules.VideoConfiguration;
import com.evomotion.modules.YUVInputVideoController;

import java.nio.ByteBuffer;

public class H264FileDecodeActivity extends AppCompatActivity implements View.OnClickListener,
                                                                         OnVideoEncodeListener,
                                                                         OnVideoDecodeListener {

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
    private SurfaceView testSurfaceView;
    private YUVInputVideoController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,path);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h264_file_decodec);
        testSurfaceView = (SurfaceView) findViewById(R.id.test_surface_view);
        Button getInfo = (Button) findViewById(R.id.get_info);
        getInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaEncodeInfo info = new MediaEncodeInfo();
                info.printAllCodec();
            }
        });
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
                    codecUtil.setVideoListener(H264FileDecodeActivity.this);
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
                mController = new YUVInputVideoController();
                mController.setVideoEncoderListener(this);
                VideoConfiguration configuration = new VideoConfiguration.Builder().setSize(width,height).build();
                mController.setVideoConfiguration(configuration);
                mController.resume();
                mController.start();
                if (thread != null) {
                    thread.start();
                }
                break;
        }
    }

    FrameWriter mFrameWriter = new FrameWriter();

    @Override
    public void onVideoEncode(ByteBuffer bb, MediaCodec.BufferInfo bi) {
        Log.d("xxx","xiao fei offset:"+bi.offset+",size:"+bi.size);
        bb.position(bi.offset);
        bb.limit(bi.offset + bi.size);
        mFrameWriter.writeFrameToStorage(bb,null);
    }

    @Override
    public void onVideoDecode(ByteBuffer bb, MediaCodec.BufferInfo bi) {
        if(mController != null) {
            bb.position(bi.offset);
            bb.limit(bi.offset + bi.size);
            Log.e("xxx","offset:"+bi.offset+",size:"+bi.size);
            mController.queueBufferInfo(bb,1080,960);
        }
    }
}