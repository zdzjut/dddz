package com.dddz.ffmpeg;

import com.dddz.ffmpeg.build.CommandBuilderFactory;
import com.dddz.ffmpeg.config.ProgramConfig;
import com.dddz.ffmpeg.data.CommandTasker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试
 * @author dddz
 * @since jdk1.8
 * @version 2020年10月13日
 */
public class Example {
	static	ProgramConfig config= new ProgramConfig("D:/ffmpeg/bin/",true,8,"",true);

	public static void main(String[] args) {
		live();
	}
	/**
	 * 完整ffmpeg路径测试
	 * 直播可用 会重连
	 */
	public static void live() {
		CommandManager manager = new CommandManagerImpl(config);
		// -rtsp_transport tcp
		//测试多个任何同时执行和停止情况
		String cmd=config.getPath()+"ffmpeg -i rtsp://admin:admin123@192.168.10.248:554/cam/realmonitor?channel=1&subtype=0 -c copy -rtsp_transport tcp -f hls -hls_time 2.0 -hls_list_size 1 -hls_wrap 20 ./ts/test.m3u8";
		manager.start("live", cmd,true);
	}


	/**
	 * 命令组装器测试
	 */
	public static void test1() throws InterruptedException{
		CommandManager manager = new CommandManagerImpl(config);
		Map<String,String> map = new HashMap<>();
		map.put("appName", "test123");
		map.put("input", "rtsp://admin:admin@192.168.2.236:37779/cam/realmonitor?channel=1&subtype=0");
		map.put("output", "rtmp://192.168.30.21/live/");
		map.put("codec", "h264");
		map.put("fmt", "flv");
		map.put("fps", "25");
		map.put("rs", "640x360");
		map.put("twoPart", "2");
		// 执行任务，id就是appName，如果执行失败返回为null
		String id = manager.start(map);
		// 通过id查询
		CommandTasker info = manager.query(id);
		// 查询全部
		Collection<CommandTasker> infoList = manager.queryAll();
		Thread.sleep(30000);
		 manager.stop(id);
	}
	/**
	 * 默认方式，rtsp->rtmp转流单个命令测试
	 */
	public static void test2() throws InterruptedException{
		CommandManager manager = new CommandManagerImpl(config);
		// -rtsp_transport tcp 
		//测试多个任何同时执行和停止情况
		//默认方式发布任务
		manager.start("tomcat", config.getPath()+"ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat",config.isDebug());
		
		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
	}

	
	/**
	 * rtsp-rtmp转流多任务测试
	 * @throws InterruptedException
	 */
	public static void test3() throws InterruptedException{
		CommandManager manager = new CommandManagerImpl(config);
		// -rtsp_transport tcp 
		//测试多个任何同时执行和停止情况
		//false表示使用配置文件中的ffmpeg路径，true表示本条命令已经包含ffmpeg所在的完整路径
		manager.start("tomcat", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat",false);
		manager.start("tomcat1", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat1",false);
		manager.start("tomcat2", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat2",false);
		manager.start("tomcat3", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat3",false);
		manager.start("tomcat4", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat4",false);
		manager.start("tomcat5", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat5",false);
		manager.start("tomcat6", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat6",false);
		manager.start("tomcat7", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat7",false);
		manager.start("tomcat8", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat8",false);
		manager.start("tomcat9", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat9",false);
		
		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
	}
	
	/**
	 * 测试流式命令行构建器
	 * @throws InterruptedException
	 */
	public static void testStreamCommandAssmbly() throws InterruptedException {
		CommandManager manager = new CommandManagerImpl(config);
		manager.start("test1", CommandBuilderFactory.createBuilder(config.getPath())
				.add("ffmpeg").add("-i","rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov")
				.add("-rtsp_transport","tcp")
				.add("-vcodec","copy")
				.add("-acodec","copy")
				.add("-f","flv")
				.add("-y").add("rtmp://106.14.182.20:1935/rtmp/test1"));
		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
	}
	/**
	 * 测试任务中断自动重启任务
	 */
	public static void testBroken() throws InterruptedException {
		CommandManager manager = new CommandManagerImpl(config);
		manager.start("test1", CommandBuilderFactory.createBuilder(config.getPath())
				.add("ffmpeg").add("-i","rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov")
				.add("-rtsp_transport","tcp")
				.add("-vcodec","copy")
				.add("-acodec","copy")
				.add("-f","flv")
				.add("-y").add("rtmp://106.14.182.20:1935/rtmp/test1"));
		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
		manager.destory();
	}
	/**
	 * 批量测试任务中断自动重启任务
	 */
	public static void testBrokenMuti() throws InterruptedException {
		CommandManager manager = new CommandManagerImpl(config);
		
		manager.start("test1", CommandBuilderFactory.createBuilder(config.getPath())
				.add("ffmpeg").add("-i","rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov")
				.add("-rtsp_transport","tcp")
				.add("-vcodec","copy")
				.add("-acodec","copy")
				.add("-f","flv")
				.add("-y").add("rtmp://106.14.182.20:1935/rtmp/test1"));
		manager.start("test2", CommandBuilderFactory.createBuilder(config.getPath())
				.add("ffmpeg").add("-i","rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov")
				.add("-rtsp_transport","tcp")
				.add("-vcodec","copy")
				.add("-acodec","copy")
				.add("-f","flv")
				.add("-y").add("rtmp://106.14.182.20:1935/rtmp/test2"));
		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
		manager.destory();
	}
	
//	public static void main(String[] args) throws InterruptedException {
//		test1();
//		test2();
//		test3();
//		testStreamCommandAssmbly();
//		testBroken();
//		testBrokenMuti();
//	}
}
