package com.evomotion.modules;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by jephy on 7/22/17.
 */

public class FrameWriter {

    private final static String TAG = "FrameWriter";

    private final File defaultDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/UVCResource/temp_frame_0815_1026");

    private File dir;
    private int fileNum = 0;//文件命名计数

    /**
     * @param dir 帧数据存放路径
     */

    public FrameWriter(File dir) {
        this.dir = dir;
    }

    /**
     * 默认初始化帧数据存放路径
     */
    public FrameWriter() {
        this.dir = defaultDir;
    }

    /**
     * 将ByteBuffer数据写入到存储卡的特定目录
     * @param byteBuffer 需要写入的数据
     * @param destDir 写入的目录
     * @return
     */
    public boolean writeFrameToStorage(ByteBuffer byteBuffer,File destDir){

        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);

        Log.d(TAG, "writeFile....数组长度。。。" + bytes.length);
        if (null == destDir){
            destDir = defaultDir;
        }

        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File file = new File(destDir, "/" + fileNum + "_frame.txt");
        FileOutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream=null;
        try {
            outputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(outputStream);
//            byte[] bytes = new byte[frame.remaining()];
//            frame.get(bytes);
//            outputStream.write(bytes);
            bufferedOutputStream.write(bytes);
            bufferedOutputStream.flush();
            fileNum++;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public boolean writeFrameToStorage(byte[] yData,byte[] uData,byte[] vData,File destDir){
        if (null == destDir){
            destDir = defaultDir;
        }

        if (!destDir.exists()) {
            destDir.mkdir();
        }

        File file = new File(destDir, "/" + fileNum + "_frame.txt");
        FileOutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream=null;
        try {
            outputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(outputStream);

            bufferedOutputStream.write(yData);
            bufferedOutputStream.write(uData);
            bufferedOutputStream.write(vData);

            bufferedOutputStream.flush();
            fileNum++;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将data数据写入到存储卡的特定目录
     * @param data 需要写入的数据
     * @param destDir 写入的目录
     * @return
     */
    public boolean writeFrameToStorage(byte[] data,File destDir){

        Log.d(TAG, "writeFile....数组长度。。。" + data.length);
        if (null == destDir){
            destDir = defaultDir;
        }

        if (!destDir.exists()) {
            destDir.mkdir();
        }

        File file = new File(destDir, "/" + fileNum + "_frame.txt");
        FileOutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream=null;
        try {
            outputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(data);
            bufferedOutputStream.flush();
            fileNum++;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
