package com.dddz.ffmpegcv.thread;

import com.dddz.ffmpegcv.ConstValue;
import com.dddz.ffmpegcv.util.FFmpegFrameRecorderPlus;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.FrameRecorder;

import java.util.List;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264;
import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MPEG4;


public class VideoThread extends Thread {

    private CameraRecorder cameraRecorder;
    private String outputFile;


    public VideoThread(String outputFile,CameraRecorder cameraRecorder) {
        this.outputFile = outputFile;
        this.cameraRecorder = cameraRecorder;

    }

    @Override
    public void run() {
        FFmpegFrameRecorderPlus recorder = new FFmpegFrameRecorderPlus(outputFile, 1080, 640, 0);
        recorder.setFrameRate(25);
        recorder.setVideoBitrate(2000000);
        recorder.setPixelFormat(AV_CODEC_ID_H264);
        recorder.setVideoCodec(AV_CODEC_ID_MPEG4);
        try {
            recorder.start(cameraRecorder.getFormatContext());
            int current = cameraRecorder.getCurrent();
            Thread.sleep(10000);
            recordFrame(recorder, current);
            recorder.stop();

        } catch (FrameRecorder.Exception e) {
            try {
                recorder.stop();
            } catch (FrameRecorder.Exception exception) {
                exception.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param startIndex low endpoint (inclusive) of the subList
     */
    void recordFrame(FFmpegFrameRecorderPlus recorder, int startIndex) throws FrameRecorder.Exception {
        int offset = 75; //当前时间向前75帧
        int videoLength = 150; //总共150帧
        if (startIndex < offset) {
            startIndex = ConstValue.limit + startIndex - offset;
        } else {
            startIndex = startIndex - offset;
        }
        int sum = startIndex + videoLength;
        List<AVPacket> avPacketList;
        if (sum > ConstValue.limit) { //超过最大值
            avPacketList = cameraRecorder.getAvPackets().subList(startIndex, ConstValue.limit);
            List<AVPacket> list2 = cameraRecorder.getAvPackets().subList(0, sum - ConstValue.limit);
            avPacketList.addAll(list2);
        } else {
            avPacketList = cameraRecorder.getAvPackets().subList(startIndex, sum);
        }
        System.out.println(startIndex + "--------" + sum);

        int DtsPts = 0;// 目前来看两个参数一致
        for (AVPacket avPacket : avPacketList) {
            recorder.recordPacket(avPacket, DtsPts);
            System.out.println(DtsPts);
            DtsPts += 3600;
        }
    }

}