package com.dddz.ffmpeg.handler;
/**
 * 输出消息处理
 * @author dddz
 * @since jdk1.8
 * @version 2020年10月13日
 */
public interface OutHandlerMethod {
	/**
	 * 解析消息
	 * @param id-任务ID
	 * @param msg -消息
	 */
	void parse(String id, String msg);
	
	/**
	 * 任务是否异常中断
	 * @return
	 */
	boolean isbroken();
}
