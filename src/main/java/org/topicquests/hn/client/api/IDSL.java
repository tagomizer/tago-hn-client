/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client.api;

import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public interface IDSL {

	IResult putPost(ICommonModel m);
	
	/**
	 * <p>NOTE: there are two identities in play<br/>
	 * the identity given to an object by its source<br/>
	 * the identity we give it in the database</br/>
	 * This fetches by the object's given identity</p>
	 * <p>When a post or comment comes in, it comes in with its own identity,
	 * but we must pay attention to the fact that we persist against a BigInt</p>
	 * @param objectId
	 * @return
	 */
	IResult exists(String objectId);
	
	/**
	 * This checks against our internal identity.
	 * @param id
	 * @return
	 */
	IResult existsEntry(long id);
	
	/**
	 * Serves both story and comment objects
	 * @param objectId - the identity given by the source
	 * @return
	 */
	IResult getObject(String objectId);
	
	/**
	 * The object identified by our internal BigInt identity
	 * @param id
	 * @return
	 */
	IResult getEntry(long id);
	/////////////////
	//TODO
	//	Need a Query API
	//  Let's start with SQL
	// @see IQueries
	// NOTE everything here defaults to ExternalService.hackernews
	//////////////////
	
	IResult sqlSelectQuery(String sqlQueryString);
	
	/**
	 * Returns everything by this author
	 * @param authorId
	 * @return
	 */
	IResult fetchByAuthor(String authorId);
	IResult fetchByAuthorEntry(long id);
	
	IResult fetchTypeByAuthor(String authorId, PostType type);
	IResult fetchTypeByAuthorEntry(long id, PostType type);
	
	IResult fetchByURL(String url);
	IResult fetchByURLEntry(long urlId);
	
	IResult fetchByTitle(String title);
	
	IResult fullTextQuery(String text, String language);
	
}
