package com.dddz.ffmpeg.handler;

import java.io.IOException;

import com.dddz.ffmpeg.CommandManager;
import com.dddz.ffmpeg.callback.worker.EventMsgNetWorker;
import com.dddz.ffmpeg.data.CommandTasker;
import com.dddz.ffmpeg.util.ExecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务处理实现
 *
 * @author dddz
 * @version 2016年10月29日
 * @since jdk1.8
 */
public class TaskHandlerImpl implements TaskHandler {
    private final static Logger logger = LoggerFactory.getLogger(TaskHandlerImpl.class);

    private OutHandlerMethod ohm;

    public TaskHandlerImpl(OutHandlerMethod ohm, boolean isDebug) {
        this.ohm = ohm;
    }

    public void setOhm(OutHandlerMethod ohm) {
        this.ohm = ohm;
    }

    @Override
    public CommandTasker process(String id, String command, boolean isDebug) {
        CommandTasker tasker = null;
        try {
            tasker = ExecUtil.createTasker(id, command, ohm ,isDebug);

            if (isDebug)
                logger.info(id + " 执行命令行：" + command);

            return tasker;
        } catch (IOException e) {
            //运行失败，停止任务
            ExecUtil.stop(tasker);

            if (isDebug)
               logger.error(id + " 执行命令失败！进程和输出线程已停止");
            // 出现异常说明开启失败，返回null
            return null;
        }
    }

    @Override
    public boolean stop(Process process) {
        return ExecUtil.stop(process);
    }

    @Override
    public boolean stop(Thread outHandler) {
        return ExecUtil.stop(outHandler);
    }

    @Override
    public boolean stop(Process process, Thread thread) {
        boolean ret = false;
        ret = stop(thread);
        ret = stop(process);
        return ret;
    }
}
