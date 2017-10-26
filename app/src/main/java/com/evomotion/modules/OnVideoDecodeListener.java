package com.evomotion.modules;

import android.media.MediaCodec;

import java.nio.ByteBuffer;

/**
 * Created by zhanjunjun on 2017/10/25.
 */

public interface OnVideoDecodeListener {
    void onVideoDecode(ByteBuffer bb, MediaCodec.BufferInfo bi);
}
