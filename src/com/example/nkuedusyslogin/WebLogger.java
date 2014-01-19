package com.example.nkuedusyslogin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;


public class WebLogger {
	boolean logined=false;
	URL url;
	MainActivity myActivity;
	String progress_str;
	String toast_str;
	int progress_int;
	/*
	List<NameValuePair>infos
	=new ArrayList<NameValuePair>(Arrays.asList(
			new BasicNameValuePair("user", "1234567"),
			new BasicNameValuePair("password", "123456"),
			new BasicNameValuePair("valicode", "1234")
	));
	*/
	List<String>infos
	=new ArrayList<String>(Arrays.asList(
			"user", "1234567",
			"password", "123456",
			"valicode", "1234"
	));
	List<String>headers
	=new ArrayList<String>(Arrays.asList(
		    "Accept","text/html, application/xhtml+xml, */*",
		    "Accept-Language","en-US",
		    "User-Agent","Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)",
		    "Accept-Encoding"," gzip, deflate",
		    "Host","222.30.32.10",
		    "DNT","1",
		    "Connection","Keep-Alive"
		    ));
	List<String>headers2
	=new ArrayList<String>(Arrays.asList(
		    "Accept","text/html, application/xhtml+xml, */*",
		    "Referer","http://222.30.32.10/",
		    "Accept-Language","en-US",
		    "User-Agent","Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)",
		    "Content-Type","application/x-www-form-urlencoded",
		    "Accept-Encoding"," gzip, deflate",
		    "Host","222.30.32.10",
		    //"Content-Length","97",
		    "DNT","1",
		    "Connection","Keep-Alive",
		    "Cache-Control","no-cache"
		    ));

	Bitmap valicode_bitmap;
	String res_page;
	public int lenOfInfos(){
		int res=0;
		for(int i=1;i<6;i+=2){
			res+=infos.get(i).length();
		}
		return res;
	}
	public void setValue(List<String> headers,String key,String value){
		if(headers.indexOf(key)!=-1){
			headers.set(headers.indexOf(key)+1, value);
		}else{
			headers.add(key);
			headers.add(value);
		}		
	}
	public void deleteValue(List<String> headers,String key){
		if(headers.indexOf(key)!=-1){
			headers.remove(headers.indexOf(key)+1);
			headers.remove(headers.indexOf(key));
		}
	}
	public void setContentLength(int type){
		if(type==0){
			//setValue(headers2,"Content-Length",""+80+lenOfInfos());
		}else{
			//setValue(headers2,"Content-Length",""+851);
		}
	}
	public void setUser(String value){
		setValue(infos,"user",value);	
	}
	public void setPassword(String value){
		setValue(infos,"password",value);	
	}
	public void setValicode(String value){
		setValue(infos,"valicode",value);	
	}
	public void setHeaders(HttpGet get,List<String> headers){
		for(int i=0;i<headers.size();i+=2){
			Header header=new BasicHeader(headers.get(i),headers.get(i+1));
			get.setHeader(header);
		}
	}
	public void setHeaders(HttpPost post,List<String> headers){
		for(int i=0;i<headers.size();i+=2){
			Header header=new BasicHeader(headers.get(i),headers.get(i+1));
			post.setHeader(header);
		}
	}
	public boolean reportStatus(String status,int value){
		System.out.println(status);
		progress_str=status;
		progress_int=value;
		myActivity.handler.sendEmptyMessage(0x124);
		return true;
	}
	public boolean toastStatus(String status){
		System.out.println(status);
		toast_str=status;
		myActivity.handler.sendEmptyMessage(0x126);
		return true;
	}
	public boolean reportReady(){
		myActivity.handler.sendEmptyMessage(0x125);
		return true;
	}
	public void showValicode(){
		
	}
	public String readAll(InputStream str){
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(str,"gb2312"));
			String line=null;
			String res_str="";
			while((line=br.readLine())!=null){
				res_str+=line;
			}
			return res_str;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		

	}
	public boolean init(){
		reportStatus("正在初始化....", 10);
		try {
			//建立连接
			HttpClient client = new DefaultHttpClient();
			reportStatus("建立HTTP连接....", 30);
			HttpGet get=new HttpGet("http://222.30.32.10/ValidateCode");
			setHeaders(get,headers);
			HttpResponse response=client.execute(get);
			reportStatus("获取Cookie....", 30);
			try{
				String cookie=response.getLastHeader("Set-Cookie").getValue();
				headers2.add("Cookie");
				headers2.add(cookie);
				headers.add("Cookie");
				headers.add(cookie);
			}catch(Exception e){
				e.printStackTrace();
			}
			reportStatus("获取验证码....", 50);
			HttpEntity entity=response.getEntity();
			InputStream is=entity.getContent();
			valicode_bitmap=BitmapFactory.decodeStream(is);
			myActivity.valicodeBitmap=valicode_bitmap;
			myActivity.handler.sendEmptyMessage(0x123);
			reportReady();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	public boolean login(){
		if(logined)return true;
		//建立连接
		
		try {
			reportStatus("正在登录...", 0);
			HttpClient client = new DefaultHttpClient();
			HttpPost post=new HttpPost("http://222.30.32.10/stdloginAction.do");
			setContentLength(0);
			setHeaders(post,headers2);
			List<NameValuePair>loginPost
			=new ArrayList<NameValuePair>(Arrays.asList(
					new BasicNameValuePair("usercode_text",infos.get(1)),
					new BasicNameValuePair("userpwd_text",infos.get(3)),
					new BasicNameValuePair("checkcode_text",infos.get(5)),
					new BasicNameValuePair("operation",""),
					new BasicNameValuePair("submittype",new String("确 认".getBytes( "gbk" ),"ISO-8859-1"))
					));
			UrlEncodedFormEntity urlentity=new UrlEncodedFormEntity(loginPost,HTTP.ISO_8859_1);
			post.setEntity(urlentity);
			String pageContent=readAll(client.execute(post).getEntity().getContent());
			Pattern pattern=Pattern.compile("<LI>(.*)</LI>");
			Matcher matcher=pattern.matcher(pageContent);
			if(matcher.find()){
				//error message found
				reportReady();
				toastStatus(matcher.group(1));
				return false;
			}			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			reportReady();
			toastStatus("客户端协议错误!");
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			reportReady();
			toastStatus("输入输出错误!");
			return false;
		}		
		logined=true;
		return true;
	}
	public boolean getScore(){
		if (!login()){
			init();
			return false;
		}	
		res_page="";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get=new HttpGet("http://222.30.32.10/xsxk/scoreAlarmAction.do");		
			setHeaders(get,headers);
			reportStatus("获取GPA....", 20);
			String pageContent=readAll(client.execute(get).getEntity().getContent());	
			Pattern pattern=Pattern.compile("(<p align=\"center\">.*?</table>)");
			Matcher matcher=pattern.matcher(pageContent);
			String GPA_alarm="";
			while(matcher.find()){
				GPA_alarm+=matcher.group(0)+"\r\n";
			}

			get=new HttpGet("http://222.30.32.10/xsxk/studiedAction.do");		
			setHeaders(get,headers);
			pageContent=readAll(client.execute(get).getEntity().getContent());
			pattern=Pattern.compile("共 (.) 页");
			matcher=pattern.matcher(pageContent);
			reportStatus("获取页数...", 35);
			int pages_number=1;
			if(matcher.find()){
				pages_number=Integer.parseInt(matcher.group(1));
			}
			pattern=Pattern.compile("(\\[.*?类课.*?\\])");
			matcher=pattern.matcher(pageContent);
			String GPA_count="";
			while(matcher.find()){
				GPA_count+=matcher.group(0);
			}
			reportStatus("创建HTML...", 40);
			res_page="";
			res_page+="<html>"+GPA_alarm;  
			res_page+=GPA_count;
			res_page+="<table bgcolor=\"#CCCCCC\" border=\"0\" cellspacing=\"2\" cellpadding=\"3\" width=\"100%\">";
			res_page+="<tr bgcolor=\"#3366CC\"><td>序号</td><td>课程代码</td><td>课程名称</td><td>课程类型</td><td>成绩</td><td>学分</td><td>重修成绩</td><td>重修情况</td></tr>";
	        int page_index;
	        setValue(headers,"Referer","http://222.30.32.10/xsxk/studiedAction.do");
	        for (page_index=0;page_index<pages_number;page_index++){
	        	reportStatus("正在读取第"+page_index+"页", 30);
	        	pattern=Pattern.compile("(<tr bgcolor=\"#FFFFFF\">(( *\t\t.*?)+?) *\t</tr>)");
				matcher=pattern.matcher(pageContent);
				while(matcher.find()){
					res_page+=matcher.group(0);
				}
				get=new HttpGet("http://222.30.32.10/xsxk/studiedPageAction.do?page=next");		
		        setHeaders(get,headers);
				pageContent=readAll(client.execute(get).getEntity().getContent());
	        }
	        deleteValue(headers,"Referer");
	        reportStatus("好了哭去吧。。", 100);
	        res_page+="</table></html>";
	        reportReady();
      
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	private void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		
	}
	public boolean evaluateTeacher(){
		if (!login()){
			init();
			return false;
		}
		try {
			reportStatus("获取课程数....", 15);
			HttpClient client = new DefaultHttpClient();
			HttpGet get=new HttpGet("http://222.30.32.10/evaluate/stdevatea/queryCourseAction.do");		
			setHeaders(get,headers);
			String pageContent=readAll(client.execute(get).getEntity().getContent());
			Pattern pattern=Pattern.compile("<td class=\"NavText\"><a href=\"queryTargetAction.do\\?operation=target&amp;index=(.)\">");
			Matcher matcher=pattern.matcher(pageContent);
			int course_number=0;
			while(matcher.find()){
				course_number+=1;
			}
			reportStatus("你有"+course_number+"门课程,请耐心等待", 20);
			String uri_str="http://222.30.32.10/evaluate/stdevatea/queryTargetAction.do?operation=target&index=";
			
			get=new HttpGet(uri_str+0);		
			setHeaders(get,headers);
			pageContent=readAll(client.execute(get).getEntity().getContent());
			reportStatus("获取评价项...", 25);
			String post_value="";
			List<NameValuePair>evaluatePost
			=new ArrayList<NameValuePair>(Arrays.asList(
					new BasicNameValuePair("opinion","Good!"),
					new BasicNameValuePair("operation","Store")
					));

			pattern=Pattern.compile("<select name=\"(array\\[.*?\\])\" style=\"width:110px\"><option value=\"null\">&nbsp;</option>\t\t<option value=\"(.*?)\"");
			matcher=pattern.matcher(pageContent);
			while(matcher.find()){
				//"array[n]"="10"/"5"
				evaluatePost.add(new BasicNameValuePair(matcher.group(1),matcher.group(2)));
			}
			UrlEncodedFormEntity urlentity=new UrlEncodedFormEntity(evaluatePost,HTTP.ISO_8859_1);
			for (int idx=0;idx<course_number;idx++){
				reportStatus("正在评价第"+idx+"门课...",30);
				get=new HttpGet(uri_str+idx);		
				setHeaders(get,headers);
				HttpResponse response=client.execute(get);
				setValue(headers2,"Referer",uri_str+idx);
				HttpPost post=new HttpPost("http://222.30.32.10/evaluate/stdevatea/queryTargetAction.do");
				setHeaders(post,headers2);
				post.setEntity(urlentity);
				response=client.execute(post);

			}
			deleteValue(headers2,"Referer");
			reportReady();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}
