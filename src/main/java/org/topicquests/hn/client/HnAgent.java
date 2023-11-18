/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.topicquests.hn.client.api.ExternalService;
import org.topicquests.hn.client.api.IBacksideDatabase;
import org.topicquests.hn.client.api.ICommonModel;
import org.topicquests.hn.client.api.IConstants;
import org.topicquests.hn.client.api.IDSL;
import org.topicquests.hn.client.api.PostType;
import org.topicquests.hn.client.search.RssHnModel;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.tinylog.Logger;

/**
 * @author jackpark
 *
 */
public class HnAgent {
	private HnClientEnvironment environment;
	private HnHttpClient hnClient;
	private IBacksideDatabase database;
	private IDSL dsl;
	//private boolean isRunning = true;
	private final long DELAY = 1000*60*30; //60; //1 hour
	private Timer timer= null;
	private int cycleCount = 0;
	/**
	 * 
	 */
	public HnAgent(HnClientEnvironment env) {
		environment = env;
		hnClient = environment.getHnClient();
		database = environment.getDatabase();
	}
	
	/**
	 * needed to sop the timer
	 */
	public void stop() {
		System.out.println("ChronStop");
		//try {
			timer.cancel();
		//} catch (Exception e)
	}

	public void setDSL(IDSL d) {
		dsl = d;
	}
	
	/**
	 * Full harvest until it reaches an item we already have, then it stops
	 */
	public void fullHarvest() {
		//TODO should we do this inside a single connection?
		String maxId = (String)hnClient.getMaxItemId().getResultObject();
		if (maxId != null) {
			IResult r;
			JsonObject jo;
			long itemId = Long.parseLong(maxId);
			while (itemId > -1l) {
				//TODO test for existence
				// IF exists, stop
				r = hnClient.getItem(Long.valueOf(itemId).toString());
				jo = (JsonObject)r.getResultObject();
			}
		}
	}
	/**
	 * Cycle like a "chronjob"
	 */
	public void chronJob() {
		System.out.println("ChronStarting");
		timer = new Timer();
		cycleCount =1;
		cycle(); //kickstart
		TimerTask task = new TimerTask() {
	        public void run() {
	            cycle();
	        };
	    };
	    timer.schedule(task,DELAY, DELAY);
	}
	
	/**
	 * A <em>cycle</em> consists of fetch and process data, and return the stories
	 * @return
	 */
	public IResult cycle() {
		IResult result = new ResultPojo();
		System.out.println("CycleStart "+ cycleCount); 
		// get a list of new storiy Ids
		IResult r = hnClient.getNewStories();
		if (r.hasError())
			result.addErrorString("RA-1 "+r.getErrorString());
		System.out.println("Foo "+r.getResultObject());
		// gp fetch them
		List<String> ja = (List<String>)r.getResultObject();
		if (ja != null && !ja.isEmpty())
			getStories(ja, result);
		Logger.debug("GETTINGGUPDATE");
		r = hnClient.getUpdates();
		if (r.hasError())
			result.addErrorString("RA-2 "+r.getErrorString());
		ja = (List<String>)r.getResultObject();
		Logger.debug("STARTINGUPDATE\n"+ja);
		if (ja != null && !ja.isEmpty())
			updateStories(ja, result);
		System.out.println("CycleDone "+ cycleCount +" "+result.getErrorString());
		cycleCount++;
		return result;
	}
	
	/**
	 * Here, we are updating existing stories
	 * @param ja
	 * @param result
	 */
	void updateStories(List<String> ja, IResult result) {
		Logger.debug("UpdateStories\n"+ja);
		Iterator<String> itr = ja.iterator();
		String id;
		IResult r;
		JsonObject jo;
		ICommonModel remote, local;
		JsonArray kids;
		while (itr.hasNext()) {
			id = (String)itr.next();
			// get remote
			r = hnClient.getItem(id);
			if (r.hasError())
				result.addErrorString("RA-get-1 "+r.getErrorString());
			jo = (JsonObject)r.getResultObject();
			
			remote = new RssHnModel(jo);
			// get local
			r = database.getObject(id);
			if (r.hasError())
				result.addErrorString("RA-get-f "+r.getErrorString());
			local = (ICommonModel)r.getResultObject();
			// now compare them
			// TODO
			int localScore = local.getScore();
			int remoteScore = remote.getScore();
			if (localScore != remoteScore) {
				database.updateScore(id, remoteScore);
			}
			localScore = local.getDescendentCount();
			remoteScore = remote.getDescendentCount();
			if (localScore != remoteScore) {
				database.updateDescendentCount(id, remoteScore);
			}
			// then check for kids - we don't handle that in the databasa for updates
			if (jo != null) { //kids handled in database - if any
				kids = jo.getAsJsonArray("kids");
				if (kids != null && !kids.isEmpty())
					getKids(kids, result);
			}
		}
	}
	/**
	 * Fetch each story by its ID
	 * @param ja
	 * @param result
	 */
	void getStories(List<String> ja, IResult result) {
		
		Iterator<String> itr = ja.iterator();
		String id;
		IResult r;
		JsonObject jo;
		JsonArray kids;
		while (itr.hasNext()) {
			id = (String)itr.next();
			r = hnClient.getItem(id);
			if (r.hasError())
				result.addErrorString("RA-get-2 "+r.getErrorString());
			//System.out.println("BAH "+r.getResultObject());
			jo = (JsonObject)r.getResultObject();
			System.out.println("Story "+jo);
			Logger.debug("Story "+jo);
			processStory(jo, result);
			//kids handled in database - if any
		}
	}
/******************
{
	"by": "gnicholas",
	"descendants": 14, <<<<<<<< Total number of responses to this story
	"id": 31633857,
	"kids": [31634150, 31634240, 31634129, 31634192, 31633954, 31634056, 31633986, 31634028],
	"score": 22,
	"time": 1654455694,
	"title": "Eating Sea Squirts May Reverse the Signs of Ageing, Study Shows",
	"type": "story",
	"url": "https://www.xjtlu.edu.cn/en/news/2022/may/eating-sea-squirts-may-reverse-the-signs-of-ageing-study-shows"
}	
*****************/
	/**
	 * <p>Begin processing the {@code story}: store in database, including comments</p>
	 * <p> Each story might have <em>kids</em> which are IDs for comments; they must be fetched andprocesses
	 * <p>NOTE: this assumes story is from HackerNews
	 * @param story
	 * @param result
	 */
	void processStory(JsonObject story, IResult result) {
		System.out.println(story);
		//TODO
		//Object snappers = story.get(IConstants.KIDS_FIELD); not needed - database deals with that
		// What we have here is a story in Json form and possibly its kids
		// We must now fetch that story and its kids.
		ICommonModel m = new RssHnModel(story);
		//Send to database
		IResult r = database.putPost(m, -1l, PostType.story, ExternalService.hackernews);
		if (r.hasError())
			result.addErrorString("RA-3 "+r.getErrorString());
		
	}
/*******************
{
	"by": "TedShiller",
	"descendants": 4,
	"id": 31633212,
	"kids": [31633746, 31633445, 31633272, 31633271],
	"score": 2,
	"text": "I&#x27;m not a recruiter, and I deleted my LinkedIn account over 15 years ago, and found I have never needed it. If someone asks me for my LinkedIn, I give my email address instead. This works quite well.<p>What would happen if you deleted your LinkedIn account?",
	"time": 1654451208,
	"title": "Why do you need LinkedIn? (non-recruiters)",
	"type": "story"
}
***************/
	/**
	 * Kids can have kids (nested forum): this can be recursive
	 * @param kids --- Object Ids (long) stored as String
	 * @param result
	 */
	void getKids(JsonArray kids, IResult result) {
		System.out.println("kids"+kids);
		JsonObject child;
		Iterator<JsonElement> itr = kids.iterator();
		JsonElement x;
		String id;
		IResult r;
		JsonObject jo;
		ICommonModel m;
		while (itr.hasNext()) {
			x = itr.next();
			System.out.println("???"+x);
			if (x != null) {
				id = x.getAsString();
				// We have a kid ID
				r = hnClient.getItem(id);
				if (r.hasError())
					result.addErrorString("RA-getKids "+r.getErrorString());
				//System.out.println("BAH "+r.getResultObject());
				jo = (JsonObject)r.getResultObject();
				m = new RssHnModel(jo);
				r = database.putPost(m, -1l, PostType.comment, ExternalService.hackernews);
				if (r.hasError())
					result.addErrorString("RA-getKids2 "+r.getErrorString());
				// it's kids are handled in database
			}
			
		}
	}
			
}
