package com.friendly.walking;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.friendly.walking.util.JWLog;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ApplicationPool extends Application {
	private final static boolean	TRACE				= false;
	private final static String		TRACE_TAG			= "ApplicationPool";
	private final static long		INVALID_EXTRA_ID	= -999;
	private final static String 	KEY_ID_SEPRATOR 	= ";";
	private ArrayList<Pool> 		mPoolList;	

	private final static int		TYPE_NONE			= -1;
	public final static int			TYPE_PUBLIC			= 0;

	private static ApplicationPool	mSelf;
	private static Activity			mCurrentActivity;

	private static class KakaoSDKAdapter extends KakaoAdapter {
		/**
		 * Session Config에 대해서는 default값들이 존재한다.
		 * 필요한 상황에서만 override해서 사용하면 됨.
		 * @return Session의 설정값.
		 */
		@Override
		public ISessionConfig getSessionConfig() {
			return new ISessionConfig() {
				@Override
				public AuthType[] getAuthTypes() {
					return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
				}

				@Override
				public boolean isUsingWebviewTimer() {
					return false;
				}

				@Override
				public ApprovalType getApprovalType() {
					return ApprovalType.INDIVIDUAL;
				}

				@Override
				public boolean isSaveFormData() {
					return true;
				}

				@Override
				public boolean isSecureMode() {
					return false;
				}
			};
		}

		@Override
		public IApplicationConfig getApplicationConfig() {
			return new IApplicationConfig() {
				@Override
				public Context getApplicationContext() {
					return ApplicationPool.getGlobalApplicationContext();
				}
			};
		}
	}

	public static ApplicationPool getGlobalApplicationContext() {
		return mSelf;
	}

	public static void setCurrentActivity(Activity activity) {
		mCurrentActivity = activity;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mSelf = this;
		createPoolList();

		KakaoSDK.init(new KakaoSDKAdapter());
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		mSelf = null;
	}
	
	/**
	 * 
	 */
	private void createPoolList(){
		if(null == mPoolList){
			mPoolList = new ArrayList<Pool>();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean isEmpty(){
		return (null == mPoolList || mPoolList.isEmpty());
	}
	

	/**
	 * 저장된 데이터를 꺼내줍니다.
	 * 한 번 꺼낸 데이터는 삭제됩니다.
	 * @param key
	 * @param extraId
	 * @return
	 */
	synchronized public Object getExtra(final String key, final long extraId){
		return getExtra(TYPE_PUBLIC, key, extraId);  
	}
		
	
	/**
	 * 저장된 데이터를 꺼내줍니다.
	 * 한 번 꺼낸 데이터는 삭제됩니다.
	 * @param type
	 * @param key
	 * @param extraId
	 * @return
	 */
	synchronized public Object getExtra(final int type, final String key, final long extraId){
		if(isEmpty()){
			return null;
		}
		for(Pool p : mPoolList){
			if(null != p && p.getId() == type){
				return p.getData(key, extraId);
			}
		}
		return null;  
	}
	
	/**
	 * 저장된 데이터를 꺼내줍니다.
	 * @param type
	 * @param key
	 * @param extraId
	 * @return
	 */
	synchronized public Object referExtra(final int type, final String key, final long extraId){
		if(isEmpty()){
			return null;
		}
		for(Pool p : mPoolList){
			if(null != p && p.getId() == type){
				return p.referData(key, extraId);
			}
		}
		return null;  
	}
	
	/**
	 * intent 저장되어 있는 id를 통해 데이터를 꺼내줍니다.
	 * @param key
	 * @param i
	 * @return
	 */
	synchronized public Object getExtra(final String key, final Intent i){
		return getExtra(TYPE_PUBLIC, key, i);
	}
	

	/**
	 * intent 저장되어 있는 id를 통해 데이터를 꺼내줍니다.
	 * @param key
	 * @param i
	 * @return
	 */
	synchronized public Object referExtra(final String key, final Intent i){
		return referExtra(TYPE_PUBLIC, key, i);
	}
	
	/**
	 * intent 저장되어 있는 id를 통해 데이터를 꺼내줍니다.
	 * @param key
	 * @param i
	 * @return
	 */
	synchronized public Object referExtra(final int type, final String key, final Intent i){
		if(null == i || TextUtils.isEmpty(key)){
			return null;
		}
		long extraId = i.getLongExtra(key, INVALID_EXTRA_ID);
		if(INVALID_EXTRA_ID == extraId){
			return null;
		}
		return referExtra(type, key, extraId);
	}
	
	/**
	 * intent 저장되어 있는 id를 통해 데이터를 꺼내줍니다.
	 * @param key
	 * @param i
	 * @return
	 */
	synchronized public Object getExtra(final int type, final String key, final Intent i){
		if(null == i || TextUtils.isEmpty(key)){
			return null;
		}
		long extraId = i.getLongExtra(key, INVALID_EXTRA_ID);
		if(INVALID_EXTRA_ID == extraId){
			return null;
		}
		return getExtra(type, key, extraId);
	}
	
	/**
	 * 데이터를 저장하고 intent에 데이터의 id를 설정합니다.
	 * @param key
	 * @param i
	 * @param obj
	 */
	synchronized public void putExtra(final String key, Intent i, final Object obj){
		if(null == i){
			return;
		}
		long extraId = putExtra(TYPE_PUBLIC, key, obj);
		i.putExtra(key, extraId);
	}
	
	/**
	 * 데이터를 저장합니다.
	 * @param key
	 * @param obj
	 * @return
	 */
	synchronized public long putExtra(final int type, final String key, final Object obj){
		createPoolList();
		for(Pool p : mPoolList){
			if(null != p && p.getId() == type){
				return p.putData(key, obj);
			}
		}
		Pool p = new Pool(type);
		mPoolList.add(p);
		return p.putData(key, obj); 
	}
	
	/**
	 * 데이터를 저장합니다.
	 * @param key
	 * @param obj
	 * @return
	 */
	synchronized public void putExtra(final int type, Intent i, final String key, final Object obj){
		if(null == i){
			return;
		}
		createPoolList();
		for(Pool p : mPoolList){
			if(null != p && p.getId() == type){
				i.putExtra(key, p.putData(key, obj));
				return;
			}
		}
		Pool p = new Pool(type);
		mPoolList.add(p);
		i.putExtra(key, p.putData(key, obj));
	}
	
	/**
	 * 
	 * @param type
	 */
	synchronized public void removeExtras(final int type){
		for(Pool p : mPoolList){
			if(null != p && p.getId() == type){
				p.destory();
				mPoolList.remove(p);
				return;
			}
		}
	}
	
	/**
	 * 
	 */
	synchronized public void removePoolList(){
		if(isEmpty()){
			return;
		}
		for(Pool p : mPoolList){
			if(null != p){
				p.destory();
			}
		}
		mPoolList = null;
	}
		
	/**
	 * 
	 * @author hjlee
	 *
	 */
	private class Pool {
		private Map<String, Object> 	mMap = null;
		private int						mId  = TYPE_NONE;
		
		public Pool(int id) {
			mId = id;
			mMap = new HashMap<String, Object>();
		}
		
		public void destory(){
			if(null != mMap){
				Set<String> keys = mMap.keySet();
				if(null != keys && !keys.isEmpty()){
					for(String key : keys){
						mMap.remove(key);
					}
				}
			}
			mId = TYPE_NONE;
		}
		
		public int getId(){
			return mId;
		}
		
		/**
		 * 
		 */
		public void printDatas(){
			try{
				Set<String> keys = mMap.keySet();
				if(null == keys|| keys.isEmpty()){
					if(TRACE){
						JWLog.e(TRACE_TAG, "[printDatas] storage is empty.");
					}
				} else {
					for(String key : keys){
						Class cls = mMap.get(key).getClass();
						if(TRACE){
							JWLog.e(TRACE_TAG, "[printDatas] " + key + " : " + cls.getName());
						}
					}
				}	
			} catch (Exception e){
				if(TRACE){
					JWLog.e(TRACE_TAG, "[printDatas] fail. exception occurred - " + e.getMessage());
				}
			} catch (Error e){
				if(TRACE){
					JWLog.e(TRACE_TAG, "[printDatas] fail. error occurred - " + e.getMessage());
				}
			}
		}
		
		
		/**
		 * 저장된 데이터를 꺼내줍니다.
		 * 한 번 꺼낸 데이터는 삭제됩니다.
		 * @param key
		 * @param extraId
		 * @return
		 */
		synchronized public Object getData(final String key, final long extraId){
			String dataKey = makeDataKey(extraId, key);
			Object obj = mMap.get(dataKey);
			if(TRACE){
				JWLog.e(TRACE_TAG, "[getData] key : " + dataKey);
				if(null == obj){
					JWLog.e(TRACE_TAG, "[getData] obj : " + null);
				} else {
					JWLog.e(TRACE_TAG, "[getData] obj : " + obj.getClass().getName());
				}		
				printDatas();
			}
			
			mMap.remove(dataKey);
			return obj;  
		}
		
		/**
		 * 저장된 데이터를 꺼내줍니다.
		 * @param key
		 * @param extraId
		 * @return
		 */
		synchronized public Object referData(final String key, final long extraId){
			String dataKey = makeDataKey(extraId, key);
			Object obj = mMap.get(dataKey);
			if(TRACE){
				JWLog.e(TRACE_TAG, "[referData] key : " + dataKey);
				if(null == obj){
					JWLog.e(TRACE_TAG, "[referData] obj : " + null);
				} else {
					JWLog.e(TRACE_TAG, "[referData] obj : " + obj.getClass().getName());
				}		
				printDatas();
			}
			
			return obj;  
		}
		
		/*
		*//**
		 * intent 저장되어 있는 id를 통해 데이터를 꺼내줍니다.
		 * @param key
		 * @param i
		 * @return
		 *//*
		synchronized public Object getData(final String key, final Intent i){
			if(null == i || TextUtils.isEmpty(key)){
				return null;
			}
			long extraId = i.getLongExtra(key, INVALID_EXTRA_ID);
			if(INVALID_EXTRA_ID == extraId){
				return null;
			}
			return getExtra(key, extraId);
		}
		
		*//**
		 * 데이터를 저장하고 intent에 데이터의 id를 설정합니다.
		 * @param key
		 * @param i
		 * @param data
		 *//*
		synchronized public void putData(final String key, Intent i, final Object data){
			if(null == i){
				return;
			}
			long extraId = putData(key, data);
			i.putExtra(key, extraId);
		}*/
		
		/**
		 * 데이터를 저장합니다.
		 * @param key
		 * @param data
		 * @return
		 */
		synchronized public long putData(final String key, final Object data){
			long extraId = System.nanoTime();
			
			String dataKey = null;
			int increaseForAvoidDuplicate = 0;
			do{
				extraId += increaseForAvoidDuplicate;
				dataKey = makeDataKey(extraId, key);
				increaseForAvoidDuplicate++;
			}while(mMap.containsKey(dataKey));
			
			mMap.put(dataKey, data);
			
			if(TRACE){
				JWLog.e(TRACE_TAG, "[putData] key : " + dataKey);
				if(null == data){
					JWLog.e(TRACE_TAG, "[putData] obj : " + null);
				} else {
					JWLog.e(TRACE_TAG, "[putData] obj : " + data.getClass().getName());
				}
				printDatas();
			}
			return extraId;
		}
		
		/**
		 * 
		 * @param extraId
		 * @param key
		 * @return
		 */
		private String makeDataKey(final long extraId, final String key){
			return extraId + KEY_ID_SEPRATOR + key;
		}
	}

}
