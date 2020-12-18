package com.dddz.ffmpeg;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.dddz.ffmpeg.build.CommandAssembly;
import com.dddz.ffmpeg.build.CommandAssemblyImpl;
import com.dddz.ffmpeg.build.CommandBuilder;
import com.dddz.ffmpeg.config.ProgramConfig;
import com.dddz.ffmpeg.data.CommandTasker;
import com.dddz.ffmpeg.data.TaskDao;
import com.dddz.ffmpeg.data.TaskDaoImpl;
import com.dddz.ffmpeg.handler.StreamLog;
import com.dddz.ffmpeg.handler.KeepAliveHandler;
import com.dddz.ffmpeg.handler.OutHandlerMethod;
import com.dddz.ffmpeg.handler.TaskHandler;
import com.dddz.ffmpeg.handler.TaskHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FFmpeg命令操作管理器
 * 
 * @author dddz
 * @since jdk1.8
 * @version 2020年10月13日
 */
public class CommandManagerImpl implements CommandManager {
	private final static Logger logger = LoggerFactory.getLogger(CommandManagerImpl.class);

	/**
	 * ffmpeg基础配置
	 */
	private ProgramConfig config;

	/**
	 * 任务持久化器
	 */
	private TaskDao taskDao = null;
	/**
	 * 任务执行处理器
	 */
	private TaskHandler taskHandler = null;
	/**
	 * 命令组装器
	 */
	private CommandAssembly commandAssembly = null;
	/**
	 * 任务消息处理器
	 */
	private OutHandlerMethod ohm = null;
	
	/**
	 * 保活处理器
	 */
	private KeepAliveHandler keepAliveHandler=null;


	/**
	 * 指定任务池大小的初始化，其他使用默认
	 */
	public CommandManagerImpl(ProgramConfig config) {
		this.config=config;
		init(config.getSize());
	}

	/**
	 * 初始化，如果几个处理器未注入，则使用默认处理器
	 */
	public void init(Integer size) {
		if (this.ohm == null) {
			this.ohm = new StreamLog();
		}
		if (this.taskDao == null) {
			this.taskDao = new TaskDaoImpl(size);
			//初始化保活线程
			if(this.config.isKeepalive()) {
				keepAliveHandler = new KeepAliveHandler(taskDao,config.isDebug());
				keepAliveHandler.start();
			}
		}
		if (this.taskHandler == null) {
			this.taskHandler = new TaskHandlerImpl(this.ohm,config.isDebug());
		}
		if (this.commandAssembly == null) {
			this.commandAssembly = new CommandAssemblyImpl();
		}
		
	}

	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public void setTaskHandler(TaskHandler taskHandler) {
		this.taskHandler = taskHandler;
	}

	public void setCommandAssembly(CommandAssembly commandAssembly) {
		this.commandAssembly = commandAssembly;
	}


	/**
	 * 是否已经初始化
	 * 
	 * @param b  如果未初始化时是否初始化
	 */
	public boolean isInit(boolean b) {
		boolean ret = this.ohm == null || this.taskDao == null || this.taskHandler == null|| this.commandAssembly == null;
		if (ret && b) {
			init(null);
		}
		return ret;
	}


	@Override
	public String start(String id, String command, boolean isDebug) {
		if (isInit(true)) {
			logger.error("执行失败，未进行初始化或初始化失败！");
			return null;
		}
		if (id != null && command != null) {
			CommandTasker tasker = taskHandler.process(id,  command,isDebug);
			if (tasker != null) {
				int ret = taskDao.add(tasker);
				if (ret > 0) {
					return tasker.getId();
				} else {
					// 持久化信息失败，停止处理
					taskHandler.stop(tasker.getProcess(), tasker.getThread());
					if (config.isDebug())
						logger.error("持久化失败，停止任务！");
				}
			}
		}
		return null;
	}

	@Override
	public String start(Map<String, String> assembly) {
		// ffmpeg环境是否配置正确
		if (checkConfig()) {
			logger.error("配置未正确加载，无法执行");
			return null;
		}
		// 参数是否符合要求
		if (assembly == null || assembly.isEmpty() || !assembly.containsKey("threadId")) {
			logger.error("参数不正确，无法执行");
			return null;
		}
		String threadId = assembly.get("threadId");
		if (threadId != null && "".equals(threadId.trim())) {
			logger.error("threadId不能为空");
			return null;
		}
		assembly.put("ffmpegPath", config.getPath() + "ffmpeg");
		String command = commandAssembly.assembly(assembly);
		if (command != null) {
			return start(threadId, command, true);
		}

		return null;
	}

	@Override
	public String start(String id, CommandBuilder commandBuilder) {
		// ffmpeg环境是否配置正确
		if (checkConfig()) {
			logger.error("配置未正确加载，无法执行");
			return null;
		}
		String command =commandBuilder.get();
		if (command != null) {
			return start(id, command, true);
		}
		return null;
	}

	private boolean checkConfig() {
		return config == null;
	}
	
	@Override
	public boolean stop(String id) {
		if (id != null && taskDao.isHave(id)) {
			if (config.isDebug())
				logger.info("正在停止任务：" + id);
			CommandTasker tasker = taskDao.get(id);
			if (taskHandler.stop(tasker.getProcess(), tasker.getThread())) {
				taskDao.remove(id);
				return true;
			}
		}
		logger.error("停止任务失败！id=" + id);
		return false;
	}

	@Override
	public int stopAll() {
		Collection<CommandTasker> list = taskDao.getAll();
		Iterator<CommandTasker> iter = list.iterator();
		CommandTasker tasker;
		int index = 0;
		while (iter.hasNext()) {
			tasker = iter.next();
			if (taskHandler.stop(tasker.getProcess(), tasker.getThread())) {
				taskDao.remove(tasker.getId());
				index++;
			}
		}
		if (config.isDebug())
			logger.info("停止了" + index + "个任务！");
		return index;
	}

	@Override
	public CommandTasker query(String id) {
		return taskDao.get(id);
	}

	@Override
	public Collection<CommandTasker> queryAll() {
		return taskDao.getAll();
	}

	@Override
	public void destory() {
		if(keepAliveHandler!=null) {
			//安全停止保活线程
			keepAliveHandler.interrupt();
		}
	}
}
