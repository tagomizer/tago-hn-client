/**
 * 
 */
package org.topicquests.hn.rss.api;

import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public interface IBacksideDatabase {
	/**
	 * NOTE: any objectId here refers to the database's {@code ext_id} field
	 * not the id field,  which is our internal identifier, a sequential BigInt.<br/>
	 * Object Ids are those aasignedd by e.g. hackernews.<br/>
	 * We treat object Ids as Strings (varchar in the db), eventhough they may be numeric at thesource.
	 */
	
	/**
	 * A post is any Story or comment or poll object
	 * @param m
	 * @param parentId can be {@code -1} which is the same as {@code null}
	 * @param postType TODO
	 * @param externalService TODO
	 * @return
	 */
	IResult putPost(ICommonModel m, long parentId, PostType postType, ExternalService externalService);
	
	/**
	 * Will return the database ID or {@code null}
	 * @param objectId
	 * @return
	 */
	IResult existsObject(String objectId);
	
	IResult removeObject(String objectId);
	
	/**
	 * Serves both story and comment objects
	 * @param objectId
	 * @return
	 */
	IResult getObject(String objectId);
	
	/**
	 * Some objects, e.g. posts or comments, may have "kids"
	 * @param objectId
	 * @return
	 */
	IResult getRecursive(String objectId);
	
	/**
	 * <p>List everything in the {@code public.post}table</p>
	 * <p>Returns {@code id,title,bodytext}</p>
	 * @param limit
	 * @param offset
	 * @return
	 */
	IResult listEntities(int limit, int offset);
	
	////////////////////
	//Specific queries
	/////////////////////
	IResult listStories(int limit, int offset);
	
	IResult fetchAllByAuthor(String author);
	
	IResult fetchStoriesByAuthor(String author);
	
	IResult fetchCommentsByAuthor(String author);
	
	IResult fetchPollsByAuthor(String author);
	
	IResult fetchByURL(String url);
	
	IResult fetchByTitle(String title);
	
	/**
	 * For full text search
	 * @param text
	 * @return
	 */
	IResult textSearch(String text);
	
	IResult updateScore(String itemId, int newScore);
	
	IResult updateDescendentCount(String itemId, int newCount);
	
	IResult addTag(long itemId, long authorId, String tagText);
	
	IResult getTagsByItemId(String itemId);
}

