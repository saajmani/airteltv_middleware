package com.accedo.wynkstudio.util;

/**
 * @author      Accedo Software Private Limited 
 * @version     1.0                 
 * @since       2014-07-01   
 * */
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringUtil {

	public static String listToString(List<String> stringList) {
		String stringObject = stringList.toString();
		return stringObject.substring(1, stringObject.length() - 1);
	}

//	public static String combineString(String stringOne, String stringTwo) {
//
//		return stringOne + FSCConstants.TILT + stringTwo;
//	}

	/**
	 * Returns true if data is not empty and not null
	 * 
	 * @param data
	 * @return boolean
	 */
	public static boolean notEmpty(String data) {
		return (data != null && !data.isEmpty());
	}

	public static String listToQueryString(List<String> stringList) {
		String stringObject = stringList.toString();
		stringObject = stringObject.substring(1, stringObject.length() - 1);
		stringObject = stringObject.replace(",", "','");
		stringObject = "'" + stringObject + "'";
		return (stringObject);
	}

	public static String listToString1(List<String> stringList) {
		String stringObject = stringList.toString();
		return stringObject.substring(1, stringObject.length() - 1);
	}

	public static String[] listToStringArray(List<String[]> stringList) {
		String[] userTokenArray = new String[stringList.size()];
		int i = 0;
		for (String[] strings : stringList) {
			userTokenArray[i++] = strings[1];
		}

		return userTokenArray;
	}

	public static void printJSON(Object obj) {
		String JSONString = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			JSONString = mapper.writeValueAsString(obj);
			System.out.println("JSON String = " + JSONString);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}

	}

	public static String exponentialToDecimalFormatter(Double d) {
		NumberFormat formatter = new DecimalFormat(
				"##########################.#####");
		String f = formatter.format(d);
		return f;
	}

}
