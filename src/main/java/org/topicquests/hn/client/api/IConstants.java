/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client.api;

/**
 * @author jackpark
 * @see https://github.com/HackerNews/API
 */
public interface IConstants {
	/******************
	 * HN Story

	{
		"by": "gnicholas", <<<< author handle
		"descendants": 14, <<<<<<<< Total number of responses to this story
		"id": 31633857,
		"kids": [31634150, 31634240, 31634129, 31634192, 31633954, 31634056, 31633986, 31634028],
		"score": 22,		<<<<<<< for this story
		"time": 1654455694,
		"title": "Eating Sea Squirts May Reverse the Signs of Ageing, Study Shows",
		"type": "story",
		"url": "https://www.xjtlu.edu.cn/en/news/2022/may/eating-sea-squirts-may-reverse-the-signs-of-ageing-study-shows"
	}	
*/
	/*******************
	 * HN Item

	{
		"by": "TedShiller", <<<< author handle
		"descendants": 4,   <<< number of direct children
		"id": 31633212,  
		"kids": [31633746, 31633445, 31633272, 31633271],
		"score": 2,        <<< for this author
		"text": "I&#x27;m not a recruiter, and I deleted my LinkedIn account over 15 years ago, and found I have never needed it. If someone asks me for my LinkedIn, I give my email address instead. This works quite well.<p>What would happen if you deleted your LinkedIn account?",
		"time": 1654451208,
		"title": "Why do you need LinkedIn? (non-recruiters)",
		"type": "story"
	}
*/
	/** Field types for data which affect mappings */
	public static final String
		BY_FIELD			= "by", 			//Story and Item
		OBJECT_ID_FIELD		= "extid",				// hackernews id
		ID_FIELD			= "id",			// used by postgresdatabase
		PARENT_FIELD		= "parent",			// parent id of comments
		DESCENDENTS_FIELD	= "descendants", 	// story and item == count
		KIDS_FIELD			= "kids",			// "" == array of IDs
		POLL_FIELD			= "poll",			// refers to the poll for which this comment is a "part"
		PARTS_FIELD			= "parts",			// parts of a poll
		SCORE_FIELD			= "score",			// ""
		TIME_FIELD			= "time",			// ""
		MOD_TIME_FIELD		= "modified",		// not in live data - created at update time
		LANGUAGE_FIELD		= "lang",			// not in  live data
		TITLE_FIELD			= "title",			// 
		TYPE_FIELD			= "type",			// 
		URL_FIELD			= "url",			// Story and add to Item to index against URL
		TEXT_FIELD			= "text",			// Item might also be in Story
		SOURCE_FIELD		= "source",			// one of e.g. HN, Reddit, ...
		IS_DELETED_FIELD	= "deleted",		// boolean 
		IS_DEAD_FIELD		= "dead",			// boolean
		ANCESTRY_FIELD		= "ancestry";
;			
	
	/**
	 * Sources refer to which source of information is being harvested
	 */
	public static final String
		HN_SOURCE			= "hn", //HackerNews
		RT_SOURCE			= "reddit";
}
