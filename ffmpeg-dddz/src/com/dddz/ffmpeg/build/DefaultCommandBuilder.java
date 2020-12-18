package com.dddz.ffmpeg.build;


/**
 * 默认流式命令行构建器（非线程安全）
 * @author dddz
 */
public class DefaultCommandBuilder implements CommandBuilder {

	StringBuilder Builder=null;
	String command=null;
	


	public DefaultCommandBuilder(String path) {
		create(path);
	}


	@Override
	public CommandBuilder create(String path) {
		Builder=new StringBuilder(path);
		return this;
	}


	@Override
	public CommandBuilder add(String key, String val) {
		return add(key).add(val);
	}

	@Override
	public CommandBuilder add(String val) {
		if(Builder!=null) {
			Builder.append(val);
			addBlankspace();
		}
		return this;
	}

	@Override
	public CommandBuilder build() {
		if(Builder!=null) {
			command=Builder.toString();
		}
		return this;
	}
	
	private void addBlankspace() {
		Builder.append(" ");
	}

	@Override
	public String get() {
		if(command==null) {
			build();
		}
		return command;
	}

}
