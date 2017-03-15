package com.wang.httpandudputils.httpUtil;

import android.text.TextUtils;

import com.wang.httpandudputils.interfaces.OnRequestListener;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * http相关请求
 * Created by wayr on 2016/8/1.
 */
public class HttpRequest {

    public static final int GET = 0;
    public static final int POST = 1;

    /**
     * 异步get访问网络
     * @param url
     * @param listener
     */
    public static void getRequest( String url,  OnRequestListener listener){
        requestAsyn(url,null,null,GET,listener);
    }

    /**
     * 异步get访问网络
     * @param url
     * @param headerMap 请求头数据
     * @param listener
     */
    public static void getRequest(String url, HashMap<String,String> headerMap, OnRequestListener listener){
        requestAsyn(url,headerMap,null,GET,listener);
    }

    /**
     * 异步post访问网络
     * @param url
     * @param param
     * @param listener
     */
    public static void postRequest(final String url, final String param, final OnRequestListener listener){
        requestAsyn(url,null,param,POST,listener);
    }
    /**
     * 异步post访问网络
     * @param url
     * @param headerMap 请求头数据
     * @param param
     * @param listener
     */
    public static void postRequest(String url, HashMap<String,String> headerMap, String param, OnRequestListener listener) {
        requestAsyn(url, headerMap, param, 1, listener);
    }

    /**
     * 同步get访问网络
     * @param url
     * @return
     */
    public static String getSync(String url){
        return requestSync(url,null,null,GET);
    }
    /**
     * 同步get访问网络
     * @param url
     * @return
     */
    public static String getSync(String url, HashMap<String,String> headerMap){
        return requestSync(url,headerMap,null,GET);
    }

    /**
     * 同步post访问网络
     * @param url
     * @param param
     * @return
     */
    public static String postSync(String url, String param){
        return requestSync(url,null,param,POST);
    }

    /**
     * 同步post访问网络
     * @param url
     * @param headerMap 请求头数据
     * @param param 参数
     * @return
     */
    public static String postSync(String url, HashMap<String,String> headerMap,String param){
        return requestSync(url,headerMap,param,POST);
    }
    private static void requestAsyn(final String url, final HashMap<String,String> headerMap, final String param, final int type, final OnRequestListener listener) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> arg0) {
                if(TextUtils.isEmpty(url)) {
                    arg0.onNext(null);
                } else {
                    String result = null;
                    if(type == 0) {
                        result = HttpUtil.doGet(url,headerMap);
                    } else if(type == 1) {
                        result = HttpUtil.doPost(url, headerMap, param);
                    }

                    if(result != null && !"".equals(result)) {
                        if(!result.startsWith("{") && !result.startsWith("[")) {
                            arg0.onNext(null);
                        } else {
                            arg0.onNext(result);
                        }
                    } else {
                        arg0.onNext(null);
                    }
                }

                arg0.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            public void onCompleted() {
            }
            public void onError(Throwable arg0) {
            }

            public void onNext(String result) {
                if(result == null) {
                    if(listener != null) {
                        listener.onFail();
                    }
                } else if(listener != null) {
                    listener.onOk(result);
                }

            }
        });
    }
    public static String requestSync(final String url,final HashMap<String,String> headerMap, final String param, final int type){
        if (TextUtils.isEmpty(url)) {
            return null;
        } else {
            String result = null;
            if(type == GET){
                result = HttpUtil.doGet(url,headerMap);
            }else if(type == POST){
                result = HttpUtil.doPost(url,headerMap,param);
            }
            if (result == null || "".equals(result)) {
                return null;
            } else {
                /** 判断是否为json 格式 **/
                if (result.startsWith("{") || result.startsWith("[")) {
                   return result;
                } else {
                    return null;
                }
            }
        }
    }

}
