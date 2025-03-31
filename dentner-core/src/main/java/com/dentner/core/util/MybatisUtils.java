package com.dentner.core.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

public class MybatisUtils {
	public static boolean equals(String val1, String val2) {
		if(val1 == null || val2 == null) return false;
		return val1.equals(val2);
	}

	public static boolean isNotEquals(String val1, String val2) {
		return !equals(val1, val2);
	}

	public static boolean isEmpty(Object obj) {
		return !isNotEmpty(obj);
	}

	public static boolean isNotEmpty(Object obj) {
		if(obj == null || "null".equals(obj)){
			return false;
		}else{
			if( obj instanceof String ) 		return !StringUtils.isEmpty((String)obj);
			else if( obj instanceof Integer )	return true;
			else if( obj instanceof List ) 		return !((List)obj).isEmpty();
			else if( obj instanceof Map ) 		return !((Map)obj).isEmpty();
			else if( obj instanceof Object[] )  return Array.getLength(obj) > 0;
			else return true;
		}
	}

	public static boolean isContains(String s, String gubun) {
		if(s.contains(gubun)) {
			return true;
		} else {
			return false;
		}
	}
}
