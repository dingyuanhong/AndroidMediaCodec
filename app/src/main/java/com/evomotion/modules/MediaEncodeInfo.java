package com.evomotion.modules;

import android.annotation.SuppressLint;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by zhanjunjun on 2017/10/26.
 */

public class MediaEncodeInfo {


    private String TAG = MediaEncodeInfo.class.getName();

    public void printAllCodec()
    {String         mimeType  = "video/avc";
        Log.i(TAG,"function getDeCodecInfo");
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo codecInfo = null;
        for (int i = 0; i < numCodecs && codecInfo == null; i++) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            if (!info.isEncoder()) {
                continue;
            }
            String[] types = info.getSupportedTypes();
            boolean found = false;
            for (int j = 0; j < types.length && !found; j++) {
                if (types[j].equals(mimeType)) {
                    found = true;
                }
            }
            if (!found)
                continue;
            codecInfo = info;
            checkSupportColorFormat(codecInfo);
        }
    }

    //检查编解码器支持类型及格式
    @SuppressLint("NewApi")
    private int checkSupportColorFormat(MediaCodecInfo codecInfo) {
        Log.i(TAG, "function checkSupportColorFormat");
        String         mimeType  = "video/avc";
        Log.e(TAG, "Found " + codecInfo.getName() + " supporting " + mimeType);

        // Find a color profile that the codec supports
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        Log.e(TAG, "supported length-" + capabilities.colorFormats.length + " == " + Arrays.toString(capabilities.colorFormats));
        ColorNameList nameList = new ColorNameList();
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            switch (capabilities.colorFormats[i]) {
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:

                    Log.i(TAG, "encode supported color format::" + capabilities.colorFormats[i] + " " + nameList.GetColorName(capabilities.colorFormats[i]));
                    break;
                default:
                    Log.e(TAG, "unknown supported color format " + capabilities.colorFormats[i] + " " + nameList.GetColorName(capabilities.colorFormats[i]));
                    break;
            }
        }

        return -1;
    }

}