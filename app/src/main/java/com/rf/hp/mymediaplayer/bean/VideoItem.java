package com.rf.hp.mymediaplayer.bean;

import java.io.Serializable;

public class VideoItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String title;
	
	private String duration;
	
	private long size;
	
	private String data;
	private String displayName;
	private String path;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size2) {
		this.size = size2;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "VideoItem [title=" + title + ", duration=" + duration
				+ ", size=" + size + ", data=" + data + "]";
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
