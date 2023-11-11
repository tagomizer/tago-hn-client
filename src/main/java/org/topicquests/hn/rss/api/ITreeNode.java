/**
 * 
 */
package org.topicquests.hn.rss.api;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author jackpark
 * <p>For HackerNews, the root node is almost always a Story, Poll, or Job</p>
 */
public interface ITreeNode {

	JsonObject getData();

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
	
	void addChildNode(JsonObject kid);
	
	/**
	 * Can return {@code null}
	 * @return
	 */
	JsonArray listChildren();
	
	void setAncestry(String ancestry);
	
	/**
	 * Can return {@code null}
	 * @return
	 */
	List<String> getAncestry();
	/////////////////
	// TODO - might want some kind of search inside the tree
	/////////////////
}
