/**
 * 
 */
package org.topicquests.hn.rss.test;

import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class ListStoryTest extends TestingRoot {

	/**
	 * 
	 */
	public ListStoryTest() {
		super();
		IResult r = database.listStories(5, 0);
		System.out.println("A "+r.getErrorString());
		System.out.println("B "+r.getResultObject());
		
		environment.shutDown();
		System.exit(0);

	}

}
