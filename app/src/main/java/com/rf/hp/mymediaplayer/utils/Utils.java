package com.rf.hp.mymediaplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;


/**
 * 将时间转成常见的格式的工具类
 */
public class Utils {

	private StringBuilder mFromatBuilder;
	private Formatter mformatter;
	public Utils(){
		mFromatBuilder = new StringBuilder();
		mformatter = new Formatter(mFromatBuilder,Locale.getDefault());
	}
	
	public String stringForTime(int timeMs){
		int totalSeconds = timeMs/1000;
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		mFromatBuilder.setLength(0);
		if(hours>0){
			return mformatter.format("%d:%02d:%02d",hours,minutes,seconds).toString();
		}else{
			return mformatter.format("%02d:%02d",minutes,seconds).toString();
		}
	}
	public String longForDate(long time){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		String format = sdf.format(date);
		return format;
	}
	
	public String getSystemTime(){
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		return format.format(new Date());
	}
}
