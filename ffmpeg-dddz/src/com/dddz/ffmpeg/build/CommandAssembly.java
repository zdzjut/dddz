package com.dddz.ffmpeg.build;

import java.util.Map;
/**
 * 命令组装器接口
 * @author dddz
 * @since jdk1.8
 * @version 2016年10月29日
 */
public interface CommandAssembly {
	/**
	 * 将参数转为ffmpeg命令
	 * @param paramMap
	 * @return
	 */
	public String assembly(Map<String, String> paramMap);
	
	public String assembly();
}
