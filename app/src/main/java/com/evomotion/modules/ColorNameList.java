package com.evomotion.modules;

import android.media.MediaCodecInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cievon on 2017/10/23.
 */

public class ColorNameList {
    Map<Integer , String> colorMap;

    public ColorNameList()
    {
        colorMap = new HashMap<Integer,String>();

        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatMonochrome,"COLOR_FormatMonochrome");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format8bitRGB332,"COLOR_Format8bitRGB332");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format12bitRGB444,"COLOR_Format12bitRGB444");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format16bitARGB4444,"COLOR_Format16bitARGB4444");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format16bitARGB1555,"COLOR_Format16bitARGB1555");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format16bitRGB565,"COLOR_Format16bitRGB565");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format16bitBGR565,"COLOR_Format16bitBGR565");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format18bitRGB666,"COLOR_Format18bitRGB666");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format18bitARGB1665,"COLOR_Format18bitARGB1665");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format19bitARGB1666,"COLOR_Format19bitARGB1666");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format24bitRGB888,"COLOR_Format24bitRGB888");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format24bitBGR888,"COLOR_Format24bitBGR888");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format24bitARGB1887,"COLOR_Format24bitARGB1887");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format25bitARGB1888,"COLOR_Format25bitARGB1888");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format32bitBGRA8888,"COLOR_Format32bitBGRA8888");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format32bitARGB8888,"COLOR_Format32bitARGB8888");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411Planar,"COLOR_FormatYUV411Planar");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411PackedPlanar,"COLOR_FormatYUV411PackedPlanar");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,"COLOR_FormatYUV420Planar");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar,"COLOR_FormatYUV420PackedPlanar");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,"COLOR_FormatYUV420SemiPlanar");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422Planar,"COLOR_FormatYUV422Planar");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422PackedPlanar,"COLOR_FormatYUV422PackedPlanar");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422SemiPlanar,"COLOR_FormatYUV422SemiPlanar");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYCbYCr,"COLOR_FormatYCbYCr");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYCrYCb,"COLOR_FormatYCrYCb");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatCbYCrY,"COLOR_FormatCbYCrY");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatCrYCbY,"COLOR_FormatCrYCbY");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV444Interleaved,"COLOR_FormatYUV444Interleaved");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatRawBayer8bit,"COLOR_FormatRawBayer8bit");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatRawBayer10bit,"COLOR_FormatRawBayer10bit");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatRawBayer8bitcompressed,"COLOR_FormatRawBayer8bitcompressed");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatL2,"COLOR_FormatL2");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatL4,"COLOR_FormatL4");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatL8,"COLOR_FormatL8");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatL16,"COLOR_FormatL16");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatL24,"COLOR_FormatL24");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatL32,"COLOR_FormatL32");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar,"COLOR_FormatYUV420PackedSemiPlanar");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422PackedSemiPlanar,"COLOR_FormatYUV422PackedSemiPlanar");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format18BitBGR666,"COLOR_Format18BitBGR666");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format24BitARGB6666,"COLOR_Format24BitARGB6666");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format24BitABGR6666,"COLOR_Format24BitABGR6666");
        put(MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar,"COLOR_TI_FormatYUV420PackedSemiPlanar");

        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface,"COLOR_FormatSurface");
        put(MediaCodecInfo.CodecCapabilities.COLOR_Format32bitABGR8888,"COLOR_Format32bitABGR8888");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible,"COLOR_FormatYUV420Flexible");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422Flexible,"COLOR_FormatYUV422Flexible");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV444Flexible,"COLOR_FormatYUV444Flexible");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatRGBFlexible,"COLOR_FormatRGBFlexible");
        put(MediaCodecInfo.CodecCapabilities.COLOR_FormatRGBAFlexible,"COLOR_FormatRGBAFlexible");
        put(MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar,"COLOR_QCOM_FormatYUV420SemiPlanar");
    }

    public String GetColorName(Integer color)
    {
        return colorMap.get(color);
    }
    private void put(Integer key,String Value){
        colorMap.put(key,Value);
    }
}
