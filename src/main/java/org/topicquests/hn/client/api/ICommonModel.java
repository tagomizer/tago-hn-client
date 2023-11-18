/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client.api;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * @author jackpark
 * @see https://github.com/HackerNews/API
 */
public interface ICommonModel {
	
	/**
	 * A model makes a {@link JSONObject} which will be indexed for search
	 * @return
	 */
	JsonObject getData();
	
	/**
	 * All objects have a source as enumerated in {@link IConstants}
	 * @param source
	 * same as External Service Type
	 */
	//void setSource(String source);
	//String getSource();
	
	/**
	 * This is the <em>System ID</em> not the <em>Object id</em> from, e.g., hackernews
	 * It will be given  when this object is fetched from our database
	 * @param id
	 */
	void setIdString(String id);
	String getId();
	
	void setObjectId(String id);
	String getObjectId();
	
	/**
	 * Might be {@code null}
	 * @param parentId
	 */
	void setParentId(String parentId);
	String getParentId();
	/**
	 * The {@code by} field in the data and index.
	 * NOTE: this is not the database's internal ID
	 * @param id
	 */
	void setCreatorId(String id);
	String getCreatorId();
	
	void setTimeLong(long time);
	void setTimeString(String time);
	long getTime();
	
	void setModified(long time);
	void setModifiedString(String time);
	long getModified();
	
	void setTitle(String title);
	String getTitle();
	
	void setText(String text);
	String getText();

	
	void setType(PostType type);
	PostType getType();
	
	void setServiceType(ExternalService type);
	ExternalService getServiceType();
	
	void setIsDeleted(boolean isDeleted);
	boolean getIsDeleted();
	
	void setIsDead(boolean isDead);
	boolean getIsDead();
	
	/**
	 * In HackerNews, only stories carry a URL, but we assign that
	 * same URL to each item for indexing
	 * @param url can be {@code null}
	 */
	void setURL(String url);
	String getURL();
	
	void setDescendentCountString(String count);
	void setDescendentCountInt(int count);
	int getDescendentCount();
	
	/**
	 * Score is an int - defaults to 0
	 * @param score
	 */
	void setScoreString(int score);
	int getScore();
	
	void setKidsCountString(String count);
	void setKidsCountInt(int count);
	String getKidsCount();
	
	/**
	 * Can return {@code null} or an empty {@link List}
	 * Stored in JSON as JSONArray
	 * @param kidId
	 */
	void addKidId(String kidId);
	JsonArray listKids();
}
