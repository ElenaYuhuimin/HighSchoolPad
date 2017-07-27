package com.emodou.extend;

import com.emodou.home.SpeakActivity;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


public class MyAudioTrack {
	
	int mFrequency;					// ������
	
	int mChannel;					// ���
	
	int mSampBit;					// �����
	
	AudioTrack mAudioTrack;			
	
	public MyAudioTrack(int frequency, int channel, int sampbit){
		mFrequency = frequency;
		mChannel = channel;
		mSampBit = sampbit;
	}
	
	public void init(){
		if (mAudioTrack != null){
			release();
		}
		
		
		// ��ù����������С�������С
		int minBufSize = AudioTrack.getMinBufferSize(mFrequency, 
													mChannel,
													mSampBit);
		
		
		
//		         STREAM_ALARM��������
//		         STREAM_MUSCI������������music��
//		         STREAM_RING������
//		         STREAM_SYSTEM��ϵͳ����
//		         STREAM_VOCIE_CALL���绰����
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				mFrequency, 
				mChannel, 
				mSampBit, 
				minBufSize,
				AudioTrack.MODE_STREAM);
//				AudioTrack����MODE_STATIC��MODE_STREAM���ַ��ࡣ
//      		STREAM����˼�����û���Ӧ�ó���ͨ��write��ʽ�����һ��һ�ε�д��audiotrack�С�
//				�����������socket�з������һ��Ӧ�ò��ĳ���ط���ȡ��ݣ�����ͨ������õ�PCM��ݣ�Ȼ��write��audiotrack��
//				���ַ�ʽ�Ļ�������������JAVA���Native�㽻����Ч����ʧ�ϴ�
//				��STATIC����˼��һ��ʼ������ʱ�򣬾Ͱ���Ƶ��ݷŵ�һ���̶���buffer��Ȼ��ֱ�Ӵ���audiotrack��
//				����Ͳ���һ�δε�write�ˡ�AudioTrack���Լ��������buffer�е���ݡ�
//				���ַ�������������ڴ�ռ�ý�С����ʱҪ��ϸߵ�������˵�����á�
		
		
		
		mAudioTrack.play();	
	}
	
	
	public void release(){
		if (mAudioTrack != null){
			mAudioTrack.stop();				       	
			mAudioTrack.release();
		}
	}
	
	public void playAudioTrack(byte []data, int offset, int length){
		if (data == null || data.length == 0){
			return ;
		}
		
		try {
			mAudioTrack.write(data, offset, length);
			System.out.println("HeadPosition:"+mAudioTrack.getPlaybackHeadPosition());
			
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("MyAudioTrack", "catch exception...");
		}
	}
	
	public int getPrimePlaySize(){
		int minBufSize = AudioTrack.getMinBufferSize(mFrequency, 
				mChannel,
				mSampBit);
		
		return minBufSize * 2;
	}
}