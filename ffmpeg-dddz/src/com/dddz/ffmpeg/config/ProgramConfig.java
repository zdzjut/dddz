package com.dddz.ffmpeg.config;

/**
 * 程序基础配置
 * @author dddz
 * 
 */
public class ProgramConfig {
	
	private String path;//默认命令行执行根路径
	private boolean debug;//是否开启debug模式
	private Integer size;//任务池大小
	private String callback;//回调通知地址
	private boolean keepalive = true;//是否开启保活

	/**
	 *
	 * @param path exe所在路径（不包含exe） *必填
	 * @param debug 是否开启debug模式
	 * @param size 任务池大小 *必填
	 * @param callback 回调通知地址
	 * @param keepalive 是否开启保活 默认是
	 */
	public ProgramConfig(String path, boolean debug, Integer size, String callback, boolean keepalive) {
		this.path = path;
		this.debug = debug;
		this.size = size;
		this.callback = callback;
		this.keepalive = keepalive;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public boolean isKeepalive() {
		return keepalive;
	}

	public void setKeepalive(boolean keepalive) {
		this.keepalive = keepalive;
	}

	@Override
	public String toString() {
		return "ProgramConfig [path=" + path + ", debug=" + debug + ", size=" + size + ", callback=" + callback
				+ ", keepalive=" + keepalive + "]";
	}
}
