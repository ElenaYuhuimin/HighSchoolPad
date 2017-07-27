package com.emodou.domain;

/**
 * 学习包实体类
 * @author woody
 *
 */
public class EmodouPackage {
 @Override
    //登陆时返回的packagelist中有一个ifgroup属性，目前所有的包都是一样的，所以没有存，
    //若想存，请修改ChangepackageActivity界面中initSqlPacFromUserinfo函数
	public String toString() {
		return "EmodouPackage [id=" + id + ", packageid=" + packageid
				+ ", packagename=" + packagename + ", packageicon="
				+ packageicon + ", packagedes=" + packagedes + ", endtime="
				+ endtime  + "]";
	}
 
	 private int id;
	 private String packageid;
	 private String packagename;
	 private String packageicon;
	 private String packagedes;
	 private String endtime;
	 private String userid;
	 private Integer changetime; //从服务器获得的上次修改时间,在Changebook界面通过getpackageinfo.php获得
	 private int state;
	 private String available;//已弃用
	 private String price;
	 
	 private String ifgroup;//是否是大客户包（1是 0不是）
	 
	 private String packageMode;
	 
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPackageid() {
		return packageid;
	}
	public void setPackageid(String packageid) {
		this.packageid = packageid;
	}
	public String getPackagename() {
		return packagename;
	}
	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}
	public String getPackageicon() {
		return packageicon;
	}
	public void setPackageicon(String packageicon) {
		this.packageicon = packageicon;
	}
	public String getPackagedes() {
		return packagedes;
	}
	public void setPackagedes(String packagedes) {
		this.packagedes = packagedes;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getAvailable() {
		return available;
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPackageMode() {
		return packageMode;
	}
	public void setPackageMode(String packageMode) {
		this.packageMode = packageMode;
	}
	public String getIfgroup() {
		return ifgroup;
	}
	public void setIfgroup(String ifgroup) {
		this.ifgroup = ifgroup;
	}
}
