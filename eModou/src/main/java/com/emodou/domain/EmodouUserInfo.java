package com.emodou.domain;

public class EmodouUserInfo {

	private int id;
	private String userid;
	private String username;
	private String nikiname;
	private String sex;
	private String phone;
	private String email;
	private String location;
	private String school;
	private String grade;
	private String role;//性别 0女 1男 2不详
	private String ticket;
	private String tickettimeString;
	private String viplistString;
	private String packagelistString;
	private Long date;
	
	private String qqnum;
	private String usericon;//头像
	private String classcode;
	
	private String parentname;
	private String parentphone;
	private String parentemail;
	
	private String lastRecordTime;//上次同步的时间，E_studyrecord.php上传数据中的lasttime字段
	
	public String getClasscode() {
		return classcode;
	}
	public void setClasscode(String classcode) {
		this.classcode = classcode;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNikiname() {
		return nikiname;
	}
	public void setNikiname(String nikiname) {
		this.nikiname = nikiname;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getTickettimeString() {
		return tickettimeString;
	}
	public void setTickettimeString(String tickettimeString) {
		this.tickettimeString = tickettimeString;
	}
	public String getViplistString() {
		return viplistString;
	}
	public String getLastRecordTime() {
		return lastRecordTime;
	}
	public void setLastRecordTime(String lastRecordTime) {
		this.lastRecordTime = lastRecordTime;
	}
	public void setViplistString(String viplistString) {
		this.viplistString = viplistString;
	}
	public String getPackagelistString() {
		return packagelistString;
	}
	public String getParentemail() {
		return parentemail;
	}
	public void setParentemail(String parentemail) {
		this.parentemail = parentemail;
	}
	public void setPackagelistString(String packagelistString) {
		this.packagelistString = packagelistString;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getQqnum() {
		return qqnum;
	}
	public void setQqnum(String qqnum) {
		this.qqnum = qqnum;
	}
	public String getUsericon() {
		return usericon;
	}
	public void setUsericon(String usericon) {
		this.usericon = usericon;
	}
	public String getParentname() {
		return parentname;
	}
	public void setParentname(String parentname) {
		this.parentname = parentname;
	}
	public String getParentphone() {
		return parentphone;
	}
	public void setParentphone(String parentphone) {
		this.parentphone = parentphone;
	}
}
