/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client.nlp;

import org.topicquests.hn.client.HnClientEnvironment;
import org.topicquests.hn.client.api.IBacksideDatabase;
import org.topicquests.hn.client.api.ICommonModel;
import org.topicquests.hn.client.api.IThreadListener;
import org.topicquests.hn.client.search.RssHnModel;
import org.topicquests.support.api.IResult;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.tinylog.Logger;

/**
 * 
 */
public class DatabaseHarvester implements IThreadListener {
	private HnClientEnvironment environment;
	private IBacksideDatabase database;
	private NodeStudyThread studyThread;
	private final int COUNT = 100; //number of hits to fetch at a time
	private int cursor = 0; // not thread safe

	/**
	 * 
	 */
	public DatabaseHarvester(HnClientEnvironment env) {
		environment = env;
		studyThread = environment.getStudyThread();
		studyThread.setListener(this);
		database = environment.getDatabase();
	}

	/**
	 * Run a harvest session over the entire database.
	 */
	public void doHarvest() {
		cursor = 0;
		threadEmpty();
	}
	
	/**
	 * This, in theory, will run until no further hits are found
	 */
	@Override
	public void threadEmpty() {
		JsonArray hits = batchFetch(cursor);
		int hitCount = hits.size();
		cursor += hitCount;
		JsonObject jo;
		for (int i=0;i<hitCount;i++) {
			jo = hits.get(i).getAsJsonObject();
			studyThread.addItem(new RssHnModel(jo));
		}
	}

	JsonArray batchFetch(int offset) {
		IResult r = database.listEntities(COUNT, offset);
		JsonArray result = (JsonArray)r.getResultObject();
		
		return result;
	}
}

