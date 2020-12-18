package com.dddz.ffmpeg;

import com.dddz.ffmpeg.build.CommandAssembly;
import com.dddz.ffmpeg.build.CommandBuilder;
import com.dddz.ffmpeg.data.CommandTasker;
import com.dddz.ffmpeg.data.TaskDao;
import com.dddz.ffmpeg.handler.TaskHandler;

import java.util.Collection;
import java.util.Map;

/**
 * FFmpeg命令操作管理器，可执行FFmpeg命令/停止/查询任务信息
 * 
 * @author dddz
 * @since jdk1.8
 */
public interface CommandManager {
	
	/* 只支持外部传入配置 */

	/**
	 * 注入自己实现的持久层
	 * 
	 * @param taskDao
	 */
	void setTaskDao(TaskDao taskDao);

	/**
	 * 注入ffmpeg命令处理器
	 */
	void setTaskHandler(TaskHandler taskHandler);

	/**
	 * 注入ffmpeg命令组装器
	 */
	void setCommandAssembly(CommandAssembly commandAssembly);

	/**
	 * 通过命令发布任务
	 * @param id - 任务标识
	 * @param command - FFmpeg命令
	 * @param isDebug -
	 */
	String start(String id, String command, boolean isDebug);

	/**
	 * 通过流式命令构建器发布任务
	 * @param commandBuilder
	 */
	String start(String id, CommandBuilder commandBuilder);
	
	/**
	 * 通过组装命令发布任务
	 * 
	 * @param assembly
	 *            -组装命令（详细请参照readme文档说明）
	 */
	String start(Map<String, String> assembly);

	/**
	 * 停止任务
	 * 
	 * @param id
	 */
	boolean stop(String id);

	/**
	 * 停止全部任务
	 * 
	 * @return
	 */
	int stopAll();

	/**
	 * 通过id查询任务信息
	 * 
	 * @param id
	 */
	CommandTasker query(String id);

	/**
	 * 查询全部任务信息
	 * 
	 */
	Collection<CommandTasker> queryAll();
	
	/**
	 * 销毁一些后台资源和保活线程
	 */
	void destory();
	
}
