/**
 * 
 */
package org.topicquests.hn.rss.test;

import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class DirectItemTest extends TestingRoot {

	/**
	 * 
	 */
	public DirectItemTest() {
		super();
		
		IResult r = hnClient.getItem("36118976");//36118807");
		System.out.println("A "+r.getErrorString());
		System.out.println("B "+r.getResultObject());
		System.exit(0);
	}

}
