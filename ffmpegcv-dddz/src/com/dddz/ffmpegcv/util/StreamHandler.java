package com.dddz.ffmpegcv.util;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_ref;
import static org.bytedeco.ffmpeg.global.avutil.AV_TIME_BASE;

public class StreamHandler extends Thread {
    String id;
    String rtspUrl;
    String rtmpUrl;

    @Override
    public void run() {
        //拉流的rtsp链接
        String inputFile = rtspUrl;
        //推流rtmp
        String outputFile = rtmpUrl;
        //推流rtmp2
        String rtmpUrl2 = "";
        String host = "127.0.0.1";
        String host2 = "127.0.0.1";
        int port = 1935;
        int port2 = 1936;
        long startTime1 = 0;
        boolean flag = true;
        boolean flag3 = true;
        try {
            //检测端口情况，防止拉流产生阻塞

            //转流器
            ConvertVideoPacket videoPakcet = new ConvertVideoPacket();
            //创建第一个推流器
            FFmpegFrameGrabber grabber = videoPakcet.from(inputFile);
            System.out.println(id + "--->打开拉流器");
            //第一个推流器
            FFmpegFrameRecorderPlus record = videoPakcet.to(outputFile);
            System.out.println(id + "--->打开录制器/推流器1");
            //第二个推流器，根据需要开启
            FFmpegFrameRecorderPlus record2 = null;
            long err_index = 0;
            int no_frame_index = 0;
            System.out.println(id + "--->开始推流");
            AVPacket pkt = null;
            AVPacket pkt2 = null;
            int f = 0;
            long startTime = System.currentTimeMillis();
            long dts = 90000;
            dts = AV_TIME_BASE;
            long dts1 = 0;
            startTime1 = startTime;

            while (flag) {
                if (flag3 && f == 0) {
                    try {
                        //检测推流器端口情况，防止推流异常

                        //创建第二个推流器
                        record2 = videoPakcet.to(rtmpUrl2);
                        System.out.println("打开公网推流器");
                        f = 1;
                    } catch (Exception e) {
                        //flag=false;
                        System.out.println(e.getMessage());
                    }


                }
                if (!flag3 && f == 1) {
                    f = 0;
                    if (record2 != null) {
                        record2.stop();
                        record2.release();
                    }
                }
                if (err_index > 0 || no_frame_index >= 5) {
                    break;
                }
                pkt = null;
                try {
                    pkt = grabber.grabPacket();
                    if (pkt == null || pkt.size() <= 0 || pkt.data() == null) {
                        no_frame_index++;
                        System.out.println(id + "---->空--->" + no_frame_index);
                        continue;
                    }


                    if (flag3 && f == 1) {
                        pkt2 = new AVPacket();
                        //BeanUtils.copyProperties(pkt2,pkt);
                        //av_packet_ref(pkt2, pkt)==0 av_copy_packet av_packet_copy_props
                        //拷贝pkt对象，要使用提供的函数
                        av_packet_ref(pkt2, pkt);
                        err_index += (record.recordPacket(pkt) ? 0 : 1);
                        try {
                            record2.recordPacket(pkt2);
                        } catch (Exception e) {
                            System.out.println("record2发送失败，请检测远程服务器是否打开或查看本地录制器是否正常");
                        }
                    } else {
                        //有些摄像头时间戳有问题，可以自己进行计算
                        //dts+=3600;
                        // pkt.dts(dts);
                        //System.out.println(pkt.dts());
                        err_index += (record.recordPacket(pkt) ? 0 : 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    err_index++;
                }
                Thread.sleep(10);
            }
            long endTime = System.currentTimeMillis();
            long cha = endTime - startTime1;
            System.out.println(id + "---->推流结束----->运行时间=" + cha / 1000 / 60 + "分");
            System.exit(0);
            flag = false;
            grabber.stop();
            record.stop();
            record.release();
            if (record2 != null) {
                record2.stop();
                record2.release();
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long cha = endTime - startTime1;
            System.out.println(id + "---->推流异常>----->运行时间=" + cha / 1000 / 60 + "分");
            System.exit(0);
            flag = false;
        }
    }
}
