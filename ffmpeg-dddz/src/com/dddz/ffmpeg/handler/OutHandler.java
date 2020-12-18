package com.dddz.ffmpeg.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 任务消息输出处理器
 * @author dddz
 * @since jdk1.8
 * @version 2020年10月13日
 */
public class OutHandler extends Thread {

	private final static Logger logger = LoggerFactory.getLogger(OutHandler.class);

	/**控制状态 */
	private volatile boolean desstatus = true;

	/**读取输出流*/
	private BufferedReader br;

	/**任务ID*/
	private String id;

	/**消息处理方法*/
	private OutHandlerMethod ohm;

	private boolean isDebug;


	/**
	 * 创建输出线程（默认立即开启线程）
	 */
	public static OutHandler create(InputStream is, String id,OutHandlerMethod ohm,boolean isDebug) {
		return create(is, id, ohm,true,isDebug);
	}

	/**
	 * 创建输出线程
	 */
	public static OutHandler create(InputStream is, String id,OutHandlerMethod ohm,boolean start,boolean isDebug) {
		OutHandler out= new OutHandler(is, id, ohm,isDebug);
		if(start)
			out.start();
		return out;
	}


	public void setDesStatus(boolean desStatus) {
		this.desstatus = desStatus;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OutHandlerMethod getOhm() {
		return ohm;
	}

	public OutHandler(InputStream is, String id,OutHandlerMethod ohm,boolean isDebug) {
		br = new BufferedReader(new InputStreamReader(is));
		this.id = id;
		this.ohm=ohm;
		this.isDebug=isDebug;
	}

	/**
	 * 重写线程销毁方法，安全的关闭线程
	 */
	public void destroy() {
		setDesStatus(false);
	}

	/**
	 * 执行输出线程
	 */
	@Override
	public void run() {
		String msg;
		try {
			if (isDebug) {
				logger.info(id + "开始推流！");
			}
			while (desstatus && (msg = br.readLine()) != null) {
				ohm.parse(id,msg);
				if(ohm.isbroken()) {
					logger.error("检测到<"+id+">中断，提交重启任务给保活处理器");
					//如果发生异常中断，立即进行保活
					//把中断的任务交给保活处理器进行进一步处理
					KeepAliveHandler.add(id);
				}
			}
		} catch (IOException e) {
			logger.error("发生内部异常错误，自动关闭[" + this.getId() + "]线程");
			destroy();
		} finally {
			if (this.isAlive()) {
				destroy();
			}
		}
	}

}
