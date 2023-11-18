/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.rss.test;

import org.topicquests.hn.client.HnClientEnvironment;
import org.topicquests.hn.client.HnAgent;
import org.topicquests.hn.client.HnHttpClient;
import org.topicquests.hn.client.api.IBacksideDatabase;

/**
 * @author jackpark
 *
 */
public class TestingRoot {
	protected HnClientEnvironment environment;
	protected HnHttpClient hnClient;
	protected HnAgent hnAgent;
	protected IBacksideDatabase database;
//	protected IClient client;

	/**
	 * 
	 */
	public TestingRoot() {
		environment = new HnClientEnvironment();
		hnClient = environment.getHnClient();
		hnAgent = environment.getHnAgent();
		database = environment.getDatabase();
//		client = environment.getClient();
	}

}
