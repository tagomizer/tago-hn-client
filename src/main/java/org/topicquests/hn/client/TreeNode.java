/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client;

import java.util.ArrayList;
import java.util.List;

import org.topicquests.hn.client.api.ExternalService;
import org.topicquests.hn.client.api.IConstants;
import org.topicquests.hn.client.api.ITreeNode;
import org.topicquests.hn.client.api.PostType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author jackpark
 *
 */
public class TreeNode implements ITreeNode {
	private JsonObject data;
	/**
	 * 
	 */
	public TreeNode() {
		data = new JsonObject();
	}
	
	public TreeNode(JsonObject d) {
		data = d;
	}

	@Override
	public JsonObject getData() {
		return data;
	}

	@Override
	public void setIdString(String id) {
		data.addProperty(IConstants.ID_FIELD, id);
	}

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
	public void setParentId(String parentId) {
		data.addProperty(IConstants.PARENT_FIELD, parentId);
	}

	@Override
	public String getParentId() {
		JsonElement o = data.get(IConstants.PARENT_FIELD);
		if (o == null) return null;
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
	public void addChildNode(JsonObject kid) {
		JsonArray ja = listChildren();
		if (ja == null) {
			ja = new JsonArray();
			data.add(IConstants.KIDS_FIELD, ja);
		}
		ja.add(kid);
	}

	@Override
	public JsonArray listChildren() {
		JsonElement o = data.get(IConstants.KIDS_FIELD);
		JsonArray ja = null;
		if (o == null) {
			return null;
		} else
			ja = o.getAsJsonArray();
		return ja;
	}

	@Override
	public void setAncestry(String ancestry) {
		data.addProperty(IConstants.ANCESTRY_FIELD, ancestry);
	}

	@Override
	public List<String> getAncestry() {
		JsonElement o = data.get(IConstants.ANCESTRY_FIELD);
		if (o != null) {
			String id = this.getId();
			List<String>result = new ArrayList<String>();
			String ax = o.getAsString();
			String [] vals = ax.split("."); // e.g. 41.43.44.45
			int len = vals.length;
			for (int i=0;i<len;i++) {
				if (!vals[i].equals(id))
					result.add(vals[i]);
			}
			return result;
		}
		// TODO Auto-generated method stub
		return null;
	}

}
