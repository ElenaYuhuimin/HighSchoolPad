package com.emodou.domain;

import java.io.Serializable;
import java.sql.Date;

import android.R.integer;
import android.os.Parcel;
import android.os.Parcelable;

public class EmodouPracticeManager implements Parcelable, Serializable{
	
	private String choice;//用户选了哪个选项
	private String no;//该题是第几题
	
	private String que;
	private String num;//这个题共有多少选项
	private String type;
	private EmodouAnswer asw;
	
	
	public String getChoice() {
		return choice;
	}
	public void setChoice(String choice) {
		this.choice = choice;
	}
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getQue() {
		return que;
	}
	public void setQue(String que) {
		this.que = que;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	
	public EmodouAnswer getAsw() {
		return asw;
	}
	public void setAsw(EmodouAnswer asw) {
		this.asw = asw;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		 out.writeString(que);  
		 out.writeString(num);  
		 out.writeString(type);
		 out.writeString(choice);
		 out.writeString(no);
		 out.writeSerializable(asw);  

		 
		
	}
	
	

    public static final Parcelable.Creator<EmodouPracticeManager> CREATOR
            = new Parcelable.Creator<EmodouPracticeManager>() {
        public EmodouPracticeManager createFromParcel(Parcel in) {
            return new EmodouPracticeManager(in);
        }

        public EmodouPracticeManager[] newArray(int size) {
            return new EmodouPracticeManager[size];
        }
    };
    
    private EmodouPracticeManager(Parcel in) {
    	que = in.readString();
    	num = in.readString();
    	type = in.readString();
    	choice = in.readString();
    	no = in.readString();
    	asw = (EmodouAnswer) in.readSerializable();
    }
	public EmodouPracticeManager() {
		super();
	}
	
}
