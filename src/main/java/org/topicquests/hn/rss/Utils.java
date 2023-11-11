/**
 * 
 */
package org.topicquests.hn.rss;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author jackpark
 *
 */
public class Utils {


	/**
	 * Given a {@code String} e.g. "[foo, bar]" convert that to a {@code List}
	 * @param listString
	 * @return
	 */
	public static List<String> createList(String listString) {
		String x = null;
		if (listString.startsWith("["))
			x = listString.trim().substring(1);
		//System.out.println("mmm "+x.endsWith("]"));
		if (x.endsWith("]"))
			x = x.substring(0, (x.length()-1));
		//System.out.println("BUM "+x);
		List<String>result = new ArrayList<String>();
		String [] ids = x.split(",");
		int len = ids.length;
		for (int i=0;i<len;i++) {
			// System.out.println(ids[i]);
			result.add(ids[i]);
		}
		return result;
	}
	
	/**
	 * Parse {@code jsonString} and return a {@code JsonObject}
	 * @param jsonString
	 * @return
	 */
	public static JsonObject toJSON(String jsonString) {
		return JsonParser.parseString(jsonString).getAsJsonObject();
	}
}
