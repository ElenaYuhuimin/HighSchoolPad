package com.emodou.domain;

import android.R.integer;

/**
 * 用户单词管理类
 * @author woody
 *
 */
public class EmodouWordManager {

	private int id;
	private String userid;
	private String wordname;

	private String bookid;
	
	private String classid;
	private String isAddToNewWordsBood;
	private int entitleRighttimes; //给英文找汉意的正确次数
	private int cntitleRighttimes; //给中文找英文的正确次数
	private int entitleWrongtimes; //给英文找汉意的错误次数
	private int cntitleWrongtimes; //给中文找英文的错误次数
	private int learnState;//-1陌生 0还未学习 1模糊 2熟悉
	private int reviewState;//-1需要复习 0未学习 1学习过 2复习过（-1的状态还未用到）
	private long lastStateTime;
	private int lastState;
	
	//为了同步上传重新加的字段
	private int cright;//认识次数(本地累加，每次重新学习的时候不清空）
	private int cprompt;//提醒后认识次数（本地累加）
	private int cwrong;//不认识次数（本地累加）
	private String last;//最后一次：0正确 1提示 2错误（本地替换）
	
	public String getWordname() {
		return wordname;
	}
	public void setWordname(String wordname) {
		this.wordname = wordname;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getIsAddToNewWordsBood() {
		return isAddToNewWordsBood;
	}
	public void setIsAddToNewWordsBood(String isAddToNewWordsBood) {
		this.isAddToNewWordsBood = isAddToNewWordsBood;
	}
	public String getClassid() {
		return classid;
	}
	public void setClassid(String classid) {
		this.classid = classid;
	}
	public int getLearnState() {
		return learnState;
	}
	public void setLearnState(int learnState) {
		this.learnState = learnState;
	}
	public int getReviewState() {
		return reviewState;
	}
	public void setReviewState(int reviewState) {
		this.reviewState = reviewState;
	}
	public long getLastStateTime() {
		return lastStateTime;
	}
	public void setLastStateTime(long lastStateTime) {
		this.lastStateTime = lastStateTime;
	}
	public int getLastState() {
		return lastState;
	}
	public void setLastState(int lastState) {
		this.lastState = lastState;
	}
	public int getEntitleRighttimes() {
		return entitleRighttimes;
	}
	public void setEntitleRighttimes(int entitleRighttimes) {
		this.entitleRighttimes = entitleRighttimes;
	}
	public int getCntitleRighttimes() {
		return cntitleRighttimes;
	}
	public void setCntitleRighttimes(int cntitleRighttimes) {
		this.cntitleRighttimes = cntitleRighttimes;
	}
	public int getEntitleWrongtimes() {
		return entitleWrongtimes;
	}
	public void setEntitleWrongtimes(int entitleWrongtimes) {
		this.entitleWrongtimes = entitleWrongtimes;
	}
	public int getCntitleWrongtimes() {
		return cntitleWrongtimes;
	}
	public void setCntitleWrongtimes(int cntitleWrongtimes) {
		this.cntitleWrongtimes = cntitleWrongtimes;
	}
	public int getCright() {
		return cright;
	}
	public void setCright(int cright) {
		this.cright = cright;
	}
	public int getCprompt() {
		return cprompt;
	}
	public void setCprompt(int cprompt) {
		this.cprompt = cprompt;
	}
	public int getCwrong() {
		return cwrong;
	}
	public void setCwrong(int cwrong) {
		this.cwrong = cwrong;
	}
	public String getLast() {
		return last;
	}
	public void setLast(String last) {
		this.last = last;
	}
	public String getBookid() {
		return bookid;
	}
	public void setBookid(String bookid) {
		this.bookid = bookid;
	}
	
	
}
