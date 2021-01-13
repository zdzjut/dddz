package com.dddz.ffmpeg.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认任务消息输出处理
 * @author dddz
 * @since jdk1.8
 * @version 2020年10月13日
 */
public class StreamLog implements OutHandlerMethod{
	private final static Logger logger = LoggerFactory.getLogger(StreamLog.class);

	/**
	 * 任务是否异常中断，如果
	 */
	public boolean isBroken=false;
	
	@Override
	public void parse(String id,String msg) {
		//过滤消息
		if (msg.contains("fail")) {
			logger.debug(id + "任务可能发生故障：" + msg);
			logger.debug("失败，设置中断状态");
			isBroken=true;
		}else if(msg.contains("miss")) {
			logger.debug(id + "任务可能发生丢包：" + msg);
			logger.debug("失败，设置中断状态");
			isBroken=true;
		}else {
			isBroken=false;
			logger.debug("来自"+id + "的消息：" + msg);
		}

	}

	@Override
	public boolean isbroken() {
		return isBroken;
	}
	
}
