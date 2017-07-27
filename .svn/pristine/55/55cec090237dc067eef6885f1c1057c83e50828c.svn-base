package com.emodou.util;

import java.io.BufferedInputStream;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouLearnTime;
import com.emodou.domain.EmodouUserInfo;
import com.emodou.domain.EmodouLastSynchronismTime;
import com.emodou.domain.EmodouWordClass;
import com.emodou.domain.EmodouWordForWlist;
import com.emodou.domain.EmodouWordInfo;
import com.emodou.domain.EmodouWordManager;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.multipart.content.ContentBody;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ValidateUtils{
	public static boolean isMobile(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	public static boolean isNetworkConnected(Context context){
		if(context!=null){
			ConnectivityManager mConnectivityManager = (ConnectivityManager)context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
										.getActiveNetworkInfo();
			if(mNetworkInfo!=null){
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	public static String readFromFile(Context context, String filename) {

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			File targetFile = new File(filename);
			String readedStr = "";

			try {
				if (!targetFile.exists()) {
					targetFile.createNewFile();
					return "No File error ";
				} else {
					InputStream in = new BufferedInputStream(
							new FileInputStream(targetFile));
					BufferedReader br = new BufferedReader(
							new InputStreamReader(in, "UTF-8"));
					String tmp;

					while ((tmp = br.readLine()) != null) {
						readedStr += tmp;
					}
					br.close();
					in.close();

					return readedStr;
				}
			} catch (Exception e) {
				Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
				return e.toString();
			}
		} else {
			Toast.makeText(context, "未发现SD卡！", Toast.LENGTH_LONG).show();
			return "SD Card error";
		}

	}
	
	public static String getUserid(Context context) {
		DbUtils dbUtils = DbUtils.create(context);
		EmodouUserInfo userInfo = null;
		try {
			userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (userInfo != null) {
			return userInfo.getUserid();
		} else {
			return "";
		}

	}
	
	public static boolean isExist(String path) {
		File file = new File(path);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		return (file.exists());
	}
	
	/**
	 * 删除文件夹以及目录下的文件
	 * 
	 * @param filePath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String filePath) {
		boolean flag = false;
		// 如果filePath不以文件分隔符结尾，自动添加文件分隔符
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		File dirFile = new File(filePath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		File[] files = dirFile.listFiles();
		// 遍历删除文件夹下的所有文件(包括子目录)
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				// 删除子文件
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				// 删除子目录
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前空目录
		return dirFile.delete();
	}
	
	/**
	 * 删除单个文件
	 * 
	 * @param filePath
	 *            被删除文件的文件名
	 * @return 文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return false;
	}
	
	public static void unzipFiles3(File file, String destDir)
			throws FileNotFoundException, IOException {
		// 压缩文件
		File srcZipFile = file;
		// 基本目录
		if (!destDir.endsWith("/")) {
			destDir += "/";
		}
		String prefixion = destDir;
		Log.v("prefixion", prefixion);

		// 压缩输入流
		ZipInputStream zipInput = new ZipInputStream(new FileInputStream(
				srcZipFile));
		// 压缩文件入口
		ZipEntry currentZipEntry = null;
		// 循环获取压缩文件及目录
		while ((currentZipEntry = zipInput.getNextEntry()) != null) {
			// 获取文件名或文件夹名
			String fileName = currentZipEntry.getName();
			Log.v("filename", fileName);
			// 构成File对象
			File tempFile = new File(prefixion + fileName);
			// 父目录是否存在
			if (!tempFile.getParentFile().exists()) {
				// 不存在就建立此目录
				tempFile.getParentFile().mkdir();
			}
			// 如果是目录，文件名的末尾应该有“/"
			if (currentZipEntry.isDirectory()) {
				// 如果此目录不在，就建立目录。
				if (!tempFile.exists()) {
					tempFile.mkdir();
				}
				// 是目录，就不需要进行后续操作，返回到下一次循环即可。
				continue;
			}
			// 如果是文件
			if (tempFile != null || !tempFile.exists()) {
				// 不存在就重新建立此文件。当文件不存在的时候，不建立文件就无法解压缩。
				tempFile.createNewFile();
			}
			// 输出解压的文件
			FileOutputStream tempOutputStream = new FileOutputStream(tempFile);

			// 获取压缩文件的数据
			byte[] buffer = new byte[10240];
			int hasRead = 0;
			// 循环读取文件数据
			while ((hasRead = zipInput.read(buffer)) > 0) {
				tempOutputStream.write(buffer, 0, hasRead);
			}
			tempOutputStream.flush();
			tempOutputStream.close();
		}
		zipInput.close();

	}
	
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}

		return false;
	}
	
	public static String getTicket(Context context) {
		DbUtils dbUtils = DbUtils.create(context);
		EmodouUserInfo userInfo = null;
		try {
			userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (userInfo != null) {
			return userInfo.getTicket();
		} else {
			return "";
		}

	}
	
	/**
	 * 获得上次更新时间
	 * 
	 * @param context
	 * @return
	 */
	public static String getLastSycTime(Context context, String userid) {

		String lasttime = "";
		DbUtils db = DbUtils.create(context);
		try {
			EmodouLastSynchronismTime lastSynchronismTime = db
					.findFirst(Selector.from(EmodouLastSynchronismTime.class)
							.where("userid", "=", userid));
			if (lastSynchronismTime != null) {
				lasttime = lastSynchronismTime.getLasttime();
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lasttime;
	}
	
	public static void updateLastTimeFromServer(JSONObject result,
			String userid, Context contex) throws JSONException {
		// 成功后将同步时间更新
		String time = result.getString("Time");
		if (time != null && !time.equals("")) {
			DbUtils db = DbUtils.create(contex);
			try {
				EmodouLastSynchronismTime lastSynchronismTime = db
						.findFirst(Selector.from(
								EmodouLastSynchronismTime.class).where(
								"userid", "=", userid));
				if (lastSynchronismTime != null) {
					lastSynchronismTime.setLasttime(time);
					db.delete(lastSynchronismTime);
					EmodouLastSynchronismTime lastSynchronismTime2 = new EmodouLastSynchronismTime();
					lastSynchronismTime2.setLasttime(time);
					lastSynchronismTime2.setUserid(userid);
					db.saveBindingId(lastSynchronismTime2);
				} else {
					EmodouLastSynchronismTime lastSynchronismTime2 = new EmodouLastSynchronismTime();
					lastSynchronismTime2.setLasttime(time);
					lastSynchronismTime2.setUserid(userid);
					db.saveBindingId(lastSynchronismTime2);
				}

			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	public static void updateStudyRecordFromServer(String classrecordString,
			String userid, Context contex) throws JSONException {
		JSONArray classrecordArray = new JSONArray(classrecordString);

		if (classrecordArray != null) {
			for (int i = 0; i < classrecordArray.length(); i++) {
				JSONObject classrcord = classrecordArray.getJSONObject(i);
				// 如果课程信息不为空
				if (classrcord != null) {
					String bookid = classrcord.getString("bookid");
					String classid = classrcord.getString("classid");
					String score = classrcord.getString("score");
					String classstatus = classrcord.getString("status");
					DbUtils db = DbUtils.create(contex);
					try {
						EmodouClassManager classManager = db.findFirst(Selector
								.from(EmodouClassManager.class)
								.where("bookid", "=", bookid)
								.and("classid", "=", classid)
								.and("userid", "=", userid));
						// 如果有数据就更新一下数据库。
						if (classManager != null) {
							if (Integer.parseInt(classstatus) > classManager
									.getState()) {
								classManager.setState(Integer
										.parseInt(classstatus));
							}
							if (Integer.parseInt(score) > Integer
									.parseInt(classManager.getScore())) {
								classManager.setScore(score);
							}

							db.update(classManager);

						}
						// 没有的话就像数据库里面插入一条数据
						else {
							EmodouClassManager classManager2 = new EmodouClassManager();
							classManager2.setBookid(bookid);
							classManager2.setClassid(classid);
							classManager2
									.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_NOT_DOWNLOAD);
							classManager2.setProgress(0);
							classManager2.setScore(score);
							classManager2.setState(Integer
									.parseInt(classstatus));
							classManager2.setUserid(userid);
							db.saveBindingId(classManager2);
						}

					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

	}
	
	
	public static void updateTimeRecordFromServer(String timerecordString,
			String userid, Context contex) throws JSONException {
		JSONArray timerecordArray = new JSONArray(timerecordString);
		// 如果有下行的时间数据
		if (timerecordArray != null) {
			for (int i = 0; i < timerecordArray.length(); i++) {
				JSONObject timeObject = timerecordArray.getJSONObject(i);
				String startime = timeObject.getString("starttime");
				String endtime = timeObject.getString("endtime");
				String type = timeObject.getString("type");

				DbUtils db = DbUtils.create(contex);
				try {
					EmodouLearnTime learntime = db.findFirst(Selector
							.from(EmodouLearnTime.class)
							.where("startTime", "=", startime)
							.and("type", "=", type));
					if (learntime == null) {
						EmodouLearnTime learnTime2 = new EmodouLearnTime();
						learnTime2.setUserid(userid);
						learnTime2.setStartTime(Long.parseLong(startime));
						learnTime2.setEndTime(Long.parseLong(endtime));
						learnTime2.setType(type);

						// 按月月-天天 记录哪一天
						SimpleDateFormat formatter = new SimpleDateFormat(
								"MM-dd");
						Date date = new Date(Long.parseLong(endtime) * 1000);
						String datesteing = formatter.format(date);
						System.out.println(formatter.format(date));
						learnTime2.setDate(datesteing);

						db.saveBindingId(learnTime2);

					}
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}
	
	public static Intent sycStudyRecord(Context contex, String bookid,
			String classid, String state, String userid, String ticket,
			String flag_up) {
		JSONObject studyObject = new JSONObject();
		JSONObject SynObject = new JSONObject();
		JSONArray classrecordArray = new JSONArray();
		JSONObject classObject = new JSONObject();
		try {
			classObject.put("bookid", bookid);
			classObject.put("classid", classid);
			classObject.put("status", state);
			classObject.put("score", "0");
			//classObject.put("studytime", "listen");
			classrecordArray.put(classObject);
			SynObject.put("classrecord", classrecordArray);
			studyObject.put("record", SynObject);
			studyObject.put("userid", userid);
			studyObject.put("ticket", ticket);
			studyObject.put("lasttime",
					ValidateUtils.getLastSycTime(contex, userid));
			studyObject.put("flag_up", flag_up);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Intent intent = new Intent("com.emodou.service.SycStudyRecordService");
		Bundle bundle = new Bundle();
		bundle.putString("category", Constants.STUDY_RECORD_CATORY_RECORD);
		bundle.putString("record", studyObject.toString());
		intent.putExtras(bundle);

		return intent;
		// 启动同步服务
	}
	
	public static void fileDelete(String path) {
		File file = new File(path);
		file.delete();
	}
	
	public static boolean isAvilible(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		List<String> pName = new ArrayList<String>();// 用于存储所有已安装程序的包名
		// 从pinfo中将包名字逐一取出，压入pName list中
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);// 判断pName中是否有目标程序的包名，有TRUE，没有FALSE
	}
	
	public static boolean hasSD() {
		File sdDir = null;
		File sdDir1 = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		return sdCardExist;
		/*
		 * { sdDir = Environment.getExternalStorageDirectory();//获取根目录 sdDir1 =
		 * Environment.getDataDirectory(); }
		 * System.out.println("getExternalStorageDirectory(): "
		 * +sdDir.toString());
		 * System.out.println("getDataDirectory(): "+sdDir1.toString());
		 */
	}
	
	public static File getSDPath() {
		File sdDir = null;
		sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		return sdDir;
	}
	
	/**
	 * 播放音乐函数 如果是本地存在的音乐直接播放如果是云端的需要下载
	 * 
	 * @param videoUrl
	 */
	public static void playUrl(String videoUrl, final String en,
			final Context contex, final String userid) {
		String type = "word";
		final String path2;

		if (ValidateUtils.hasSD()) {
			String path = ValidateUtils.getSDPath().getAbsolutePath();

			if (videoUrl != null && videoUrl.length() > 3
					&& videoUrl.substring(0, 4).equals("http")) {

				String videoUrl2 = "http/res/" + en + ".mp3";
				// 如果参数是网络地址则保存在本地的路径为http/res
				path = path + "/emodou/4/" + videoUrl2;
				path2 = path;
			} else if (videoUrl != null && videoUrl.length() > 3
					&& videoUrl.substring(0, 3).equals("/st")) {
				path2 = videoUrl;
				path = videoUrl;
			} else {
				path = path + videoUrl;
				path2 = videoUrl;
			}

			// 如果不存在本地文件则访问网络请求并下载更改数据库
			if (!ValidateUtils.isExist(path)) {

				HttpUtils http = new HttpUtils();
				HttpHandler handler = http.download(videoUrl, path, true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
						true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
						new RequestCallBack<File>() {

							@Override
							public void onStart() {
							}

							@Override
							public void onLoading(long total, long current,
									boolean isUploading) {

							}

							@Override
							public void onSuccess(
									ResponseInfo<File> responseInfo) {

								try {
									MediaPlayer mediaPlayer = new MediaPlayer();
									mediaPlayer.reset();
									mediaPlayer.setDataSource(path2);
									mediaPlayer.prepare();// prepare之后自动播放
									mediaPlayer.start();

									DbUtils db = DbUtils.create(contex);
									try {

										EmodouWordInfo myword = db
												.findFirst(Selector
														.from(EmodouWordInfo.class)
														.where("wordname", "=",
																en));
										if (myword != null) {
											myword.setLocal_voice(path2);
											db.update(myword);
										}

									} catch (DbException e) {
										e.printStackTrace();
									}

								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalStateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

							@Override
							public void onFailure(HttpException error,
									String msg) {
								error.printStackTrace();
							}
						});

			} else {
				try {
					MediaPlayer mediaPlayer = new MediaPlayer();
					mediaPlayer.reset();
					mediaPlayer.setDataSource(path);
					mediaPlayer.prepare();// prepare之后自动播放
					mediaPlayer.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			Toast.makeText(contex, "未找到sd卡", 0).show();

		}

	}

	
	
	
	public static List<EmodouWordForWlist> getWordManagerListBySql(Context context, DbUtils db, String sql) {
		Cursor cur = null;
		List<EmodouWordForWlist> wordManagerlist = new ArrayList<EmodouWordForWlist>();
		String voice = "";
	    String local_voice = "";
	    String wordmean = "";
		try {
			cur = db.execQuery(sql);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DbUtils dbUtils = DbUtils.create(context);

		if (cur != null) {
			for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
				
				int id = cur.getInt(cur.getColumnIndex("id"));
				String userid = cur.getString(cur.getColumnIndex("userid"));
				String wordname = cur.getString(cur.getColumnIndex("wordname"));
				String classid = cur.getString(cur.getColumnIndex("classid"));
				String isAddToNewWordsBood =  cur.getString(cur.getColumnIndex("isAddToNewWordsBood"));
				int learnState =  cur.getInt(cur.getColumnIndex("learnState"));
				int reviewState =  cur.getInt(cur.getColumnIndex("reviewState"));
				long lastStateTime =  cur.getInt(cur.getColumnIndex("lastStateTime"));
				int lastState =  cur.getInt(cur.getColumnIndex("lastState"));
				
				
				EmodouWordInfo wordInfo;
				try {
					wordInfo = dbUtils.findFirst(Selector.from(EmodouWordInfo.class)
															 .where("word_name", "=", wordname)
															 .and("classid","=",classid));
					voice = wordInfo.getVoice();
					local_voice =  wordInfo.getLocal_voice();
					wordmean = wordInfo.getMeaning();
					
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				EmodouWordForWlist wordWlist = new EmodouWordForWlist();
				wordWlist.setId(id);
				wordWlist.setUserid(userid);
				wordWlist.setWordname(wordname);
				wordWlist.setWordmean(wordmean);
				wordWlist.setClassid(classid);
				wordWlist.setIsAddToNewWordsBood(isAddToNewWordsBood);
				wordWlist.setLearnState(learnState);
				wordWlist.setReviewState(reviewState);
				wordWlist.setLastStateTime(lastStateTime);
				wordWlist.setLastState(lastState);
				wordWlist.setVoice(voice);
				wordWlist.setLocal_voice(local_voice);
				wordWlist.setFlipState(true);//正面在上为true，反面为false
				
				wordManagerlist.add(wordWlist);
			}
		}

		return wordManagerlist;

	}
	
	public static List<EmodouWordForWlist> getWordInfoListBySql(Context context, DbUtils db, String sql) {
		Cursor cur = null;
		List<EmodouWordForWlist> wordManagerlist = new ArrayList<EmodouWordForWlist>();
		String userid = "";
	    String isAddToNewWordsBood = "";
	    int  learnState = 0;
	    int  reviewState = 0;
	    int lastState =0;
	    long lastStateTime = 0;
		try {
			cur = db.execQuery(sql);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DbUtils dbUtils = DbUtils.create(context);

		if (cur != null) {
			for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
				
				int id = cur.getInt(cur.getColumnIndex("id"));
				//String userid = cur.getString(cur.getColumnIndex("userid"));
				String wordname = cur.getString(cur.getColumnIndex("word_name"));
				String classid = cur.getString(cur.getColumnIndex("classid"));
				String voice = cur.getString(cur.getColumnIndex("voice"));
				String local_voice = cur.getString(cur.getColumnIndex("local_voice"));
				String wordmean = cur.getString(cur.getColumnIndex("meaning"));
				//String isAddToNewWordsBood =  cur.getString(cur.getColumnIndex("isAddToNewWordsBood"));
				//int learnState =  cur.getInt(cur.getColumnIndex("learnState"));
				//int reviewState =  cur.getInt(cur.getColumnIndex("reviewState"));
				//long lastStateTime =  cur.getInt(cur.getColumnIndex("lastStateTime"));
				//int lastState =  cur.getInt(cur.getColumnIndex("lastState"));
				
				
				EmodouWordManager wordManager;
				try {
					wordManager = dbUtils.findFirst(Selector.from(EmodouWordManager.class)
															 .where("wordname", "=", wordname)
															 .and("classid","=",classid));
					userid = wordManager.getUserid();
					isAddToNewWordsBood =  wordManager.getIsAddToNewWordsBood();
					learnState = wordManager.getLearnState();
					reviewState = wordManager.getReviewState();
					lastState = wordManager.getLastState();
					lastStateTime = wordManager.getLastStateTime();
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				EmodouWordForWlist wordWlist = new EmodouWordForWlist();
				wordWlist.setId(id);
				wordWlist.setUserid(userid);
				wordWlist.setWordname(wordname);
				wordWlist.setWordmean(wordmean);
				wordWlist.setClassid(classid);
				wordWlist.setIsAddToNewWordsBood(isAddToNewWordsBood);
				wordWlist.setLearnState(learnState);
				wordWlist.setReviewState(reviewState);
				wordWlist.setLastStateTime(lastStateTime);
				wordWlist.setLastState(lastState);
				wordWlist.setVoice(voice);
				wordWlist.setLocal_voice(local_voice);
				wordWlist.setFlipState(true);//正面在上为true，反面为false
				
				wordManagerlist.add(wordWlist);
			}
		}

		return wordManagerlist;

	}
	
	//1表示添加到生词本，0表示不添加到生词本
	public static void addToNewWord(Context context, String wordname, String classid){
		DbUtils dbUtils = DbUtils.create(context);
		try {
			EmodouWordManager wordManagerInfo = dbUtils.findFirst(Selector.from(EmodouWordManager.class)
																          .where("wordname","=",wordname)
																          .and("classid","=",classid));
			if(wordManagerInfo != null){
				wordManagerInfo.setIsAddToNewWordsBood("1");
				dbUtils.update(wordManagerInfo);
			}
			
			
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void deleteFromNewWord(Context context, String wordname, String classid) {
		DbUtils dbUtils = DbUtils.create(context);
		try {
			EmodouWordManager wordManagerInfo = dbUtils.findFirst(Selector.from(EmodouWordManager.class)
																          .where("wordname","=",wordname)
																          .and("classid","=",classid));
			if(wordManagerInfo != null){
				wordManagerInfo.setIsAddToNewWordsBood("0");
				dbUtils.update(wordManagerInfo);
			}
			
			
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static String transTimeFormat(long time) {
		
		String timeCount = "";
		long hour = time / 3600000;
//		String hour = "0" + hourc;
//		hour = hour.substring(hour.length() - 2, hour.length());

		long minue = (time - hour * 3600000) / (60000);
//		String minue = "0" + minuec;
//		minue = minue.substring(minue.length() - 2, minue.length());

		long sec = (time - hour * 3600000 - minue * 60000) / 1000;
//		String sec = "0" + secc;
//		sec = sec.substring(sec.length() - 2, sec.length());
		if(time < 3600000 && time >= 60000){
			timeCount = minue + "分" + sec + "秒";
		}else if(time >= 3600000){
			timeCount = hour +"时" + minue + "分" + sec + "秒";
		}else if(time < 60000){
			timeCount = sec + "秒";
		}
		return timeCount;
	}
	
	//设置课程已学完
	public static void setClassLearned(Context context, String classid, String userid){
		DbUtils dbUtils = DbUtils.create(context);
		try {
			EmodouClassManager classManager = dbUtils.findFirst(Selector.from(EmodouClassManager.class)
					                                                    .where("classid","=",classid)
					                                                    .and("userid","=",userid));
			if(classManager != null){
				classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENED);
				dbUtils.update(classManager);
			}
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//设置课程正在学习中
	public static void setClassLearning(Context context, String classid, String userid){
		DbUtils dbUtils = DbUtils.create(context);
		try {
			EmodouClassManager classManager = dbUtils.findFirst(Selector.from(EmodouClassManager.class)
					                                                    .where("classid","=",classid)
					                                                    .and("userid","=",userid));
			if(classManager != null){
				classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENING);
				dbUtils.update(classManager);
			}
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	//将字符串写入到文本文件中
    public static void WriteTxtFile(String strcontent, String txtName)
    {
      //每次写入时，都换行写
      String strContent=strcontent+"\n";
      try {
           File file = new File("/sdcard/emodou/" + txtName + ".txt");
           if (!file.exists()) {
            LogUtil.d("TestFile", "Create the file:" + "/sdcard/emodou/" + txtName + ".txt");
            file.createNewFile();
           }
           RandomAccessFile raf = new RandomAccessFile(file, "rw");
           raf.seek(file.length());
           raf.write(strContent.getBytes());
           raf.close();
      } catch (Exception e) {
           LogUtil.e("TestFile", "Error on write File.");
          }
    }
    
    //将Long类型转换成Date类型
    public static String transferLongToDate(String dateFormat, Long millSec){
    	SimpleDateFormat dateFormat2 = new SimpleDateFormat(dateFormat);
    	Date date = new Date(millSec);
    		return dateFormat2.format(date);
    }
    
    /** 
     * 计算popupWindow的高度 
     *  
     * @param listView 
     */  
    public static int setListViewHeightBasedOnChildren(ListView listView) {  
        // 获取ListView对应的Adapter  
    	int totalHeight = 0;
    	ListAdapter listAdapter = listView.getAdapter();  
        if (listAdapter == null) {  
            totalHeight = 0; 
        }  
  
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0); // 计算子项View 的宽高  
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度  
        }  
        
        // listView.getDividerHeight()获取子项间分隔符占用的高度 
        totalHeight += (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
        return totalHeight;
    }  
}
