/**
 * 
 */
package org.topicquests.hn.rss.test;

import org.topicquests.hn.rss.api.ITreeNode;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class ObjectFetchTest extends TestingRoot {

	/**
	 * 
	 */
	public ObjectFetchTest() {
		super();
		IResult r = database.getObject("45"); //41");
		System.out.println("A "+r.getErrorString());
		ITreeNode n = (ITreeNode)r.getResultObject();
		System.out.println("B "+n.getData());
		
		environment.shutDown();
		System.exit(0);
	}

}
