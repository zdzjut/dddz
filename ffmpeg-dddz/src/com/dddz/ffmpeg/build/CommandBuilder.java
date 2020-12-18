package com.dddz.ffmpeg.build;

/**
 * 流式命令行构建器
 * 
 * @author dddz
 */
public interface CommandBuilder {

	/**
	 * 创建命令行
	 * 
	 * @param root
	 *            -命令行运行根目录或FFmpeg可执行文件安装目录
	 * @return
	 */
	CommandBuilder create(String root);

	/**
	 * 累加键-值命令
	 * 
	 * @param key
	 * @param val
	 * @return
	 */
	CommandBuilder add(String key, String val);

	/**
	 * 累加命令
	 * 
	 * @param val
	 * @return
	 */
	CommandBuilder add(String val);

	/**
	 * 生成完整命令行
	 * 
	 * @return
	 */
	CommandBuilder build();
	
	/**
	 * 获取已经构建好的命令行
	 * @return
	 */
	String get();
}
