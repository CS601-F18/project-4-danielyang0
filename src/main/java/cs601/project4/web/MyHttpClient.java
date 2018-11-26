package cs601.project4.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class MyHttpClient {
	public static Map<String, String> fetchGet(String urlString) throws IOException {
		Map<String, String> map = new HashMap<>();
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		int status = con.getResponseCode();
		map.put("status", status+"");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder content = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine).append("\n");
		}
		in.close();
//		logger.info(status+"");
//		logger.info(content.toString());
		map.put("content", content.toString());
		return map;
	}
	
	public static Map<String,String> fetchPost(String urlString, Map<String, String> postParams) throws IOException {
		
		Map<String, String> map = new HashMap<>();
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		//con.setRequestProperty("Content-Type", "application/json");
		
		con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(postParams));
        out.flush();
        out.close();
		
		int status = con.getResponseCode();
		map.put("status", status+"");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder content = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine).append("\n");
		}
		in.close();
		map.put("content", content.toString());
		return map;
	}
}

class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params) 
      throws UnsupportedEncodingException{
        StringBuffer sb = new StringBuffer();
 
        for (Map.Entry<String, String> entry : params.entrySet()) {
          sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          sb.append("=");
          sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
          sb.append("&");
        }
 
        String resultString = sb.toString();
        return resultString.length() > 0
          ? resultString.substring(0, resultString.length() - 1)
          : resultString;
    }
}
