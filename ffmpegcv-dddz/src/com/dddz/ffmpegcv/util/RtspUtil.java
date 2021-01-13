package com.dddz.ffmpegcv.util;

import com.dddz.ffmpegcv.ConstValue;
import com.dddz.ffmpegcv.thread.CameraRecorder;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;

import java.util.List;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_ref;
import static org.bytedeco.ffmpeg.global.avutil.av_freep;

public class RtspUtil {
    int errorTimes = 0;

    public void rtspToAvPacket(CameraRecorder cameraRecorder) {
        if (errorTimes > 5) {
//            log.error(lane+"车道录制失效");
            return;
        }
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(cameraRecorder.getInputRtsp());
        try {
            grabber.setOption("rtsp_transport", "tcp");
            grabber.setFrameRate(25);
            grabber.setVideoBitrate(2000000);
            grabber.start();
            cameraRecorder.setFormatContext(grabber.getFormatContext());
            List<AVPacket> avPackets = cameraRecorder.getAvPackets();
            boolean overflow = false;
            while (true) {
                AVPacket pkt = grabber.grabPacket();
                AVPacket pkt2 = new AVPacket();
                av_packet_ref(pkt2, pkt);
                int current = cameraRecorder.getCurrent();
                if (overflow) {
                    AVPacket avPacket = avPackets.get(current);
                    avcodec.av_packet_unref(avPacket);
                    av_freep(avPacket);//gc
                }
                avPackets.set(current, pkt2);
                if (++current == ConstValue.limit) {
                    overflow = true;
                    current = 0;
                }
                cameraRecorder.setCurrent(current);
            }
        } catch (FrameGrabber.Exception e) {
            try {
                grabber.stop();
            } catch (FrameGrabber.Exception exception) {
                exception.printStackTrace();
            }
            rtspToAvPacket(cameraRecorder);
            errorTimes++;
        }
    }


}