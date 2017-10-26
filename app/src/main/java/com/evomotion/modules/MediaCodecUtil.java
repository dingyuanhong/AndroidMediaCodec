package com.evomotion.modules;


import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by ZhangHao on 2016/8/5.
 * url: http://www.jianshu.com/p/0695891fa834
 * 用于硬件解码(MediaCodec)H264的工具类
 */
public class MediaCodecUtil{

    private String TAG = "MediaCodecUtil";
    //解码后显示的surface及其宽高
    private SurfaceHolder holder;
    private int width, height;
    //解码器
    private MediaCodec mCodec;
    private MediaFormat mMediaFormat;
    private ByteBuffer mSPS;
    private ByteBuffer mPPS;
    private boolean isInitedCodec = false;
    private boolean isShowSurface = false;
    // 需要解码的类型
    private final static String MIME_TYPE = "video/avc"; // H.264 Advanced Video
    private final static int TIME_INTERNAL = 5;

    /**
     * 初始化解码器
     *
     * @param holder 用于显示视频的surface
     * @param width  surface宽
     * @param height surface高
     */
    public MediaCodecUtil(SurfaceHolder holder, int width, int height) {
//        logger.d("MediaCodecUtil() called with: " + "holder = [" + holder + "], " +
//                "width = [" + width + "], height = [" + height + "]");
        this.holder = holder;
        this.width = width;
        this.height = height;
    }

    public MediaCodecUtil(SurfaceHolder holder) {
        this(holder, holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
    }

    public void setSPS(byte[] sps,int offset,int length)
    {
        try {
            byte[] temp = Arrays.copyOfRange(sps, offset, length);
            mSPS = ByteBuffer.wrap(temp);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setPPS(byte[] pps,int offset,int length)
    {
        try {
            byte[] temp = Arrays.copyOfRange(pps, offset, length);
            mPPS = ByteBuffer.wrap(temp);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void startCodec() {
        Log.i(TAG,"function startCodec");
        if (!isInitedCodec) {
            //第一次打开则初始化解码器
            initDecoder();
        }
    }

    public MediaCodecInfo getDeCodecInfo(String codecName)
    {
        Log.i(TAG,"function getDeCodecInfo");
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo codecInfo = null;
        for (int i = 0; i < numCodecs && codecInfo == null; i++) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            if (info.isEncoder()) {
                continue;
            }
            String[] types = info.getSupportedTypes();
            boolean found = false;
            for (int j = 0; j < types.length && !found; j++) {
                if (types[j].equals(codecName)) {
                    found = true;
                }
            }
            if (!found)
                continue;
            codecInfo = info;
        }
        return codecInfo;
    }

    //检查编解码器支持类型及格式
    @SuppressLint("NewApi")
    private int checkSupportColorFormat() {
        Log.i(TAG,"function checkSupportColorFormat");
        String mimeType = "video/avc";
        MediaCodecInfo codecInfo = getDeCodecInfo(mimeType);

        Log.e(TAG, "Found " + codecInfo.getName() + " supporting " + mimeType);

        // Find a color profile that the codec supports
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        Log.e(TAG,"supported length-" + capabilities.colorFormats.length + " == " + Arrays.toString(capabilities.colorFormats));
        ColorNameList nameList = new ColorNameList();
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            switch (capabilities.colorFormats[i]) {
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:

                    Log.i(TAG, "supported color format::" + capabilities.colorFormats[i] + " " + nameList.GetColorName(capabilities.colorFormats[i]));
                    break;
                default:
                    Log.e(TAG, "unknown supported color format " + capabilities.colorFormats[i] + " " + nameList.GetColorName(capabilities.colorFormats[i]));
                    break;
            }
        }

        return -1;
    }


    private void initDecoder() {
        Log.i(TAG,"function initDecoder");
        //初始化MediaFormat
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE,
                width, height);
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);

        //检查格式是否支持,此方法不可信(乐视，努比亚，华为部分机型);
        Boolean CheckSupport = Boolean.TRUE;
        Boolean CheckResolution = Boolean.TRUE;
        //忽略API 21以下设备机型
        Boolean CheckNoMethond = Boolean.TRUE;
        try {
            checkSupportColorFormat();

            Log.i(TAG, "check for decode info.");
            MediaCodecInfo.CodecCapabilities capabilities = getDeCodecInfo(MIME_TYPE).getCapabilitiesForType("video/avc");

            Boolean bSupport = Boolean.FALSE;
            Boolean bResolution = Boolean.FALSE;
            if (CheckSupport) {
                Log.i(TAG, "check for FormatSupported.");
                bSupport = capabilities.isFormatSupported(mediaFormat);

            }
            if (CheckResolution) {
                Log.i(TAG, "check for Solution.");
                MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
                bResolution = videoCapabilities.isSizeSupported(width, height);
            }
            if(bSupport == Boolean.FALSE && bResolution == Boolean.FALSE)
            {
                Log.e(TAG, "MediaFormat Support and Resolution not support:" + bSupport + " " + bResolution);
                mediaFormat = null;
                return;
            }
            else  if (bSupport == Boolean.FALSE) {
                Log.e(TAG, "MediaFormat Support not support:" + bSupport);
                mediaFormat = null;
                return;
            }
            else if (bResolution == Boolean.FALSE) {
                Log.e(TAG, "MediaFormat Resolution not support:" + bResolution);
                mediaFormat = null;
                return;
            }
        }
        catch(NoSuchMethodError er)
        {
            Log.e(TAG,er.getMessage());
            er.printStackTrace();
            if(CheckNoMethond)
            {
                mMediaFormat = null;
                return;
            }
        }
        catch(Exception ex)
        {
            Log.e(TAG,ex.getMessage());
            ex.printStackTrace();
            mMediaFormat = null;
            return;
        }

        if(mSPS != null && mPPS != null)
        {
            ByteBuffer sps = mSPS;
            ByteBuffer pps = mPPS;
            //http://www.jianshu.com/p/61ccf8e2c245
            mediaFormat.setByteBuffer("csd-0",sps);
            mediaFormat.setByteBuffer("csd-1",pps);
        }
        else {
            mMediaFormat = null;
            Log.e(TAG," no has SPS/PPS");
            return ;
        }
        try {
            Log.i(TAG,"Codec create.");
            if(mCodec == null) {
                //根据需要解码的类型创建解码器
                mCodec = MediaCodec.createDecoderByType(MIME_TYPE);

            }
            Log.i(TAG,"Codec configure.");
            //配置MediaFormat以及需要显示的surface
            if(isShowSurface) {
                mCodec.configure(mediaFormat, holder.getSurface(), null, 0);
            }else{
                mCodec.configure(mediaFormat, null, null, 0);
            }
        }catch(Exception ex)
        {
            Log.e(TAG,"Codec configure Error.");
            ex.printStackTrace();
            mMediaFormat = null;
            mCodec = null;
            return ;
        }

        Log.i(TAG, "check for start.");
        try {
            //开始解码
            mCodec.start();
        }catch(Exception ex)
        {
            Log.e(TAG,"Codec start Error.");
            ex.printStackTrace();
            if(mCodec != null)
            {
                mCodec.release();
            }
            mCodec = null;
            mMediaFormat = null;
            return ;
        }
        mMediaFormat = mediaFormat;

        MediaCodecInfo codecInfo = mCodec.getCodecInfo();
        String name = mCodec.getName();
        Log.i(TAG,codecInfo.toString());
        Log.i(TAG,name);
        Log.i(TAG,"initDecoder success.");
        isInitedCodec = true;
    }

    //获取NAL标志头长度
    public static int getNalLength(byte[] buf, int offset, int length)
    {
        if(length - offset < 3)
        {
            return 0;
        }

        if(buf[offset] == 0x00)
        {
            if(buf[offset + 1] == 0x00)
            {
                if(buf[offset + 2] == 0x01)
                {
                    return 3;
                }else if(buf[offset + 2] == 0x00)
                {
                    return 4;
                }
            }
        }
        return 0;
    }
    private byte getNalType(byte[] buf, int offset, int length)
    {
        //获取NAL标识长度
        int nalLength = getNalLength(buf,offset,length);
        if(nalLength == 0)
        {
            return 0;
        }
        //获取NAL类型
        byte nal = buf[offset + nalLength];
        byte nalType = (byte)(nal & 0x1F);
        return nal;
    }

    private Boolean checkDataFrame(byte[] buf, int offset, int length)
    {
        //获取NAL标识长度
        int nalLength = getNalLength(buf,offset,length);
        if(nalLength == 0)
        {
            Log.e(TAG,"frame nal error." + buf[0] + buf[1] + buf[2] + buf[3]);
            return false;
        }
        //获取NAL类型
        byte nal = buf[offset + nalLength];
        byte nalType = (byte)(nal & 0x1F);
        Log.i(TAG,"nal type:" + nalType);
        if(nalType == 0x7) //sps
        {
            if(!isInitedCodec)
            {
                setSPS(buf,offset,length);
                initDecoder();
            }
            return false;
        }else if(nalType == 8) //pps
        {
            if(!isInitedCodec)
            {
                setPPS(buf,offset,length);
                initDecoder();
            }
            return false;
        }else if(nalType != 5 && nalType != 1) //关键帧和P帧
        {
            Log.e(TAG,"unsupped nal type:" + nalType);
            return false;
        }
        if(!isInitedCodec)
        {
            Log.e(TAG,"codec not inited.");
            return false;
        }
        return true;
    }

    int mCount = 0;

    //解码帧
    public boolean onFrame(byte[] buf, int offset, int length) {
        //检查数据帧
        if(checkDataFrame(buf,offset,length) == false)
        {
            return false;
        }

        //获取NAL类型
        byte nalType = getNalType(buf,offset,length);

        // 获取输入buffer index
        ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
        //-1表示一直等待；0表示不等待；其他大于0的参数表示等待毫秒数
        int inputBufferIndex = mCodec.dequeueInputBuffer(100);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            //清空buffer
            inputBuffer.clear();
            //put需要解码的数据
            inputBuffer.put(buf, offset, length);
            //解码
            if(nalType == 5)
            {
                mCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount * TIME_INTERNAL, MediaCodec.BUFFER_FLAG_KEY_FRAME);
            }else {
                mCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount * TIME_INTERNAL, 0);
            }

            mCount++;
        } else {
            Log.e(TAG,"dequeueInputBuffer failed.");
            return false;
        }
        // 获取输出buffer index
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
        if (outputBufferIndex < 0) {
            Log.e(TAG,"outputBufferIndex = " + outputBufferIndex);
        }
        ByteBuffer[] outBuffers = mCodec.getOutputBuffers();
        //循环解码，直到数据全部解码完成
        while (outputBufferIndex >= 0) {
            if(!isShowSurface) {
                if (outputBufferIndex >= 0) {
                    ByteBuffer bb = outBuffers[outputBufferIndex];
                    if (mListener != null) {
                        mListener.onVideoDecode(bb, bufferInfo);
                    }
                }
            }

            //logger.d("outputBufferIndex = " + outputBufferIndex);
            //true : 将解码的数据显示到surface上
            if(isShowSurface){
                mCodec.releaseOutputBuffer(outputBufferIndex, true);
            }else{
                mCodec.releaseOutputBuffer(outputBufferIndex, false);
            }


            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                Log.e(TAG, "BUFFER_FLAG_END_OF_STREAM");
            }

            outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
        }

        return true;
    }

    private OnVideoDecodeListener mListener;

    public void setVideoListener(OnVideoDecodeListener mOnVideoListener) {
        this.mListener = mOnVideoListener;
    }

    /**
     *停止解码，释放解码器
     */
    public void stopCodec() {
        Log.i(TAG,"function stopCodec");
        try {
            if(mCodec != null)
            {
                mCodec.stop();
                mCodec.release();
            }
            mCodec = null;
            mMediaFormat = null;
            isInitedCodec = false;
        } catch (Exception e) {
            e.printStackTrace();
            mCodec = null;
            mMediaFormat = null;
        }
    }
}