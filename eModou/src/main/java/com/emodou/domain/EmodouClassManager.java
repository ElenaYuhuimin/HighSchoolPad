package com.emodou.domain;


/**
 * 课程管理类
 * 
 * @author woody
 *
 */
public class EmodouClassManager {
	
	private int id;
	private String bookid;
	private String classid;
	private String userid;
	private String type;
	private int state; // 1未学习 2学习中3学完
	private int downloadstate;// 0未下载 1下载中2下载完3等待4
	private String score;
	private float progress;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public int getDownloadstate() {
		return downloadstate;
	}

	public void setDownloadstate(int downloadstate) {
		this.downloadstate = downloadstate;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBookid() {
		return bookid;
	}

	public void setBookid(String bookid) {
		this.bookid = bookid;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

}
