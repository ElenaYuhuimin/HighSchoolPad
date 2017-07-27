package com.emodou.util;


public class LrcBean implements Comparable<LrcBean> {
	public int beginTime = 0;
	public int lineTime = 0;
	public String lrcBody = "";
	public String lrccn = "";
	@Override
	public int compareTo(LrcBean another) {// 排序参数
		return this.beginTime - another.beginTime;
	}
}
