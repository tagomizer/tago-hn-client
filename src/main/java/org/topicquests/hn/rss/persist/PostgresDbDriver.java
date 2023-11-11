/**
 * 
 */
package org.topicquests.hn.rss.persist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.topicquests.hn.rss.RssEnvironment;
import org.topicquests.hn.rss.RssHnClient;
import org.topicquests.hn.rss.TreeNode;
import org.topicquests.hn.rss.api.ExternalService;
import org.topicquests.hn.rss.api.IBacksideDatabase;
import org.topicquests.hn.rss.api.ICommonModel;
import org.topicquests.hn.rss.api.IQueries;
import org.topicquests.hn.rss.api.ITreeNode;
import org.topicquests.hn.rss.api.PostType;
import org.topicquests.hn.rss.search.RssHnModel;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.pg.api.IPostgresConnection;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.support.util.LRUCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.tinylog.Logger;



/**
 * @author jackpark
 *
 */
public class PostgresDbDriver implements IBacksideDatabase {
	private RssEnvironment environment;
	private RssHnClient hnClient;
	private PostgresConnectionFactory database;
	private LRUCache storyCache; //TODO what does cache use as key?
	private final int clientCacheSize = 8192;

	/**
	 * 
	 */
	public PostgresDbDriver(RssEnvironment env) {
		environment = env;
		hnClient = environment.getHnClient();
		database = environment.getPostgresFactory();
	    storyCache = new LRUCache(clientCacheSize);

	}

	//////////////////////////////////////////
	//TODO
	// A Post can have children, or be a child of some other Post
	// If there is a list of descendents (children), THEN
	//		We must fetch them as individuals and see to it that they are stored
	//		paying attention to the ancestry - ltree -
	//{
	//	"by": "BitPolice",
	//	"descendants": 1,
	//	"id": 34467639,
	//	"kids": [34468045],
	//	"score": 6,
	//	"time": 1674319213,
	//	"title": "TikTok's enshittification (21 Jan 2023)",
	//	"type": "story",
	//	"url": "https://pluralistic.net/2023/01/21/potemkin-ai/#hey-guys"
	//}
	//////////////////////////////////////////
	@Override
	public IResult putPost(ICommonModel m, long parentId, PostType postType, ExternalService externalService) {
		  Logger.debug("DBconn- "+m.getObjectId());
		  System.out.println("PutPost- "+m.getObjectId());
		IResult result = new ResultPojo();
	    IPostgresConnection conn = null;
	    String objectId = m.getObjectId(); //string
	    try {
	      conn = database.getConnection();
	      //Logger.error("PDD-0 "+objectId+" "+m+" "+postType+" "+parentId+" "+externalService+" "+conn+" "+result.getErrorString(), null);
		  //Logger.debug("DBconn "+conn);
		  //System.out.println("DBconn "+conn);
	      result = _putPost(m, objectId, parentId, postType, externalService, conn, true);
	      //Logger.error("PDD-1 "+objectId+" "+result.getErrorString(), null);
	      if (result.hasError())
	    	  result.addErrorString("PDD4a");
	      //Logger.error("PDD-2 "+objectId+" "+result.getErrorString(), null);
	      // then push to cache only if it's a story
		  System.out.println("PutPost+ "+m.getObjectId());

	    } catch (Exception e) {
	      result.addErrorString("PDD-4 "+objectId+" "+e.getMessage());
	      Logger.error("PDD-5 "+objectId+" "+result.getErrorString(), e);
	    }
	    conn.closeConnection(result);

	    return result;	
	}
	
	/**
	 * Liable to be recursive on children - if any
	 * @param m
	 * @param objectId
	 * @param parentId can be {@code null}
	 * @param postType
	 * @param externalService
	 * @param conn
	 * @param test {@code true] if existence test needed
	 * @return
	 * @throws Exception
	 */
	private IResult _putPost(ICommonModel m, String objectId, long parentId, PostType postType,
				ExternalService externalService, IPostgresConnection conn, boolean test) throws Exception {
	    Logger.debug("PUTTING POST- "+objectId+"\n"+m.getData());
	      
		IResult result = new ResultPojo();
		boolean isDeleted = m.getIsDeleted();
		if (isDeleted) {
			handleIsDeleted(m, conn, result);
		} else {
			boolean okToPost = !test;
			if (test) {
				//First - have we see3n this object?
				IResult r =  _existsObject(objectId, conn); //NOTE: coult toss an exception
				/////////////////////////////
				// Just because it exists is no reason to skip it.
				// We need to examine it for possible mutations and updates
				// We will do that in a new Thread
				/////////////////////////////
				if (r.getResultObject() == null) 
					okToPost = true;
			}
			//Logger.error("PUTTING POST-a "+okToPost,null);
			if (okToPost) {
		      String sql = IQueries.putEntity;
		      // When you put an entity, authors, urls, must be converted to their local ID - BigInt
		      //URL
		      String url = m.getURL();
		      long urlId = -1;
		      if (url != null && !url.equals(""))
		    	  urlId = getOrInsertURL(url, conn);;
		      //Logger.error("PUTTING POST-1 "+urlId,null);
		      //TODO massive error if it's -1
		      //AUTHOR
		      String author = m.getCreatorId();
		      //Logger.debug("PUTTING POST-3 "+author);
		      long authorId = getOrInsertAuthor(author, conn);
		      //Logger.error("PUTTING POST-4 "+authorId+" "+postType.toString(),null);
		      String lang = "en"; //FOR NOW 
		      //TODO massive error if it's -1
		      // Now we can put together a full query
		      // post_type, ext_source, ext_id, author, story_url,
		      //	"pub_time, score, descendents, lang, is_dead, is_deleted, title, body, parent, ancestry
		      Object [] o = new Object[15];
		      o[0] = postType.toString();
		      o[1] = externalService.toString();
		      o[2] = objectId;
		      o[3] = authorId; 
		      //Logger.debug("PUTTING POST-4-1 "+objectId);
		      if (urlId == -1)
		    	  o[4] = null; 
		      else
		    	  o[4] = urlId; 
		      //Logger.debug("PUTTING POST-4-2 "+objectId);
		      Timestamp t = new Timestamp(m.getTime());
		      o[5] = t;
		      o[6] = t; //last modified
		      //Logger.debug("PUTTING POST-4-3 "+objectId);
		      o[7] = m.getScore(); //TODO int
		      //Logger.debug("PUTTING POST-4-4 "+objectId);
		      o[8] = m.getDescendentCount();
		      //Logger.debug("PUTTING POST-4-5 "+objectId);
		      o[9] = lang;
		      o[10] = m.getIsDead(); // boolean
		      //Logger.debug("PUTTING POST-4-6 "+objectId);
		      o[11] = isDeleted; // boolean
		      //Logger.debug("PUTTING POST-4-7 "+objectId);
		      o[12] = m.getTitle();
		      //Logger.debug("PUTTING POST-4-8 "+objectId);
		      o[13] = m.getText();
		      //Logger.debug("PUTTING POST-4-9 "+objectId);
		      if (parentId == -1) {
		    	  String px = m.getParentId();
		    	  if (px != null)
		    		  o[14] = Long.parseLong(px);
		    	  else
		    		  o[14] = null;
		      }
		      else
		    	  o[14] = parentId;
		     
		      //Logger.debug("PUTTING POST-5 "+parentId);
		      result = conn.executeSelect(sql, o);
		      Logger.debug("PUTTING POST+ "+objectId+" "+result.getErrorString());
		      // no error messages
		      long postId = -1;
				ResultSet rs = (ResultSet)result.getResultObject();
		    	Logger.debug("PUTTING POST-6 "+rs);
				if (rs != null && rs.next()) {
					postId = rs.getLong("id");
			    	Logger.debug("PUTTING POST-6a "+postId+" "+m);
				// DEAL WITH kids
			      JsonArray kids = m.listKids();
		    	  Logger.debug("PUTTING POST-7 "+kids);
			      if (kids != null && !kids.isEmpty()) {
			    	  //Logger.error("PUTTING POST-8 "+postId,null);
				      //TODO dying in here
			    	  processKids(kids, postId, conn);
				      System.out.println("PUTTING POST-9 "+objectId);
			      }
				}
			}
		}
		return result;
	}
	/**
	 * Really, this could be a thread, but then you have some issues
	 * @param kids not null and not empty
	 * @param parentId
	 * @param conn
	 * @throws
	 */
	private IResult processKids(JsonArray kids, long parentId, IPostgresConnection conn) throws Exception{
		IResult result = new ResultPojo();
		Iterator<JsonElement> itr = kids.iterator();
		String kidId;
		IResult r;
		JsonObject jo;
		//JsonArray kids;
		while (itr.hasNext()) { // for each kid
			kidId = itr.next().getAsString();
			//DO WE HAVE THIS KID?
			Logger.debug("PK-1 "+kidId);
			r = _existsObject(kidId, conn); //NOTE: coult toss an exception
		    Logger.debug("PK-2 "+kidId+" "+r.getErrorString()+" | "+r.getResultObject());
		    if (r.getResultObject() == null) {
				//TODO have we seen this before?
				r = hnClient.getItem(kidId);
				if (r.hasError())
					result.addErrorString("PDD-3 "+r.getErrorString());
				Logger.debug("PK-3 "+r.getResultObject()+" | "+r.getErrorString());
				jo = (JsonObject)r.getResultObject();
				Logger.debug("PK-4 "+r.getResultObject());
				ICommonModel m = new RssHnModel(jo);
				//Send to database
				//TODO we MUST examine m.getType() to set posttype
				_putPost(m, kidId, parentId, PostType.comment, ExternalService.hackernews, conn, false);	
			}
		}
		return result;
	}
	
	private void handleIsDeleted(ICommonModel m, IPostgresConnection conn, IResult r) {
		String objectId = m.getObjectId();
		Logger.debug("PHD-1 "+objectId);
		//TODO
	}
	private long getOrInsertURL(String url, IPostgresConnection conn) throws Exception {
		long result = getUrlId(url, conn);
		if (result == -1)
			result = insertUrl(url, conn);
		return result;
	}
	
	public long getOrInsertAuthor(String author, IPostgresConnection conn) throws Exception {
		System.out.println("GetInsertAuthor- "+author);
		long result = getAuthorId(author, conn);
		System.out.println("GetInsertAuthor- "+result);
		if (result == -1)
			result = insertAuthor(author, conn);
		return result;
	}

	private long insertUrl(String url, IPostgresConnection conn) throws Exception {
		System.out.println("InsertURL "+url);
		long result = -1;
		IResult r = new ResultPojo();
		String sql = IQueries.insertURL;
		Object [] val = new Object[1];
		val[0] = url;
		conn.beginTransaction(r);
		conn.executeSelect(sql, r, val);
		ResultSet rs = (ResultSet)r.getResultObject();
		if (rs != null && rs.next())
			result = rs.getLong("id");
		conn.endTransaction(r);
		if (r.hasError()) 
			Logger.debug(r.getErrorString());	
		
		return result;
	}
	private long getUrlId(String url, IPostgresConnection conn) throws Exception {
		//Logger.debug("FOO "+url, null);
		long result = -1;
		String sql = IQueries.getURLId;
		String [] val = new String[1];
		val[0] = url;
		IResult r = conn.executeSelect(sql, val);
		if (r.hasError()) 
			Logger.debug(r.getErrorString());
		ResultSet rs = (ResultSet)r.getResultObject();
		if (rs != null && rs.next())
			result = rs.getInt("id");
		return result;
	}
	private long insertAuthor(String author, IPostgresConnection conn) throws Exception {
		System.out.println("InsertAuthor- "+author);
		long result = -1;
		IResult r = new ResultPojo();
		String sql = IQueries.insertAuthor;
		Object [] val = new Object[1];
		val[0] = author;
		conn.beginTransaction(r);
		conn.executeSelect(sql, r, val);
		ResultSet rs = (ResultSet)r.getResultObject();
		if (rs != null && rs.next())
			result = rs.getLong("id");
		conn.endTransaction(r);
		if (r.hasError()) 
			Logger.debug(r.getErrorString());	
		if (result == -1) {
			Logger.debug("InsertAuthor failed: "+author+" "+rs);
			throw new RuntimeException("BadAuthorInsert "+author);
		}
			
		return result;
	}
	private long getAuthorId(String author, IPostgresConnection conn) throws Exception {
		long result = -1;
		String sql = IQueries.getAuthorId;
		Object [] val = new Object[1];
		val[0] = author;
		IResult r = conn.executeSelect(sql, val);
		if (r.hasError()) 
			Logger.debug(r.getErrorString());
		ResultSet rs = (ResultSet)r.getResultObject();
		if (rs != null && rs.next())
			result = rs.getInt("id");
		return result;
	}

	@Override
	public IResult existsObject(String objectId) {
		IResult result = new ResultPojo();
	    IPostgresConnection conn = null;    
	    try {
	      conn = database.getConnection();
	      result = _existsObject(objectId, conn);
	    } catch (Exception e) {
	    	result.addErrorString("PDD-1 "+e.getMessage());
	    	Logger.error(e.getMessage(), e);
	    }
	    return result;
	}
	
	IResult _existsObject(String objectId, IPostgresConnection conn) throws Exception {
		IResult result = new ResultPojo();
		//System.out.println("EO- "+objectId);
		String sql = IQueries.existsObject;
	      Object [] val = new Object[1];
	      val[0] = objectId;
			//System.out.println("EO-1 "+sql);
	      IResult r = conn.executeSelect(sql, val);
			//System.out.println("EO-2 "+r.hasError());
	      if (r.hasError()) 
	    	  Logger.debug(r.getErrorString());
	      ResultSet rs = (ResultSet)r.getResultObject();
	      if (rs != null && rs.next())
	    	  result.setResultObject(rs.getString("id"));
	      else
	    	  result.setResultObject(null);
	    return result;
	}

	@Override
	public IResult removeObject(String objectId) {
		IResult result = new ResultPojo();
	    IPostgresConnection conn = null;    
	    try {
	      conn = database.getConnection();
	      String sql = IQueries.removeObject;
	      Object [] val = new Object[1];
	      val[0] = objectId;
	      result = conn.executeSelect(sql, val);
	      conn.closeConnection(result);
	    } catch (Exception e) {
	    	Logger.error("PDD-5 "+e.getMessage(), e);
	    }
	    return result;
	}
	
	/**
	 * <p>THIS IS HUGE: must fetch all the kids and their descendents</p>
	 * <p>This takes an sQL {@code ResultSet} which has fetched a <em>post</em> and
	 * 	unbundles it into a view. That's made complicated because some objects include
	 * 	<em>children</em> - on HackerNews, a Post is a story which serves as a root (parent)
	 * 	for a possibly nested tree of comments.That makes this a complex process</p>
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	private ITreeNode unbundlePost(ResultSet rs) throws Exception {
		ITreeNode result = new TreeNode();
	      //id post_type, ext_source, ext_id, author, story_url, "+
		  //pub_time, score,descendents lang, is_dead, is_deleted, title, body, parent, ancestry
		result.setType(PostType.valueOf(rs.getString("post_type")));
		result.setIdString(rs.getString("id"));
		result.setServiceType(ExternalService.valueOf(rs.getString("ext_source")));
		result.setObjectId(rs.getString("ext_id"));
		result.setCreatorId(rs.getString("text"));
		result.setURL(rs.getString("url"));
		result.setTimeString(rs.getString("pub_time"));
		result.setScoreString(rs.getInt("score"));
		result.setDescendentCountInt(rs.getInt("descendents"));
		//not doing language
		result.setIsDead(rs.getBoolean("is_dead"));
		result.setIsDeleted(rs.getBoolean("is_deleted"));
		result.setTitle(rs.getString("title"));
		result.setText(rs.getString("body"));
		result.setParentId(rs.getString("parent"));
		result.setAncestry(rs.getString("ancestry"));
		return result;
	}

	@Override
	public IResult getObject(String objectId) {
		IResult result = new ResultPojo();
	    IPostgresConnection conn = null;    
	    try {
	      conn = database.getConnection();
	      String sql = IQueries.getObject;
	      result = _getObject(objectId,sql, conn);
	      conn.closeConnection(result);
	    } catch (Exception e) {
	    	result.addErrorString("PDD-2 "+e.getMessage());
	    	Logger.error(e.getMessage(), e);
	    }
	    return result;
	}
	IResult _getObject(String objectId, String sql,IPostgresConnection conn) throws Exception {
		Logger.debug("PDBgetObject "+objectId+"\n"+sql);
		IResult result = new ResultPojo();
		Object [] val = new Object[1];
	      val[0] = Long.valueOf(objectId);
	      IResult r = conn.executeSelect(sql, val);
	      if (r.hasError()) 
	    	  Logger.debug("PDDB-x "+result.getErrorString());
	      ResultSet rs = (ResultSet)r.getResultObject();
	      if (rs != null && rs.next())
	    	  result.setResultObject(unbundlePost(rs));
		return result;
	}

	@Override
	public IResult getRecursive(String objectId) {
		Logger.debug("PDBgetRecursive "+objectId);
		IResult result = new ResultPojo();
	    IPostgresConnection conn = null;    
	    try {
	      conn = database.getConnection();
	      String sql = IQueries.getRecursiveEntityById;
	      System.out.println(sql);
	      Object [] val = new Object[1];
	      val[0] = objectId;
	      result = conn.executeSelect(sql, val);
	      if (result.hasError()) 
	    	  Logger.debug(result.getErrorString());
	      ResultSet rs = (ResultSet)result.getResultObject();
	      boolean isFired = false;
	      ITreeNode root = null;
	      ITreeNode working, temp;
	      Map<String, ITreeNode> theTree = new HashMap<String, ITreeNode>();
	      String idx, ptx;
	      if (rs != null) {
	    	  while (rs.next()) {
	    		  if (!isFired) {
	    			  isFired = true;
	    			  root = unbundlePost(rs);
		    		  Logger.debug("ROOT\n"+root.getData());
	    			  idx = root.getId();
	    			  theTree.put(idx, root);
	    		  } else {
		    		  ///////////////////////////
		    		  // first row is story
		    		  // subsequent rows are comments
		    		  // they can form a tree structure based on ancestors field]
		    		  //////////////////////////
		    		  working = unbundlePost(rs);
		    		  Logger.debug("WORKING\n"+working.getData());
		    		  idx = working.getId();
	    			  theTree.put(idx, working);
	    			  ptx = working.getParentId();
	    			  temp = theTree.get(ptx);
	    			  temp.addChildNode(working.getData());
	    		  }
	    	  }
	    	  result.setResultObject(root);
	    	  if (result.hasError()) 
		    	  Logger.debug(result.getErrorString());
		      rs = (ResultSet)result.getResultObject();
		      if (rs != null) 
		    	  result.setResultObject(unbundlePost(rs));
	    	  Logger.debug("BIGMONSTER "+objectId+" "+theTree.size());
	      }
	      if (!isFired) {
	    	  // this one had no comments
	    	  sql = IQueries.getObject;
		      System.out.println(sql);
		      result = _getObject(objectId,sql, conn);
	      }
	      conn.closeConnection(result);
	    } catch (Exception e) {
	    	result.addErrorString("PDD-2 "+e.getMessage());
	    	Logger.error(e.getMessage(), e);
	    }
		return result;
	}

	@Override
	public IResult fetchAllByAuthor(String author) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult fetchStoriesByAuthor(String author) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult fetchCommentsByAuthor(String author) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult fetchPollsByAuthor(String author) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult fetchByURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult fetchByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult textSearch(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult updateScore(String itemId, int newScore) {
		IResult result = new ResultPojo();
	    IPostgresConnection conn = null;    
	    try {
	      conn = database.getConnection();
	      String sql = IQueries.updateScore;
	      Object [] val = new Object[2];
	      val[0] = Integer.valueOf(newScore);
	      val[1] = Long.valueOf(itemId);
	      result = conn.executeSQL(sql, val);
	      conn.closeConnection(result);
	    } catch (Exception e) {
	    	Logger.error("PDD-7 "+e.getMessage(), e);
	    }
	    return result;
	}

	@Override
	public IResult updateDescendentCount(String itemId, int newCount) {
		IResult result = new ResultPojo();
	    IPostgresConnection conn = null;    
	    try {
	      conn = database.getConnection();
	      String sql = IQueries.updateDescendentCount;
	      Object [] val = new Object[2];
	      val[0] = Integer.valueOf(newCount);
	      val[1] = Long.valueOf(itemId);
	      result = conn.executeSQL(sql, val);
	      conn.closeConnection(result);
	    } catch (Exception e) {
	    	Logger.error("PDD-8 "+e.getMessage(), e);
	    }
	    return result;
	}

	@Override
	public IResult addTag(long itemId, long authorId, String tagText) {
		//Get tagId if exists, otherwise insert tag, then add to post
		IResult result = new ResultPojo();
	    IPostgresConnection conn = null;    
	    try {
	      conn = database.getConnection();
	      String sql = "SELECT id FROM public.tag WHERE text=?";
	      Object [] val = new Object[1];
	      val[0] = tagText;
	      IResult r = conn.executeSelect(sql, val);
	      if (result.hasError()) 
	    	  Logger.error(result.getErrorString());
	      ResultSet rs = (ResultSet)r.getResultObject();
	      long tagId =-1;
	      boolean isNew = false;
	      if (rs != null) {
	    	  if (rs.next()) {
	    		  tagId = rs.getLong("id");
	    		  isNew = true;
	    	  }
	      }
	      boolean exists = false;
	      if (tagId == -1) {
	    	  // insert tag
	    	  sql = IQueries.insertTag;
	    	  r = conn.executeSelect(sql, val);
	    	  if (result.hasError()) 
		    	  Logger.error(result.getErrorString());
	    	  rs = (ResultSet)r.getResultObject();
		      if (rs != null) {
		    	  if (rs.next())
		    		  tagId = rs.getLong("id");
		      }
	      }
	      if (tagId>-1) {
	    	  // see if this tag/post exists
	    	  if (!isNew) {
	    		  sql = "SELECT * FROM public.tag_post WHERE post_id = ? AND tag_id = ?";
		    	  val = new Object[2];
		    	  val[0] = Long.valueOf(itemId);
		    	  val[1] = Long.valueOf(tagId);
		    	  r = conn.executeSelect(sql, val);
		    	  if (result.hasError()) 
			    	  Logger.error(result.getErrorString());
		    	  rs = (ResultSet)r.getResultObject();
			      if (rs != null) {
			    	  if (rs.next())
			    		  exists = true;
			      }
	    	  }
	    	  if (!exists) {
		    	  sql = IQueries.addTagToPost;
		    	  val = new Object[2];
		    	  val[0] = Long.valueOf(itemId);
		    	  val[1] = Long.valueOf(tagId);
		    	  r = conn.executeSQL(sql, val);
		    	  if (result.hasError()) 
			    	  Logger.error(result.getErrorString());
		    	  sql= IQueries.addTagToAuthor;
		    	  val[0] = Long.valueOf(authorId);
		    	  r = conn.executeSQL(sql, val);
		    	  if (result.hasError()) 
			    	  Logger.error(result.getErrorString());
	    	  }
	      }
	    } catch (Exception e) {
	    	Logger.error(e.getMessage(), e);
	    }

		return result;
	}

	@Override
	public IResult getTagsByItemId(String itemId) {
		IResult result = new ResultPojo();
	    IPostgresConnection conn = null;    
	    try {
	      conn = database.getConnection();
	      String sql = IQueries.getTagsByPost;
		// TODO Auto-generated method stub
	    } catch (Exception e) {
	    	
	    }

		return result;
	}

	@Override
	public IResult listStories(int limit, int offset) {
		IResult result = new ResultPojo();
	    IPostgresConnection conn = null;    
	    try {
	      conn = database.getConnection();
	      String sql = IQueries.FetchAllStories;
	     // System.out.println(sql);
	     // System.out.println(ascDesc+" "+limit+" "+offset);
	      Object [] val = new Object[2];
	      val[0] = Integer.valueOf(limit);
	      val[1] = Integer.valueOf(offset);
	      result = conn.executeSelect(sql, val);
	      if (result.hasError()) 
	    	  Logger.error(result.getErrorString());
	      ResultSet rs = (ResultSet)result.getResultObject();
	      if (rs != null) {
	    	  JsonArray hits = new JsonArray();
		      result.setResultObject(hits);
	    	  JsonObject hit;
	    	  while (rs.next()) {
	    		  hit = new JsonObject();
	    		  hit.addProperty("id", rs.getString("id"));
	    		  hit.addProperty("title", rs.getString("title"));
	    		  hit.addProperty("url", rs.getString("url"));
	    		  hits.add(hit);
	    	  }
	      }

	      conn.closeConnection(result);
	    } catch (Exception e) {
	    	Logger.error("PDD-ls "+e.getMessage(), e);
	    }
	  return result;
	}

	@Override
	public 	IResult listEntities(int limit, int offset) {
		IResult result = new ResultPojo();
	    IPostgresConnection conn = null;    
	    try {
	      conn = database.getConnection();
	      String sql = IQueries.listEntities;
	     // System.out.println(sql);
	     // System.out.println(ascDesc+" "+limit+" "+offset);
	      Object [] val = new Object[2];
	      val[0] = Integer.valueOf(limit);
	      val[1] = Integer.valueOf(offset);
	      result = conn.executeSelect(sql, val);
	      if (result.hasError()) 
	    	  Logger.debug(result.getErrorString());
	      ResultSet rs = (ResultSet)result.getResultObject();
	      if (rs != null) {
	    	  JsonArray hits = new JsonArray();
		      result.setResultObject(hits);
	    	  JsonObject hit;
	    	  while (rs.next()) {
	    		  hit = new JsonObject();
	    		  hit.addProperty("id", rs.getString("id"));
	    		  hit.addProperty("title", rs.getString("title"));
	    		  //hit.addProperty("url", rs.getString("url"));
	    		  hit.addProperty("text", rs.getString("body"));
	    		  hit.addProperty("by", rs.getString("author"));
	    		  hits.add(hit);
	    	  }
	      }

	      conn.closeConnection(result);
	    } catch (Exception e) {
	    	Logger.error("PDD-ls "+e.getMessage(), e);
	    }
	  return result;		
	}



}
