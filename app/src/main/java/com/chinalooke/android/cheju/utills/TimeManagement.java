package com.chinalooke.android.cheju.utills;

import java.text.ParseException;

/**
 * ʱ��ת��������
 * @author max.chengdu 2015��11��29��
 *
 */
public class TimeManagement {
	/**
	 * ���� yyyy-MM-dd
	 * 
	 * @param String ����ΪString����yyyy-MM-dd HH:mm:ss
	 * @return ���� yyyy-MM-dd
	 * @throws ParseException 
	 */
	public static String exchangeStringDate(String date) throws ParseException {
		if (date != null && date.length() > 10) {
			String result = date.substring(0, 10);
			return result;
		}else{
			return null;
		}

	}

	/**
	 * ����HH:mm:ss
	 * 
	 * @param String ����ΪString����
	 * @return ����HH:mm:ss
	 * @throws ParseException 
	 */
	public static String exchangeStringTime(String date) throws ParseException {
		if (date != null && date.length() > 10) {
			String result = date.substring(10, date.length());
			return result;
		}else{
			return null;
		}
	}


}
