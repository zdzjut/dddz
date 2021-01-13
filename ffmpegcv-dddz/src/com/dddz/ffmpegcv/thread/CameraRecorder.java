package com.dddz.ffmpegcv.thread;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;

import java.util.ArrayList;
import java.util.List;

public class CameraRecorder {
    private int lane;
    private int limit;
    private int current;
    private String inputRtsp;
    private AVFormatContext formatContext;
    private List<AVPacket> avPackets;

    public CameraRecorder(int lane, int limit,  String inputRtsp) {
        this.current = 0;
        this.lane = lane;
        this.limit = limit;
        this.inputRtsp = inputRtsp;
        this.avPackets = new ArrayList<>(limit);
        for (int i = 0; i < limit; i++) {
            this.avPackets.add(null);
        }
    }

    public void increase() {
        ++current;
    }
    public int getLane() {
        return lane;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public String getInputRtsp() {
        return inputRtsp;
    }

    public void setInputRtsp(String inputRtsp) {
        this.inputRtsp = inputRtsp;
    }

    public AVFormatContext getFormatContext() {
        return formatContext;
    }

    public void setFormatContext(AVFormatContext formatContext) {
        this.formatContext = formatContext;
    }

    public List<AVPacket> getAvPackets() {
        return avPackets;
    }

    public void setAvPackets(List<AVPacket> avPackets) {
        this.avPackets = avPackets;
    }
}
