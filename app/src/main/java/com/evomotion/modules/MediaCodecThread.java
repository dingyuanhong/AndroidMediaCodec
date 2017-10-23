package com.evomotion.modules;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

/**
 * Created by ZhangHao on 2017/5/5.
 * url: http://www.jianshu.com/p/0695891fa834
 * 读取H264文件送入解码器解码线程
 */

public class MediaCodecThread extends Thread {
    //自定义的log打印，可以无视
    private String TAG = "MediaCodecThread";
    //解码器
    private MediaCodecUtil util;
    //文件路径
    private String path;
    //文件读取完成标识
    private boolean isFinish = false;
    //这个值用于找到第一个帧头后，继续寻找第二个帧头，如果解码失败可以尝试缩小这个值
    private int FRAME_MIN_LEN = 4;
    //一般H264帧大小不超过200k,如果解码失败可以尝试增大这个值
    private static int FRAME_MAX_LEN = 300 * 1024;
    //根据帧率获取的解码每帧需要休眠的时间,根据实际帧率进行操作
    private int PRE_FRAME_TIME = 1000 / 25;

    /**
     * 初始化解码器
     *
     * @param util 解码Util
     * @param path 文件路径
     */
    public MediaCodecThread(MediaCodecUtil util, String path) {
        this.util = util;
        this.path = path;
    }

    /**
     * 寻找指定buffer中h264头的开始位置
     *
     * @param data   数据
     * @param offset 偏移量
     * @param max    需要检测的最大值
     * @return h264头的开始位置 ,-1表示未发现
     */
    private int findHead(byte[] data, int offset, int max) {
        int i;
        for (i = offset; i < max; i++) {
            //发现帧头
            if (isHead(data, i,max))
                break;
        }
        //检测到最大值，未发现帧头
        if (i == max) {
            i = -1;
        }
        return i;
    }

    /**
     * 判断是否是I帧/P帧头:
     * 00 00 00 01 65    (I帧)
     * 00 00 00 01 61 / 41   (P帧)
     *
     * @param data
     * @param offset
     * @return 是否是帧头
     */
    private boolean isHead(byte[] data, int offset,int size) {
        boolean result = false;
        // 00 00 00 01 x
        if ((size - offset) >=4 && data[offset] == 0x00 && data[offset + 1] == 0x00
                && data[offset + 2] == 0x00 && data[3] == 0x01) {
            result = true;
        }
        // 00 00 01 x
        if ((size - offset) >= 3 && data[offset] == 0x00 && data[offset + 1] == 0x00
                && data[offset + 2] == 0x01) {
            result = true;
        }
        return result;
    }

    @Override
    public void run() {
        super.run();
        File file = new File(path);
        //判断文件是否存在
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                //保存完整数据帧
                byte[] frame = new byte[FRAME_MAX_LEN];
                //当前帧长度
                int frameLen = 0;
                //每次从文件读取的数据
                byte[] readData = new byte[10 * 1024];
                //开始时间
                long startTime = System.currentTimeMillis();
                //索引位置
                long pos = 0;
                //循环读取数据
                while (!isFinish) {
                    if (fis.available() > 0) {
                        int readLen = fis.read(readData);
                        //当前长度小于最大值
                        if (frameLen + readLen < FRAME_MAX_LEN) {
                            //将readData拷贝到frame
                            System.arraycopy(readData, 0, frame, frameLen, readLen);
                            //修改frameLen
                            frameLen += readLen;

                            //寻找第一个帧头
                            int headFirstIndex = findHead(frame, 0, frameLen);
                            while (headFirstIndex >= 0 && isHead(frame, headFirstIndex,frameLen)) {
                                //输出NAL头
                                int nalLength = MediaCodecUtil.getNalLength(frame,headFirstIndex,frameLen);

                                //过掉第一个头寻找第二个帧头
                                int headSecondIndex = findHead(frame, headFirstIndex + nalLength, frameLen);

                                //如果第二个帧头存在，则两个帧头之间的就是一帧完整的数据
                                if (headSecondIndex > 0 && isHead(frame, headSecondIndex,frameLen)) {
                                    //显示nal头信息
                                    if(frameLen - headFirstIndex >= nalLength + 1)
                                    {
                                        Log.i(TAG,"NAL header:");
                                        for(int i = 0 ; i <= nalLength;i++)
                                        {
                                            Log.i(TAG,"        " + frame[headFirstIndex + i]);
                                        }
                                    }
                                    //显示数据块长度
                                    Log.i(TAG,"block size:" + ((headSecondIndex - headFirstIndex - 1) - nalLength));
                                    //显示数据块偏移
                                    Log.i(TAG,"block pos:" + (pos + nalLength));
                                    pos += (headSecondIndex - headFirstIndex - 1);
                                    //视频解码
                                    onFrame(frame, headFirstIndex, headSecondIndex);

                                    //截取headSecondIndex之后到frame的有效数据,并放到frame最前面
                                    byte[] temp = Arrays.copyOfRange(frame, headSecondIndex, frameLen);
                                    System.arraycopy(temp, 0, frame, 0, temp.length);
                                    //修改frameLen的值
                                    frameLen = temp.length;

                                    //线程休眠
                                    sleepThread(startTime, System.currentTimeMillis());
                                    //重置开始时间
                                    startTime = System.currentTimeMillis();
                                    //继续寻找数据帧
                                    headFirstIndex = findHead(frame, 0, frameLen);
                                } else {
                                    //找不到第二个帧头
                                    headFirstIndex = -1;
                                }
                            }

                        } else {
                            Log.e(TAG,"drop data.");
                            //如果长度超过最大值，frameLen置0
                            frameLen = 0;
                        }
                    } else {
                        //文件读取结束
                        isFinish = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG,"File not found");
        }
        Log.i(TAG,"process End.");
    }

    //视频解码
    private void onFrame(byte[] frame, int offset, int length) {
        if (util != null) {
            try {
                util.onFrame(frame, offset, length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG,"mediaCodecUtil is NULL");
        }
    }

    //修眠
    private void sleepThread(long startTime, long endTime) {
        //根据读文件和解码耗时，计算需要休眠的时间
        long time = PRE_FRAME_TIME - (endTime - startTime);
        if (time > 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //手动终止读取文件，结束线程
    public void stopThread() {
        isFinish = true;
    }

    public void reset(){ isFinish = false; }
}