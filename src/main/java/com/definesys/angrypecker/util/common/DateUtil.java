/**
 * Copyright (c) 2015-2016, Chill Zhuang 庄骞 (smallchill@163.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.definesys.angrypecker.util.common;



import com.definesys.angrypecker.properties.DragonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {


	/**
	 * 获取YYYY格式
	 *
	 * @return
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}


    /**
     * 获取当前时间
     */
    public static String getCurrentDate(){
        return new SimpleDateFormat(getDay()).format(new Date());
    }

    /**
     * 获取昨天时间
     */
   /* public static String getYesstodateDate(){
             return new Date().getTime().;
    }*/

    /**
     * 获取过去三天时间
     */

	/**
	 * 获取YYYY格式
	 *
	 * @return
	 */
	public static String getYear(Date date) {
		return formatDate(date, "yyyy");
	}

	/**
	 * 获取YYYY-MM-DD格式
	 *
	 * @return
	 */
	public static String getDay() {
		return formatDate(new Date(), "yyyy-MM-dd");
	}

	/**
	 * 获取YYYY-MM-DD格式
	 *
	 * @return
	 */
	public static String getDay(Date date) {
		return formatDate(date, "yyyy-MM-dd");
	}

	/**
	 * 获取YYYYMMDD格式
	 *
	 * @return
	 */
	public static String getDays() {
		return formatDate(new Date(), "yyyyMMdd");
	}

	/**
	 * 获取YYYYMMDD格式
	 *
	 * @return
	 */
	public static String getDays(Date date) {
		return formatDate(date, "yyyyMMdd");
	}

	/**
	 * 获取YYYY-MM-DD HH:mm:ss格式
	 *
	 * @return
	 */
	public static String getTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 获取YYYY-MM-DD HH:mm:ss.SSS格式
	 *
	 * @return
	 */
	public static String getMsTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
	}

	/**
	 * 获取YYYYMMDDHHmmss格式
	 *
	 * @return
	 */
	public static String getAllTime() {
		return formatDate(new Date(), "yyyyMMddHHmmss");
	}

	/**
	 * 获取YYYY-MM-DD HH:mm:ss格式
	 *
	 * @return
	 */
	public static String getTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static String formatDate(Date date, String pattern) {
		String formatDate = null;
		if (StringUtils.isNotBlank(pattern)) {
			formatDate = DateFormatUtils.format(date, pattern);
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}

	/**
	 * @Title: compareDate
	 * @Description:(日期比较，如果s>=e 返回true 否则返回false)
	 * @param s
	 * @param e
	 * @return boolean
	 * @throws
	 * @author luguosui
	 */
	public static boolean compareDate(String s, String e) {
		if (parseDate(s) == null || parseDate(e) == null) {
			return false;
		}
		return parseDate(s).getTime() >= parseDate(e).getTime();
	}

	/**
	 * 格式化日期
	 *
	 * @return
	 */
	public static Date parseDate(String date) {
		return parse(date,"yyyy-MM-dd");
	}

	/**
	 * 格式化日期
	 *
	 * @return
	 */
	public static Date parseTime(String date) {
		return parse(date,"yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 格式化日期
	 *
	 * @return
	 */
	public static Date parse(String date, String pattern) {
		try {
			return DateUtils.parseDate(date, new String[]{pattern});
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 格式化日期
	 *
	 * @return
	 */
	public static String format(Date date, String pattern) {
		return DateFormatUtils.format(date, pattern);
	}

	/**
	 * 把日期转换为Timestamp
	 *
	 * @param date
	 * @return
	 */
	public static Timestamp format(Date date) {
		return new Timestamp(date.getTime());
	}

	/**
	 * 校验日期是否合法
	 *
	 * @return
	 */
	public static boolean isValidDate(String s) {
		return parse(s, "yyyy-MM-dd HH:mm:ss") != null;
	}

	/**
	 * 校验日期是否合法
	 *
	 * @return
	 */
	public static boolean isValidDate(String s, String pattern) {
        return parse(s, pattern) != null;
	}

	public static int getDiffYear(String startTime, String endTime) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			int years = (int) (((fmt.parse(endTime).getTime() - fmt.parse(
					startTime).getTime()) / (1000 * 60 * 60 * 24)) / 365);
			return years;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return 0;
		}
	}

	/**
	 * <li>功能描述：时间相减得到天数
	 *
	 * @param beginDateStr
	 * @param endDateStr
	 * @return long
	 * @author Administrator
	 */
	public static long getDaySub(String beginDateStr, String endDateStr) {
		long day = 0;
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd");
		Date beginDate = null;
		Date endDate = null;

		try {
			beginDate = format.parse(beginDateStr);
			endDate = format.parse(endDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
		// System.out.println("相隔的天数="+day);

		return day;
	}

	/**
	 * 得到n天之后的日期
	 *
	 * @param days
	 * @return
	 */
	public static String getAfterDayDate(String days) {
		return handleDayDate(days,null,"yyyy-MM-dd");
	}

    /**
     * 得到n天之前的日期
     *
     * @param days
     * @return
     */
    public static String getBeforeDayDate(String days) {
        return handleDayDate(days,"before","yyyy-MM-dd");
    }
    public static String handleDayDate(String days,String op,String pattern) {
        int daysInt = Integer.parseInt(days);

        Calendar canlendar = Calendar.getInstance(); // java.util包

        if ("before".equals(op)){
            canlendar.add(Calendar.DATE, -daysInt); // 日期减 如果不够减会将月变动
        }else {
            canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        }
        Date date = canlendar.getTime();

        SimpleDateFormat sdfd = new SimpleDateFormat(pattern);
        String dateStr = sdfd.format(date);

        return dateStr;
    }

    /**
     * 根据需要获得时候范围返回这个时间值
     * @param timeSelect 需要获得的时候范围
     * @return
     */
    public static Map<String,Date> returnDateRange(String timeSelect){

        Date startDate = null;//起始时间
        Date endDate = null;//终止时间

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 999);

        if("today".equals(timeSelect)){
//            startDate= calendar.getTime();
//            endDate= calendar1.getTime();
        }
        if("yesterday".equals(timeSelect)){
            calendar.add(calendar.DATE,-1);
//            startDate = calendar.getTime();
//            endDate= calendar1.getTime();
        }
        if("lastThreeDay".equals(timeSelect)){
            calendar.add(calendar.DATE,-2);
//            startDate = calendar.getTime();
//            endDate= calendar1.getTime();
        }
        if("week".equals(timeSelect)){
            int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
            if (1 == dayWeek) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            }
            // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            // 获得当前日期是一个星期的第几天
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
            calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day);
//            startDate = calendar.getTime();
//            endDate= calendar1.getTime();
        }
        if("lastWeek".equals(timeSelect)){
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int offset1 = 1 - dayOfWeek;
            int offset2 = 7 - dayOfWeek;
            calendar.add(Calendar.DATE, offset1 - 7);
            calendar1.add(Calendar.DATE, offset2 - 7);
//            startDate=calendar.getTime();
//            endDate = calendar1.getTime();
        }
        if("month".equals(timeSelect)){
            calendar.add(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH,1);

        }
        if("lastMonth".equals(timeSelect)){//获取上个月的时间
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.DAY_OF_MONTH,1);
            calendar1.add(Calendar.MONTH,0);
            calendar1.set(Calendar.DAY_OF_MONTH,0);
        }
        if("nextMonth".equals(timeSelect)){
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH,1);
            calendar1.add(Calendar.MONTH,2);
            calendar1.set(Calendar.DAY_OF_MONTH,0);
        }

        startDate=calendar.getTime();
        endDate = calendar1.getTime();

        Map<String,Date> map = new HashMap();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        return map;
    }

	/**
	 * 得到n天之后是周几
	 *
	 * @param days
	 * @return
	 */
	public static String getAfterDayWeek(String days) {
		int daysInt = Integer.parseInt(days);

		Calendar canlendar = Calendar.getInstance(); // java.util包
		canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
		Date date = canlendar.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("E");
		String dateStr = sdf.format(date);

		return dateStr;
	}

	/**
	 * 格式化Oracle Date
	 * @param
	 * @return
	 */
//	public static String buildDateValue(Object value){
//		if(Func.isOracle()){
//			return "to_date('"+ value +"','yyyy-mm-dd HH24:MI:SS')";
//		}else{
//			return Func.toStr(value);
//		}
//	}

	/**
	 * 根据附加条件，获取该时间范围
	 * @param range 条件字符串
	 * @return	返回一个包含开始时间和结束时间的map
	 */
	public static Map<String,Date> getTimeRange(String range){
		Map<String,Date> resultMap = new HashMap<String,Date>();
		Date startDate = null;
		Date endDate = null;
		Calendar startOfToday = Calendar.getInstance();
		Calendar endOfToday = Calendar.getInstance();
		startOfToday.set(Calendar.HOUR_OF_DAY, 0);
		startOfToday.set(Calendar.MINUTE, 0);
		startOfToday.set(Calendar.SECOND, 0);
		startOfToday.set(Calendar.MILLISECOND, 0);

		endOfToday.set(Calendar.HOUR_OF_DAY, 23);
		endOfToday.set(Calendar.MINUTE, 59);
		endOfToday.set(Calendar.SECOND, 59);
		endOfToday.set(Calendar.MILLISECOND, 999);

		if(DragonConstants.TASK_TIME_TODAY.equals(range)){

		}else if(DragonConstants.TASK_TIME_YESTERDAY.equals(range)){
			endOfToday.add(Calendar.DATE,-1);
			startOfToday.add(Calendar.DATE,-1);
		}else if(DragonConstants.TASK_TIME_LASTTHREEDAY.equals(range)){
			startOfToday.add(Calendar.DATE,-2);
		}else if(DragonConstants.TASK_TIME_WEEK.equals(range)){
			startOfToday.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
			//不能设置周日
			endOfToday.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
			endOfToday.add(Calendar.DATE,1);
		}else if(DragonConstants.TASK_TIME_LASTWEEK.equals(range)){
			endOfToday.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
			startOfToday.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
			startOfToday.add(Calendar.DATE,-6);
		}else if(DragonConstants.TASK_TIME_MONTH.equals(range)){
			startOfToday.set(Calendar.DAY_OF_MONTH,1);
			endOfToday.set(Calendar.DAY_OF_MONTH,1);
			endOfToday.add(Calendar.MONTH,1);
			endOfToday.add(Calendar.DAY_OF_MONTH,-1);
		}
		startDate = startOfToday.getTime();
		endDate = endOfToday.getTime();
		resultMap.put("startDate",startDate);
		resultMap.put("endDate",endDate);
		return resultMap;
	}

    public static List<String> getCurrDateByDays(int days,DateFormat dateFormat){
        return getCurrDateByDays(days,dateFormat,"before");
    }


    /**
     *获取当前时间的前每一天
     * @param days 具体想获取多少天
     * @param dateFormat 转换格式
     * @param dataOperation  before获取之前,after或者null获取之后
     * @return List集合,包含每一天
     */
    public static List<String> getCurrDateByDays(int days,DateFormat dateFormat,String dataOperation){
        Calendar now = Calendar.getInstance();
        if ("before".equals(dataOperation)){
            now.add(Calendar.DAY_OF_MONTH, -days);
        }else {
            now.add(Calendar.DAY_OF_MONTH, +days);
        }

        List dateList = new ArrayList(days+5);
        for (int i = 0; i < days ; i++) {
            String endDate =dateFormat.format(now.getTime());
            dateList.add(endDate);
            now.add(Calendar.DATE,1);
        }
        dateList.add(dateFormat.format(now.getTime()));
        return dateList;
    }

	public static void main(String[] args) throws ParseException {
		/*System.out.println(getTime(new Date()));
		System.out.println(getAfterDayWeek("3"));
        System.out.println(getCurrentDate());
        System.out.println(getDay(new Date()));
        System.out.println(getDay(DateUtils.addDays(new Date(),-1)));
        System.out.println(getBeforeDayDate("14"));*/

		String timeSelect = "nextMonth";
		String pattern = "yyyy-MM-dd";
        Map<String, Date> date = returnDateRange(timeSelect);
        Date startDate = date.get("startDate");
        Date endDate = date.get("endDate");
        System.out.println(DateFormatUtils.format(startDate,pattern)+"至"+DateFormatUtils.format(endDate,pattern));
    }

}
