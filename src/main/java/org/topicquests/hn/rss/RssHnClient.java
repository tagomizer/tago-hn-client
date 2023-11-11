/**
 * 
 */
package org.topicquests.hn.rss;

import org.topicquests.hn.rss.util.SimpleHttpClient;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import com.google.gson.JsonObject;

import java.util.*;
import java.util.concurrent.TimeUnit;
import org.tinylog.Logger;

/**
 * @author jackpark
 *
 */
public class RssHnClient {
	private RssEnvironment environment;
	private SimpleHttpClient client;
	private final String
		UPDATES			= "https://hacker-news.firebaseio.com/v0/updates.json",
		ITEM_1			= "https://hacker-news.firebaseio.com/v0/item/",
		ITEM_2			= ".json",
		NEWS			= "https://hacker-news.firebaseio.com/v0/newstories.json",
		MAX_ITEM		= "https://hacker-news.firebaseio.com/v0/maxitem.json";
	/**
	 * 
	 */
	public RssHnClient(RssEnvironment env) {
		environment = env;
		client = new SimpleHttpClient();
	}
	
	public IResult getMaxItemId() {
		IResult result = new ResultPojo();
		IResult r = client.get(MAX_ITEM, "");
		if (r.hasError())
			result.addErrorString("RC-2 "+r.getErrorString());
		String x = (String)r.getResultObject();
		if (x != null) {
			//string is the long number
			result.setResultObject(x.trim());
			
		}
		return result;
	}
	/**
	 * <p>Return a {@link List} of story IDs, which we then use to fetch the entire story for each</p>
	 * <p>The process is this:<br/>
	 * <ul><li>Fetch story IDs</li>
	 * <li>Fetch each story by its Id</li>
	 * <li>later: later: update database</li></ul></p>
	 * @return
	 */
	public IResult getNewStories() {
		IResult result = new ResultPojo();
		IResult r = client.get(NEWS, "");
		if (r.hasError())
			result.addErrorString("RC-3 "+r.getErrorString());
		String x = (String)r.getResultObject();
		//System.out.println("BKA "+x);
		if (x != null) {
			List<String> y = Utils.createList(x);
			//System.out.println("BAR "+y);
			result.setResultObject(y);
		}
		return result;
	}
	
	public IResult getUpdates() {
		IResult result = new ResultPojo();
		IResult r = client.get(UPDATES, "");
		return result;
	}

	/**
	 * Returns anything (Story or Comment) with that {@code id}
	 * @param id
	 * @return a {@code JsonObject}
	 */
	public IResult getItem(String id) {
		System.out.println("GetItem "+id);
		try { //delay so we don't hit their server so hard
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (Exception e) {
			Logger.error("WaitError: "+e.getMessage(), e);
		}
		IResult result = new ResultPojo();
		IResult r;
		int count = 0;
		String x = null;
		while (count++ < 4) { //try up to 3 times
			r = client.get(ITEM_1+id+ITEM_2, "");
			if (r.hasError())
				result.addErrorString("RC-1 "+r.getErrorString());
			x = (String)r.getResultObject();
			Logger.debug("aaa "+id+"/n"+x);
			//{"by":"behnamoh","descendants":0,"id":36118597,"score":1,"text":"I&#x27;m confused about the plethora of AI models Google has produced. It seems like if you want to test the waters, they offer Bard, if you want to use the API, they offer PaLM API (and now PaLM 2), and finally, they have a Gemini model in training which will supposedly compete with GPT-5. They also had a LaMDA model which drove Bard for a while and made Google look like an idiot, Meena (an LLM introduced in 2020), Minerva (2022), and several other non-LLM AI models produced over the years.<p>- Bard<p>- Meena<p>- Minerva<p>- PaLM<p>- PaLM 2<p>- Gemini<p>- LaMDA<p>- ...<p>I&#x27;m afraid Google is repeating the mistake they had with messenger apps.","time":1685397441,"title":"Ask HN: Is Google repeating its mistakes with messenger apps, now with AI?","type":"story"}
			if (x != null && !x.equals(""))
				break;
		}		
		if (x != null && !x.equals("")) { //TODO this test failed one time - passed null to Utils
			//Logger.error("CGI-2 "+id+" | "+x);
			JsonObject jo = Utils.toJSON(x);
			result.setResultObject(jo);
		}
		return result;
	}

	
}