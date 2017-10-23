package com.evomotion.mediacodec;

/**
 * Created by cievon on 2017/10/21.
 */

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.media.MediaCodecList.REGULAR_CODECS;

public class MP4Activity extends AppCompatActivity implements View.OnClickListener, TextureView.SurfaceTextureListener {
    public static final String TAG = "MainActivity";
    private static final String SAMPLE = Environment.getExternalStorageDirectory() + "/UVCResource/video.mp4";
//    private static final String SAMPLE = Environment.getExternalStorageDirectory() + "/Vid0607000005.mp4";

    Button start_decoder_button;

    TextureView renderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4_main);

        start_decoder_button = (Button) findViewById(R.id.start_decoder_button);
        start_decoder_button.setOnClickListener(this);
        start_decoder_button.setEnabled(false);
        renderView = (TextureView) findViewById(R.id.render_view);
        renderView.setSurfaceTextureListener(this);

        mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(SAMPLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startDecoder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                decoder.start();
            }
        }).start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_decoder_button:
                startDecoder();
                break;
        }
    }

    private MediaCodec decoder;
    private MediaExtractor mediaExtractor;
    private MediaFormat mFormat;

    int[] inputBufferedIds = new int[10000];
    int[] outputBufferIds = new int[10000];
    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

    private MediaCodec.Callback callback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int inputBufferedId) {
            ByteBuffer byteBuffer = codec.getInputBuffer(inputBufferedId);
            int size = mediaExtractor.readSampleData(byteBuffer, 0);
            Log.d(TAG, "byteBuffer.size = "+byteBuffer.remaining());
            codec.queueInputBuffer(inputBufferedId, 0, size, 0, MediaCodec.BUFFER_FLAG_CODEC_CONFIG);

        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int outputBufferId, @NonNull MediaCodec.BufferInfo info) {
//            ByteBuffer byteBuffer

            ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferId);
            Log.d(TAG, "解码出来啦"+info.toString()+"size = "+outputBuffer.remaining());
            MediaFormat bufferFormat = codec.getOutputFormat(outputBufferId);
            codec.flush();
            codec.dequeueOutputBuffer(info, outputBuffer.remaining());
//            codec.releaseOutputBuffer(outputBufferId,true);
            codec.flush();
            mediaExtractor.advance();
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
//            mFormat = format;
//            codec.configure(format,new Surface(renderView.getSurfaceTexture()),null,0);
        }
    };

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {

        for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
            mFormat = mediaExtractor.getTrackFormat(i);
            String mime = mFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                mediaExtractor.selectTrack(i);
                try {
                    decoder = MediaCodec.createDecoderByType(mime);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                decoder.configure(mFormat, new Surface(surfaceTexture), null, 0);
//                decoder.configure(format, null, null, 0);
                decoder.setCallback(callback);
                break;
            }
        }
        start_decoder_button.setEnabled(true);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
