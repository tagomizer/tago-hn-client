/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client.search;

import org.topicquests.hn.client.api.ExternalService;
import org.topicquests.hn.client.api.ICommonModel;
import org.topicquests.hn.client.api.IConstants;
import org.topicquests.hn.client.api.PostType;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;


/**
 * @author jackpark
 * Mostly a POJO for crafting JSONObjects for indexing
 */
public class RssHnModel implements ICommonModel  {
	private JsonObject data; 
	/**
	 * 
	 */
	public RssHnModel() {
		data = new JsonObject();
	}
	
	public RssHnModel(JsonObject d) {
		data = d;
		System.out.println("MODEL\n"+d);
		String what = getObjectId();
		//Sometimes the data from HN server has id="foo"
		// but we want objectId to be that,so we have to patch
		if (what == null) {
			what = getId();
			setObjectId(what);
			setIdString(null);
		}
	}
	
	@Override
	public JsonObject getData() {
		return data;
	}
/*
	@Override
	public void setSource(String source) {
		data.put(IConstants.SOURCE_FIELD, source);
	}

	
	@Override
	public String getSource() {
		return data.getAsString(IConstants.SOURCE_FIELD);
	}
*/
	@Override
	public void setIdString(String id) {
		data.addProperty(IConstants.ID_FIELD, id);
	}

	//@Override
	//public void setIdLong(long id) {
	//	data.put(IConstants.ID_FIELD, Long.toString(id));
	//}

	@Override
	public String getId() {
		return data.get(IConstants.ID_FIELD).getAsString();
	}

	@Override
	public void setObjectId(String id) {
		data.addProperty(IConstants.OBJECT_ID_FIELD, id);
	}

	@Override
	public String getObjectId() {
		JsonElement o  = data.get(IConstants.OBJECT_ID_FIELD);
		if (o == null)
			return null;
		return o.getAsString();
	}
	@Override
	public void setCreatorId(String id) {
		data.addProperty(IConstants.BY_FIELD, id);
	}

	@Override
	public String getCreatorId() {
		JsonElement o = data.get(IConstants.BY_FIELD);
		if (o != null)
			return o.getAsString();
		return null;
	}

	@Override
	public void setTimeLong(long time) {
		data.addProperty(IConstants.TIME_FIELD, Long.toString(time));
	}

	@Override
	public void setTimeString(String time) {
		data.addProperty(IConstants.TIME_FIELD, time);
	}

	@Override
	public long getTime() {
		return data.get(IConstants.TIME_FIELD).getAsLong();
	}
	@Override
	public void setModified(long time) {
		data.addProperty(IConstants.MOD_TIME_FIELD, Long.toString(time));
	}
	@Override
	public void setModifiedString(String time) {
		data.addProperty(IConstants.MOD_TIME_FIELD, time);
	}
	@Override
	public long getModified() {
		return data.get(IConstants.MOD_TIME_FIELD).getAsLong();
	}

	@Override
	public void setTitle(String title) {
		data.addProperty(IConstants.TITLE_FIELD, title);
	}

	@Override
	public String getTitle() {
		JsonElement o = data.get(IConstants.TITLE_FIELD);
		if (o == null) return null;
		return o.getAsString();
	}

	@Override
	public void setURL(String url) {
		data.addProperty(IConstants.URL_FIELD, url);
	}

	@Override
	public String getURL() {
		JsonElement o = data.get(IConstants.URL_FIELD);
		if (o == null) return null;
		return o.getAsString();
	}

	@Override
	public void setDescendentCountString(String count) {
		data.addProperty(IConstants.DESCENDENTS_FIELD, count);
	}

	@Override
	public void setDescendentCountInt(int count) {
		setDescendentCountString(Integer.toString(count));
	}

	@Override
	public int getDescendentCount() {
		JsonElement o = data.get(IConstants.DESCENDENTS_FIELD);
		if (o == null) return 0;
		return o.getAsInt() ;
	}

	@Override
	public void setScoreString(int score) {
		data.addProperty(IConstants.SCORE_FIELD, score);
	}

	@Override
	public int getScore() {
		JsonElement o = data.get(IConstants.SCORE_FIELD);
		if (o == null) return 0;
		return o.getAsInt();
	}

	@Override
	public void setKidsCountString(String count) {
		data.addProperty(IConstants.DESCENDENTS_FIELD, count);
	}

	@Override
	public void setKidsCountInt(int count) {
		setKidsCountString(Integer.toString(count));
	}

	@Override
	public String getKidsCount() {
		JsonElement o = data.get(IConstants.DESCENDENTS_FIELD);
		if (o == null) return "0";
		return o.getAsString();
	}

	@Override
	public void addKidId(String kidId) {
		JsonArray ja = listKids();
		if (ja == null) ja = new JsonArray();
		ja.add(kidId);
		data.add(IConstants.KIDS_FIELD, ja);
	}

	@Override
	public JsonArray listKids() {
		JsonElement o = data.get(IConstants.KIDS_FIELD);
		if (o==null) return null;
		return o.getAsJsonArray();
	}

	@Override
	public void setText(String text) {
		data.addProperty(IConstants.TEXT_FIELD, text);
	}

	@Override
	public String getText() {
		JsonElement o = data.get(IConstants.TEXT_FIELD);
		if (o == null) return null;
		return o.getAsString();
	}

	@Override
	public void setType(PostType type) {
		data.addProperty(IConstants.TYPE_FIELD, type.toString());
	}

	@Override
	public PostType getType() {
		JsonElement o = data.get(IConstants.TYPE_FIELD);
		if (o == null) return PostType.story; // default
		return PostType.valueOf(o.getAsString());
	}

	@Override
	public void setServiceType(ExternalService type) {
		data.addProperty(IConstants.SOURCE_FIELD, type.toString());
	}

	@Override
	public ExternalService getServiceType() {
		JsonElement o = data.get(IConstants.SOURCE_FIELD);
		if (o == null) return ExternalService.hackernews; //default
		return ExternalService.valueOf(o.getAsString());
	}

	@Override
	public void setIsDeleted(boolean isDeleted) {
		data.addProperty(IConstants.IS_DELETED_FIELD, new Boolean(isDeleted) );
	}

	@Override
	public boolean getIsDeleted() {
		JsonElement o = data.get(IConstants.IS_DELETED_FIELD);
		if (o == null) return false;
		return o.getAsBoolean();
	}

	@Override
	public void setIsDead(boolean isDead) {
		data.addProperty(IConstants.IS_DEAD_FIELD, new Boolean(isDead) );
	}

	@Override
	public boolean getIsDead() {
		JsonElement o = data.get(IConstants.IS_DEAD_FIELD);
		if (o == null) return false;
		return o.getAsBoolean();
	}

	@Override
	public void setParentId(String parentId) {
		data.addProperty(IConstants.PARENT_FIELD, parentId);
	}

	@Override
	public String getParentId() {
		JsonElement o = data.get(IConstants.PARENT_FIELD);
		if (o == null) return null;
		return o.getAsString();
	}

	

}
