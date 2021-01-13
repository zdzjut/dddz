package com.dddz.ffmpegcv.util;

import com.dddz.ffmpegcv.ConstValue;
import com.dddz.ffmpegcv.thread.CameraRecorder;
import com.dddz.ffmpegcv.thread.VideoThread;
import org.bytedeco.ffmpeg.global.avutil;


public class CacheTest {

    public static void main(String[] args) throws InterruptedException {
        String rtspIn = "rtsp://admin:admin123@192.168.10.248:554/cam/realmonitor?channel=1&subtype=1";
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        RtspUtil rtspUtil = new RtspUtil();
        int lane = 1;
        CameraRecorder cameraRecorder = new CameraRecorder(lane, 2048, rtspIn);
        Thread thread = new Thread(() -> {
            ConstValue.map.put(lane, cameraRecorder);
            rtspUtil.rtspToAvPacket(cameraRecorder);
        });
        thread.setName("车道" + 1);
        thread.start();
        Thread.sleep(10000);
        for (int i = 0; i < 1; i++) {
            new VideoThread("d:/zzzz/" + i + ".mp4", cameraRecorder).start();
            Thread.sleep(5000);
        }
    }


}
