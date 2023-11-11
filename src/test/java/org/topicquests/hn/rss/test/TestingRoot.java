/**
 * 
 */
package org.topicquests.hn.rss.test;

import org.topicquests.hn.rss.RssEnvironment;
import org.topicquests.hn.rss.RssHnAgent;
import org.topicquests.hn.rss.RssHnClient;
import org.topicquests.hn.rss.api.IBacksideDatabase;

/**
 * @author jackpark
 *
 */
public class TestingRoot {
	protected RssEnvironment environment;
	protected RssHnClient hnClient;
	protected RssHnAgent hnAgent;
	protected IBacksideDatabase database;
//	protected IClient client;

	/**
	 * 
	 */
	public TestingRoot() {
		environment = new RssEnvironment();
		hnClient = environment.getHnClient();
		hnAgent = environment.getHnAgent();
		database = environment.getDatabase();
//		client = environment.getClient();
	}

}
