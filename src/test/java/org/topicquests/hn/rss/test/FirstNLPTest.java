/**
 * 
 */
package org.topicquests.hn.rss.test;

import org.topicquests.hn.rss.api.ICommonModel;
import org.topicquests.hn.rss.api.IThreadListener;
import org.topicquests.hn.rss.nlp.NodeStudyThread;
import org.topicquests.hn.rss.search.RssHnModel;
import org.topicquests.support.api.IResult;

import com.google.gson.JsonObject;

/**
 * 
 */
public class FirstNLPTest extends TestingRoot implements IThreadListener {
	private final String ITEM_ID = "2"; //TODO
	private NodeStudyThread nlp;
	/**
	 * 
	 */
	public FirstNLPTest() {
		nlp = environment.getStudyThread();
		nlp.setListener(this);
		IResult r = database.getObject(ITEM_ID);
		System.out.println("A "+r.getErrorString());
		JsonObject hit = (JsonObject)r.getResultObject();
		System.out.println("B "+hit.toString());
		ICommonModel m = new RssHnModel(hit);
		nlp.addItem(m);
	}
	@Override
	public void threadEmpty() {
		System.out.println("DID");
		environment.shutDown();
		System.exit(0);
	}

}
