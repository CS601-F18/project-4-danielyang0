package cs601.project4.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import cs601.project4.exception.ParamParseException;
import cs601.project4.web.bean.BeansForJson;

/**
 * a class for parse URL path variable and varibles in request json data
 * @author yangzun
 *
 */
public class ParamParser {
	/**
	 * mainly
	 * @param request
	 * @param requestURITarget
	 * @param pathVariables
	 * @return
	 */
	public static boolean parsePath(HttpServletRequest request, String requestURITarget, Map<String, Object> pathVariables) {
		pathVariables.clear();
		String[] targetSplited = requestURITarget.split("/");
		String requestURI = request.getRequestURI();
		String[] splited = requestURI.split("/");
		if(targetSplited.length != splited.length) {
			return false;
		}
		//compare every splited token
		for (int i = 0; i < targetSplited.length; i++) {
			//in requestURI, if "//" exisited
			if( i != 0 && splited[i].length() == 0 ){
				return false;
			}
			String target = targetSplited[i];
			if(target.startsWith("{") && target.endsWith("}")) {
				String[] nameAndType = target.substring(1, target.length()-1).split(":");
				if("int".equals(nameAndType[1].toLowerCase())) {
					Integer intVariable = null;
					try {
						intVariable = Integer.valueOf(splited[i]);
					} catch (NumberFormatException e) {
						return false;
					}
					pathVariables.put(nameAndType[0], intVariable);
				}else{
					pathVariables.put(nameAndType[0], splited[i]);
				}
			}else{
				if(!target.equals(splited[i])) {
					return false;
				}
			}
		}
		return true;
	}

	
	public static <T> T parseJsonToObject(HttpServletRequest request, Class<T> clazz) throws IOException {
		BufferedReader br = request.getReader();
		StringBuffer sb = new StringBuffer();
		String line;
		while((line = br.readLine()) != null) {
			sb.append(line);
		}
		Gson gson = new Gson();
		T fromJson = null;
		try {
			fromJson = gson.fromJson(sb.toString(), clazz);
		} catch (JsonSyntaxException e) {
		}
		return fromJson;
	}
	
	public static String readRequestBody(HttpServletRequest request) throws IOException {
		BufferedReader br = request.getReader();
		StringBuffer sb = new StringBuffer();
		String line;
		while((line = br.readLine()) != null) {
			sb.append(line+"\n");
		}
		return sb.toString();
	}
	
	
	public static <T> T parseJsonToObject(HttpServletRequest request, Type type) throws IOException {
		BufferedReader br = request.getReader();
		StringBuffer sb = new StringBuffer();
		String line;
		while((line = br.readLine()) != null) {
			sb.append(line);
		}
		Gson gson = new Gson();
		T fromJson = null;
		try {
			fromJson = gson.fromJson(sb.toString(), type);
		} catch (JsonSyntaxException e) {
			// TODO: handle exception
		}
		return fromJson;
	}
	
	@Deprecated
	public static int[] parseIntParams(HttpServletRequest request, String[] intParams){
		int[] intVals = new int[intParams.length];
		for (int i = 0; i< intParams.length; i++) {
			String key = intParams[i];
			String valueString = request.getParameter(key);
			if(valueString == null) {
				throw new ParamParseException(key + " should not be empty!");
			}
			int val = 0;
			try {
				val= Integer.valueOf(valueString);
			} catch (NumberFormatException e) {
				throw new ParamParseException(key + " is not an integer!");
			}
			intVals[i] = val;
		}
		return intVals;
	}
	
	@Deprecated
	public static String[] parseStringParams(HttpServletRequest request, String[] stringParams){
		String[] stringVals = new String[stringParams.length];
		for (int i = 0; i < stringParams.length; i++) {
			String key = stringParams[i];
			String valueString = request.getParameter(key);
			if(valueString == null) {
				throw new ParamParseException(key + " should not be empty!");
			}
			stringVals[i] = valueString;
		}
		return stringVals;
	}
	
}
