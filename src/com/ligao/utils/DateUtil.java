package com.ligao.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.net.ParseException;

/**
 * 日期处理工具类
 * @author Leo.L 
 * 2016年11月14日 11:20:06
 *
 */
public class DateUtil {

			public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		

		  public static void main(String[] args) throws Exception {  
		        Date d1=sdf.parse("2016-11-14 11:14:32");  
		        Date d2=sdf.parse("2016-11-15 11:10:33");  
		        System.out.println(daysBetween(d1,d2));  
		        System.out.println(daysBetween("2012-09-08 10:10:10","2012-09-15 00:00:00"));  
		    }  
		
		    /**  
		     * 计算两个日期之间相差的天数  
		     * @param smdate 较小的时间 
		     * @param bdate  较大的时间 
		     * @return 相差天数 
		     * @throws ParseException  
		     */    
		    public static int daysBetween(Date smdate,Date bdate) throws Exception    
		    {    
		        sdf=new SimpleDateFormat("yyyy-MM-dd");  
		        smdate=sdf.parse(sdf.format(smdate));  
		        bdate=sdf.parse(sdf.format(bdate));  
		        Calendar cal = Calendar.getInstance();    
		        cal.setTime(smdate);    
		        long time1 = cal.getTimeInMillis();                 
		        cal.setTime(bdate);    
		        long time2 = cal.getTimeInMillis();         
		        long between_days=(time2-time1)/(1000*3600*24);  
		            
		       return Integer.parseInt(String.valueOf(between_days));           
		    }    
		      
		/** 
		*字符串的日期格式的计算 
		*/  
		    public static int daysBetween(String smdate,String bdate) throws Exception{  
		        sdf=new SimpleDateFormat("yyyy-MM-dd");  
		        Calendar cal = Calendar.getInstance();    
		        cal.setTime(sdf.parse(smdate));    
		        long time1 = cal.getTimeInMillis();                 
		        cal.setTime(sdf.parse(bdate));    
		        long time2 = cal.getTimeInMillis();         
		        long between_days=(time2-time1)/(1000*3600*24);  
		            
		       return Integer.parseInt(String.valueOf(between_days));     
		    }  


}
