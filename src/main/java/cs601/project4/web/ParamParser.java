package cs601.project4.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cs601.project4.exception.ParamParseException;

public class ParamParser {
	public static Map<String, Object> parsePath(HttpServletRequest request, String requestURITarget) {
		Map<String, Object> pathVariables = new HashMap<String, Object>();
		String[] targetSplited = requestURITarget.split("/");
		String requestURI = request.getRequestURI();
		String[] splited = requestURI.split("/");
		if(targetSplited.length != splited.length) {
			throw new ParamParseException("path not match");
		}
		for (int i = 0; i < targetSplited.length; i++) {
			if( i != 0 && splited[i].length() == 0 ){
				throw new ParamParseException("path not match");
			}
			String target = targetSplited[i];
			if(target.startsWith("{") && target.endsWith("}")) {
				String[] nameAndType = target.substring(1, target.length()-1).split(":");
				if("int".equals(nameAndType[1].toLowerCase())) {
					Integer intVariable = null;
					try {
						intVariable = Integer.valueOf(splited[i]);
					} catch (NumberFormatException e) {
						throw new ParamParseException(nameAndType[0] + " should be an integer");
					}
					pathVariables.put(nameAndType[0], intVariable);
				}else{
					pathVariables.put(nameAndType[0], splited[i]);
				}
			}else{
				if(!target.equals(splited[i])) {
					throw new ParamParseException("not valid path");
				}
			}
		}
		return pathVariables;
	}

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
