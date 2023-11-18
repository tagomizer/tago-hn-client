/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.rss.test;

import org.topicquests.hn.client.api.ICommonModel;
import org.topicquests.hn.client.api.IThreadListener;
import org.topicquests.hn.client.nlp.NodeStudyThread;
import org.topicquests.hn.client.search.RssHnModel;
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
