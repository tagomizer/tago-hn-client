/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client.nlp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.topicquests.hn.client.HnClientEnvironment;
import org.topicquests.hn.client.api.IBacksideDatabase;
import org.topicquests.hn.client.api.ICommonModel;
import org.topicquests.hn.client.api.IThreadListener;
import org.topicquests.hn.client.spacy.SpacyHttpClient;
import org.topicquests.support.api.IResult;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.tinylog.Logger;

/**
 * @author jackpark
 *
 */
public class NodeStudyThread {
	private HnClientEnvironment environment;
	private IBacksideDatabase database;

	private SpacyHttpClient client;
	private List<ICommonModel> items;
	private boolean isRunning = true;
	private StudyThread engine;
	private IThreadListener listener = null;
	/**
	 * 
	 */
	public NodeStudyThread(HnClientEnvironment env) {
		environment = env;
		database = environment.getDatabase();
		client = new SpacyHttpClient(environment);
		items = new ArrayList<ICommonModel>();
		engine = new StudyThread();
		engine.start();
	}
	
	public void setListener(IThreadListener l) {
		listener = l;
	}
	public void shutDown() {
		synchronized(items) {
			isRunning = false;
			items.notify();
		}
	}
	
	/**
	 * Add an {@code item} for processing. An item is one of Story or comment
	 * @param item
	 */
	public void addItem(ICommonModel item) {
		synchronized(items) {
			items.add(item);
			items.notify();
		}
	}
	
	void processItem(ICommonModel item) {
		Logger.debug("PI\n"+item.getData());
		Long id =  Long.valueOf(item.getId());
		String title = item.getTitle();
		String text = item.getText();
		long authorId = Long.valueOf(item.getCreatorId());
		IResult r;
		JsonObject data;
		JsonElement je;
		JsonArray ja;
		Set<String> guts = new HashSet<String>();
		Iterator<JsonElement>itr;
		if (title != null) {
			// run NLP on title
			r = client.processSentence(title);
			data = (JsonObject)r.getResultObject();
			je = data.get("data");
			ja = je.getAsJsonArray();
			itr = ja.iterator();
			// gather named entities
			while (itr.hasNext())
				guts.add(itr.next().getAsString());
		}
		if (text != null) {
			// run nlp on body text
			r = client.processSentence(text);
			data = (JsonObject)r.getResultObject();
			je = data.get("data");
			ja = je.getAsJsonArray();
			itr = ja.iterator();
			//gather named entities
			while (itr.hasNext())
				guts.add(itr.next().getAsString());
		}
		Logger.info(title+" | "+text+"\n"+guts);
		System.out.println("NLP: "+guts);
		List<String>tags = cleanTags(guts);
		//persist tag
		if (!tags.isEmpty()) {
			Iterator<String>itx = tags.iterator();
			while (itx.hasNext())
				r = database.addTag(id, authorId, itx.next());
		}
	}
	
	List<String> cleanTags(Set<String> guts) {
		List<String>result = new ArrayList<String>();
		Iterator<String>itr = guts.iterator();
		String tag;
		while (itr.hasNext()) {
			tag = itr.next();
			result.add(cleanTag(tag));
		}
		return result;
	}
	
	/**
	 * Remove junk from tag
	 * @param tag
	 * @return
	 */
	String cleanTag(String tag) {
		String result = tag;
		//TODO
		return result;
	}
	class StudyThread extends Thread {
		public void run() {
			ICommonModel obj = null;
			while (isRunning) {
				synchronized(items) {
					if (items.isEmpty()) {
						if (listener != null)
							listener.threadEmpty();
						try {
							items.wait();
						} catch (Exception e) {}
					} else
						obj = items.remove(0);
				}
				if (isRunning && obj == null) {
					processItem(obj);
					obj = null;
				}
			}
		}
	}

}
