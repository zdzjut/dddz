//package com.dddz.ffmpegcv.thread;
//
//import com.dddz.ffmpegcv.ConstValue;
//import com.dddz.ffmpegcv.util.FFmpegFrameRecorderPlus;
//import org.bytedeco.ffmpeg.avcodec.AVPacket;
//import org.bytedeco.javacv.FrameRecorder;
//
//import java.util.List;
//
//import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264;
//import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MPEG4;
//
//
//public class VideoThreadBakLast extends Thread {
//
//    private String outputFile;
//    int offset=75; //当前时间向前75帧
//    int videoLength=150; //总共150帧
//    static int anInt=1;
//
//    public VideoThreadBakLast(String outputFile) {
//        this.outputFile = outputFile;
//
//    }
//
//    @Override
//    public void run() {
//        FFmpegFrameRecorderPlus recorder = new FFmpegFrameRecorderPlus(outputFile, 1080, 640, 0);
//        recorder.setFrameRate(25);
//        recorder.setVideoBitrate(2000000);
//        recorder.setPixelFormat(AV_CODEC_ID_H264);
//        recorder.setVideoCodec(AV_CODEC_ID_MPEG4);
//        try {
//            recorder.start(CacheRtspThread.formatContext);
//            int current = CacheRtspThread.current;
////            int current = 956;
//
//           recordFrame(recorder, current);
//            recorder.stop();
//
//        } catch (FrameRecorder.Exception e) {
//            try {
//                recorder.stop();
//            } catch (FrameRecorder.Exception exception) {
//                exception.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * @param startIndex low endpoint (inclusive) of the subList
//     */
//    void recordFrame(FFmpegFrameRecorderPlus recorder, int startIndex) throws FrameRecorder.Exception {
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        if (startIndex < offset) {
//            startIndex = ConstValue.limit + startIndex - offset;
//        }else {
//            startIndex = startIndex - offset;
//        }
//        int sum = startIndex + videoLength;
//        List<AVPacket> avPacketList;
//        if (sum > ConstValue.limit) { //超过最大值
//            avPacketList = CacheRtspThread.avPackets.subList(startIndex, ConstValue.limit);
//            List<AVPacket> list2 = CacheRtspThread.avPackets.subList(0, sum - ConstValue.limit);
//            avPacketList.addAll(list2);
//        } else {
//            avPacketList = CacheRtspThread.avPackets.subList(startIndex, sum);
//        }
//        System.out.println(startIndex + "--------" + sum);
//        System.out.println("pk集合大小：" +CacheRtspThread.avPackets.size());
//
//        int DtsPts=0;// 目前来看两个参数一致
////        int dts=0,pts=0;
//        for (AVPacket avPacket : avPacketList) {
//            recorder.recordPacket(avPacket,DtsPts);
//            System.out.println(DtsPts);
//            DtsPts+=3600;
//        }
//    }
//
//}