//package com.dddz.ffmpegcv.thread;
//
//import com.dddz.ffmpegcv.ConstValue;
//import org.bytedeco.ffmpeg.avcodec.AVPacket;
//import org.bytedeco.ffmpeg.avformat.AVFormatContext;
//import org.bytedeco.ffmpeg.global.avcodec;
//import org.bytedeco.javacv.FFmpegFrameGrabber;
//import org.bytedeco.javacv.FrameGrabber;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.bytedeco.ffmpeg.global.avcodec.av_packet_ref;
//import static org.bytedeco.ffmpeg.global.avutil.av_freep;
//
//public class CacheRtspThreadBakLast extends Thread {
//    public static List<AVPacket> avPackets = new ArrayList<>(ConstValue.limit);
//    static {
//        for (int i = 0; i < ConstValue.limit; i++) {
//            avPackets.add(null);
//        }
//    }
//    public static AVFormatContext formatContext;
//    public static int current = 0; //当前位置
//    private String inputRtsp;
//    int errorTimes = 0;
//
//
//    public CacheRtspThreadBakLast(String inputRtsp) {
//        this.inputRtsp = inputRtsp;
//    }
//
//    @Override
//    public void run() {
//        try {
//            saveFrame();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    void saveFrame() throws Exception {
//        if (errorTimes > 5) {
//            errorTimes = 0;
////            log.error(lane+"车道录制失效");
//            return;
//        }
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputRtsp);
//        try {
//            grabber.setOption("rtsp_transport", "tcp");
//            grabber.setFrameRate(25);
//            grabber.setVideoBitrate(2000000);
//            grabber.start();
//            formatContext = grabber.getFormatContext();
//            int index = 0;
//            boolean overflow = false;
//            System.out.println("启动前大小"+avPackets.size());
//            while (true) {
//                AVPacket pkt = grabber.grabPacket();
//                AVPacket pkt2=new AVPacket();
//                av_packet_ref(pkt2, pkt);
//                if (overflow) {
//                    AVPacket avPacket = avPackets.get(index);
//                    avcodec.av_packet_unref(avPacket);
//                    av_freep(avPacket);//gc
//                }
//                avPackets.set(index,pkt2);
//                current = index;
//                index++;
//                if (index == ConstValue.limit) {
//                    overflow = true;
//                    index = 0;
//                }
//            }
//        } catch (FrameGrabber.Exception e) {
//            grabber.stop();
//            saveFrame();
//            errorTimes++;
//        }
//    }
//
//
//}