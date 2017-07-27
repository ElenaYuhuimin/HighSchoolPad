package com.emodou.util;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouClass;
import com.emodou.domain.EmodouClassManager;
import com.emodou.home.ListenActivity;
import com.emodou.home.ReadActivity;
import com.emodou.home.SpeakActivity;
import com.emodou.myclass.WorkDetailActivity;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class ValidateUtils2 {
	
////	public void loadFile(final String type, final String classid, final String bookid, String bookname,
//			ProgressDialog progressDialog, Context context) {
//		//必须选择的是同一本书，因为目前API没有返回足够的信息，如课程resurl，其他本书无法去下载
//		//先判断当前作业的这本书和系统正在学习的书是不是一本
//		//弹出对话等待框
//		progressDialog = new ProgressDialog(this);
//		progressDialog.setIndeterminate(true);
//		progressDialog.setMessage("Loading 0%...");
//		progressDialog.show();
//		
//		DbUtils dbUtils = DbUtils.create(this);
//		EmodouBook book = new EmodouBook();
//		try {
//			book = dbUtils.findFirst(Selector.from(EmodouBook.class)
//					                         .where("state","=",Constants.BOOK_STATE_SELECT)
//					                         .and("userid","=",userid));
//		} catch (DbException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		if(book != null){
//			if(!book.getBookid().equals(bookid)){
//				Toast.makeText(this, getResources().getString(R.string.myclass_workdetail_bookNotSelect)+bookname, 0).show();
//				progressDialog.dismiss();
//			}else{
//				//当前正在学习的书和作业的书是同一本
//				//type要是2，3,4这种
//				Intent itent = null;
//				EmodouClassManager classManager = new EmodouClassManager();
//				try {
//					classManager = dbUtils.findFirst(Selector.from(EmodouClassManager.class)
//							                                 .where("userid","=",userid)
//							                                 .and("bookid","=",bookid)
//							                                 .and("classid","=",classid));
//				} catch (DbException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				String articleUrl = Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_JSON1;
//				//首先如果能找到相应的资源则直接进行跳转
//				if (ValidateUtils.isExist(articleUrl)) {
//
//					if (Constants.EMODOU_TYPE_LISTEN.equals(type)) {
//						itent = new Intent(WorkDetailActivity.this, ListenActivity.class);
//					} else if (Constants.EMODOU_TYPE_READ.equals(type)) {
//						itent = new Intent(this, ReadActivity.class);
//					} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
//						itent = new Intent(this, SpeakActivity.class);
//					}
//					Bundle mBundle = new Bundle();
//					mBundle.putString("type", type);
//					mBundle.putString("classid", classid);
//					mBundle.putString("bookid", bookid);
//
//					itent.putExtras(mBundle);
//					startActivity(itent);
//					
//					progressDialog.dismiss();
//				} 
//				//如果找不到本地res资源，则找压缩包看是否存在
//				else if (classManager != null && classManager.getDownloadstate() == Constants.EMODOU_CLASS_DOWNLOAD_STATE_ALREADY_DOWNLOAD) {
//					//如果该课是已下载的状态，则解压完后还是已下载的状态，不需要改变
//					
//					//为了避免解压不完全的情况，首先盘算是否有解压出来的资源文件 文件夹如果有 删掉
//					if (ValidateUtils.isExist(Constants.LOCAL_START + type + "/"+ classid + Constants.LOCAL_RES)) {
//						ValidateUtils.deleteDirectory(Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_RES);
//					}
//
//					//拼串解压相应文件 ， 解压完之后 进行相应的跳转
//					Log.d("url", "step1");
//					File zipFile = new File(Constants.LOCAL_START + type + "/"+ classid + "/" + classid + Constants.LOCAL_ZIP);
//					try {
//						String unzipLocation = Constants.LOCAL_START + type + "/" + classid;
//						ValidateUtils.unzipFiles3(zipFile, unzipLocation);
//
//						if (Constants.EMODOU_TYPE_LISTEN.equals(type)) {
//							itent = new Intent(this, ListenActivity.class);
//						} else if (Constants.EMODOU_TYPE_READ.equals(type)) {
//							itent = new Intent(this, ReadActivity.class);
//						} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
//							itent = new Intent(this, SpeakActivity.class);
//						}
//
//						progressDialog.dismiss();
//						Bundle mBundle = new Bundle();
//						mBundle.putString("type", type);
//						mBundle.putString("classid", classid);
//						mBundle.putString("bookid", bookid);
//						itent.putExtras(mBundle);
//						startActivity(itent);
//
//
//					} catch (Exception e) {
//						Toast.makeText(this, e.toString(), 0).show();
//						progressDialog.dismiss();
//						e.printStackTrace();
//					}
//
//				}
//				
//				//如果本地压缩包也不存在，则从网络上进行下载
//				else{
//					EmodouClass classentity = new EmodouClass();
//					try {
//						classentity = dbUtils.findFirst(Selector.from(EmodouClass.class)
//								                                .where("classid","=",classid));
//					} catch (DbException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//						progressDialog.dismiss();
//					}
//					
//					String classurl = Constants.EMODOU_URL + "/"+ classentity.getResurl();
//					LogUtil.d("classurl", classurl);
//					HttpUtils http = new HttpUtils();
//					//找运行时变量map里面是否有相应的handler如果没有就新建一个
//					if (myhandlerMap.get(classid) == null) {
//						HttpHandler httpHandler = http.download(classurl,
//								Constants.LOCAL_START + type + "/" + classid + "/"+ classid + ".zip", true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将重新下载。
//								true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
//								new RequestCallBack<File>() {
//
//									@Override
//									public void onStart() {
//									}
//
//									@Override
//									//下载的过程中实时更新数据库中的进度
//									public void onLoading(long total, long current,
//											boolean isUploading) {
//
//
//										DbUtils dbUtils = DbUtils.create(WorkDetailActivity.this);
//										try {
//											EmodouClassManager classManager = dbUtils.findFirst(Selector
//															.from(EmodouClassManager.class)
//															.where("bookid", "=",bookid)
//															.and("classid", "=",classid)
//															.and("type", "=", type)
//															.and("userid", "=", userid));
//											if(classManager != null && dbUtils != null){
//												classManager.setProgress((float) ((int) (100 * current / total)) / 100);
//												dbUtils.update(classManager);
//											}
//											progressDialog.setMessage("Loading  "+  ((int) (100 * current / total)) +"%...");
//										} catch (Exception e) {
//											e.printStackTrace();
//										}
//
//									}
//
//									@Override
//									public void onCancelled() {
//										Log.d("progress_workdetail", classid + "cancel");
//										super.onCancelled();
//									}
//
//									@Override
//									//下载成功后 首先将本条状态在数据库中更新为已下载，其次如果还有等待中的任务则开启一个新的任务，并且在
//									//运行时变量map中将本条记录的handler移除5
//									public void onSuccess(
//											ResponseInfo<File> responseInfo) {
//										DbUtils dbUtils = DbUtils.create(WorkDetailActivity.this);
//										try {
//											EmodouClassManager classManager = dbUtils.findFirst(Selector
//																			.from(EmodouClassManager.class)
//																			.where("bookid", "=",bookid)
//																			.and("classid", "=",classid)
//																			.and("type", "=", type)
//																			.and("userid", "=", userid));
//											
//											if(classManager != null && dbUtils != null){
//												classManager.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_ALREADY_DOWNLOAD);
//												dbUtils.update(classManager);
//											}
//											//下载之后进行解压
//											//为了避免解压不完全的情况，首先盘算是否有解压出来的资源文件 文件夹如果有 删掉
//											if (ValidateUtils.isExist(Constants.LOCAL_START + type + "/"+ classid + Constants.LOCAL_RES)) {
//												ValidateUtils.deleteDirectory(Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_RES);
//											}
//
//											//拼串解压相应文件 ， 解压完之后 进行相应的跳转
//											Log.d("url", "step1");
//											File zipFile = new File(Constants.LOCAL_START + type + "/"+ classid + "/" + classid + Constants.LOCAL_ZIP);
//											try {
//												String unzipLocation = Constants.LOCAL_START + type + "/" + classid;
//												ValidateUtils.unzipFiles3(zipFile, unzipLocation);
//												Intent intent = null;
//
//												if (Constants.EMODOU_TYPE_LISTEN.equals(type)) {
//													intent = new Intent(WorkDetailActivity.this, ListenActivity.class);
//												} else if (Constants.EMODOU_TYPE_READ.equals(type)) {
//													intent = new Intent(WorkDetailActivity.this, ReadActivity.class);
//												} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
//													intent = new Intent(WorkDetailActivity.this, SpeakActivity.class);
//												}
//
//												progressDialog.dismiss();
//												Bundle mBundle = new Bundle();
//												mBundle.putString("type", type);
//												mBundle.putString("classid", classid);
//												mBundle.putString("bookid", bookid);
//												intent.putExtras(mBundle);
//												startActivity(intent);
//
//
//											} catch (Exception e) {
//												Toast.makeText(WorkDetailActivity.this, e.toString(), 0).show();
//												progressDialog.dismiss();
//												e.printStackTrace();
//											}
//
//											if(myhandlerMap != null){
//												myhandlerMap.remove(classid);
//											}
//											
//
//										} catch (Exception e) {
//											e.printStackTrace();
//										}
//									}
//
//									@Override
//									public void onFailure(HttpException error,String msg) {
//										error.printStackTrace();
//
//									}
//
//									
//								});
//						
//						//将handler放进map
//
//						myhandlerMap.put(classid, httpHandler);
//
//					}
//				}
//				
//			}
//		}	
//	}
}
