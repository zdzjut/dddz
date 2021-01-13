package com.dddz.ffmpegcv.util;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.io.IOException;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264;

/**
 * rtsp转rtmp
 */
public class ConvertVideoPacket {
    FFmpegFrameGrabber grabber = null;
    FFmpegFrameRecorderPlus record = null;
    int width = -1, height = -1;
    // 视频参数
    protected double framerate;// 帧率
    protected int bitrate;// 比特率
    /**
     * 选择视频源
     * @param src
     * @throws Exception
     */
    public FFmpegFrameGrabber from(String src) throws Exception {
        // 采集/抓取器
        grabber = new FFmpegFrameGrabber(src);
        //减少缓冲
        grabber.setOption("fflags", "nobuffer");
        // 增加超时参数,防止网络不通阻塞
        //防止丢包卡顿
        if(src.contains("rtsp")) {
            grabber.setOption("rtsp_transport","tcp");
        }
        grabber.start();
        if (width < 0 || height < 0) {
            width = grabber.getImageWidth();
            height = grabber.getImageHeight();
        }
        framerate = grabber.getVideoFrameRate();
        bitrate = grabber.getVideoBitrate();
        return grabber;
    }
    /**
     * 选择输出
     * @param out
     * @throws IOException
     */
    public FFmpegFrameRecorderPlus to(String out) throws Exception {
        // 录制/推流器
        record = new FFmpegFrameRecorderPlus(out, width, height);
        //record.setVideoOption("tune", "zerolatency");
        //record.setVideoOption("crf", "18");
        //record.setGopSize((int)framerate);
        record.setGopSize(2);
        record.setFrameRate(framerate);
        //record.setFrameRate(25);
        record.setVideoBitrate(bitrate);
        //record.setVideoBitrate(2000000);
        record.setAudioChannels(0);
        AVFormatContext fc = null;
        if (out.contains("rtmp") || out.indexOf("flv") > 0) {
            // 封装格式flv
            record.setFormat("flv");
            //record.setAudioCodec(AV_CODEC_ID_AAC);
            record.setVideoCodec(AV_CODEC_ID_H264);
            fc = grabber.getFormatContext();
        }
        record.start(fc);
        return record;
    }
    public FFmpegFrameRecorderPlus to2(String out) throws Exception {
        // 录制/推流器
        record = new FFmpegFrameRecorderPlus(out, width, height);
        //record.setVideoOption("tune", "zerolatency");
        //record.setVideoOption("crf", "18");
        //record.setGopSize((int)framerate);
        record.setGopSize(2);
        record.setFrameRate(framerate);
        //record.setFrameRate(25);
        record.setVideoBitrate(bitrate);
        //record.setVideoBitrate(2000000);
        record.setAudioChannels(0);
        AVFormatContext fc = null;
        if (out.contains("rtmp") || out.indexOf("flv") > 0) {
            // 封装格式flv
            record.setFormat("flv");
        }
        return record;
    }
    public static void main(String[] args) {
        //onvif http://192.168.2.134/onvif/device_service
        //rtsp://admin:admin@192.168.2.134:554/cam/realmonitor?channel=1&subtype=0&unicast=true&proto=Onvif
        //rtmp://106.14.182.20:1935/rtmp/test123
        //rtmp://127.0.0.1:1935/hls/room
        //rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov
        String inuptFile="rtsp://admin:admin@192.168.2.134:554/cam/realmonitor?channel=1&subtype=0";
        String outputFile="rtmp://127.0.0.1:1935/hls/room";
        boolean stat=true;
        try {
            ConvertVideoPacket videoPakcet=new ConvertVideoPacket();
            FFmpegFrameGrabber grabber=videoPakcet.from(inuptFile);
            FFmpegFrameRecorderPlus record=videoPakcet.to(outputFile);
            long err_index = 0;
            int no_frame_index=0;
            while (stat && (no_frame_index<5 || err_index<1)){
                AVPacket pkt=null;
                try {
                    pkt=grabber.grabPacket();
                    if(pkt==null||pkt.size()<=0||pkt.data()==null) {

                        System.out.println("空");
                        no_frame_index++;
                        continue;
                    }
                    boolean flg=record.recordPacket(pkt);
                    err_index+=(flg?0:1);
                }catch (Exception e) {//推流失败
                    e.printStackTrace();
                    err_index++;
                }
                //Thread.sleep(10);
            }
            grabber.stop();
            record.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}