package com.emodou.domain;

/**
 * 课本实体类
 * @author woody
 *
 */
public class EmodouBook {
	
	 private int id;
	 private String bookid;
	 private String bookname;
	 private String bookicon;
	 private String ISBN;//已费
	 private String publishTime;
	 private String resurl;
	 private String description;
	 private String packageid;
	 private String ressize;
	 private String wordurl;
	 private String wordsize;
	 private String press;
	 private String userid;
	 private String availability;
	 private Integer changetime;
	 private int wordstate;
	 private Integer wordChangetime;
	 
	 private int state; //1为未下载课程列表，2为已下载列表， 3已选中
 
	 public Integer getWordChangetime() {
		return wordChangetime;
	}
	public void setWordChangetime(Integer wordChangetime) {
		this.wordChangetime = wordChangetime;
	}
	public String getAvailability() {
		return availability;
	 }
	public void setAvailability(String availability) {
		this.availability = availability;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public int getWordstate() {
		return wordstate;
	}
	public void setWordstate(int wordstate) {
		this.wordstate = wordstate;
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
	public String getBookname() {
		return bookname;
	}
	public void setBookname(String bookname) {
		this.bookname = bookname;
	}
	public String getBookicon() {
		return bookicon;
	}
	public void setBookicon(String bookicon) {
		this.bookicon = bookicon;
	}
	public String getISBN() {
		return ISBN;
	}
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public String getResurl() {
		return resurl;
	}
	public void setResurl(String resurl) {
		this.resurl = resurl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPackageid() {
		return packageid;
	}
	public void setPackageid(String packageid) {
		this.packageid = packageid;
	}
	public String getRessize() {
		return ressize;
	}
	public void setRessize(String ressize) {
		this.ressize = ressize;
	}
	public String getWordurl() {
		return wordurl;
	}
	public void setWordurl(String wordurl) {
		this.wordurl = wordurl;
	}
	public String getWordsize() {
		return wordsize;
	}
	public void setWordsize(String wordsize) {
		this.wordsize = wordsize;
	}
	public String getPress() {
		return press;
	}
	public void setPress(String press) {
		this.press = press;
	}
	public Integer getChangetime() {
		return changetime;
	}
	public void setChangetime(Integer changetime) {
		this.changetime = changetime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	 
}
