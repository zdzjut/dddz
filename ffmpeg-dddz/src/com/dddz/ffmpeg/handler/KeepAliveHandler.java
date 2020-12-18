package com.dddz.ffmpeg.handler;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.dddz.ffmpeg.CommandManagerImpl;
import com.dddz.ffmpeg.data.CommandTasker;
import com.dddz.ffmpeg.data.TaskDao;
import com.dddz.ffmpeg.util.ExecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务保活处理器（一个后台保活线程，用于处理异常中断的持久任务）
 * @author dddz
 *
 */
public class KeepAliveHandler extends Thread{
	private final static Logger logger = LoggerFactory.getLogger(KeepAliveHandler.class);

	/**待处理队列*/
	private static Queue<String> queue=null;
	
	public int err_index=0;//错误计数
	public boolean isDebug;

	public volatile int stop_index=0;//安全停止线程标记
	
	/** 任务持久化器*/
	private TaskDao taskDao = null;
	
	public KeepAliveHandler(TaskDao taskDao,boolean isDebug) {
		super();
		this.taskDao=taskDao;
		this.isDebug=isDebug;
		queue=new ConcurrentLinkedQueue<>();
	}

	public static void add(String id ) {
		if(queue!=null) {
			queue.offer(id);
		}
	}
	
	public boolean stop(Process process) {
		if (process != null) {
			process.destroy();
			return true;
		}
		return false;
	}
	
	@Override
	public void run() {
		while (stop_index==0) {
			if(queue==null) {
				continue;
			}
			String id=null;
			CommandTasker task=null;
			
			try {
				while(queue.peek() != null) {
					logger.error("准备重启任务："+queue);
					id=queue.poll();
					task=taskDao.get(id);
					//重启任务
					ExecUtil.restart(task,isDebug);
				}
			}catch(IOException e) {
				logger.error(id+" 任务重启失败，详情："+task);
				//重启任务失败
				err_index++;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void interrupt() {
		stop_index=1;
	}
	
}
