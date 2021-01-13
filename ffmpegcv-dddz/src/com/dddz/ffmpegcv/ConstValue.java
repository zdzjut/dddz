package com.dddz.ffmpegcv;

import com.dddz.ffmpegcv.thread.CameraRecorder;

import java.util.HashMap;
import java.util.Map;

public interface ConstValue {
    public static int limit = 1024;
    public static Map<Integer, CameraRecorder> map = new HashMap<>();
}
