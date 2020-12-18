package com.dddz.ffmpeg.build;

/**
 * 默认流式命令构建器工厂类
 *
 * @author dddz
 */
public class CommandBuilderFactory {

    public static CommandBuilder createBuilder(String path) {
        return new DefaultCommandBuilder(path);
    }
}
