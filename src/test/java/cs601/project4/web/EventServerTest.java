package cs601.project4.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class EventServerTest {
	@Test
	public void testCreate() {
		int userid=1;
		String eventname="happy";
		int numtickets = 20;
		Map<String, String> createPostData = new HashMap<>();
		createPostData.put("userid", ""+userid);
		createPostData.put("eventname", eventname);
		createPostData.put("numtickets", ""+numtickets);
		Map<String, String> eventCreateResult = null;
		try {
			eventCreateResult = MyHttpClient.fetchPost("http://localhost:8081/create", createPostData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String status = eventCreateResult.get("status");
		String content = eventCreateResult.get("content");
		if("200".equals(status)) {
			System.out.println(200);
			System.out.println(content);
		}else if("400".equals(status)) {
			System.out.println("400");
			System.out.println(content);
		}
	}
}
