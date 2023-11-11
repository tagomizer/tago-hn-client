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
public class FirstTreeTest extends TestingRoot {

	/**
	 * 
	 */
	public FirstTreeTest() {
		super();
		IResult r = database.getRecursive("59");
		System.out.println("A "+r.getErrorString());
		ITreeNode n = (ITreeNode)r.getResultObject();
		System.out.println("B "+n.getData());
		environment.logDebug("BIGRESULT\n"+n.getData());
		
		environment.shutDown();
		System.exit(0);

	}

}
