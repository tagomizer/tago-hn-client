/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.rss.test;

/**
 * @author jackpark
 *
 */
public class ChronTest extends TestingRoot {

	/**
	 * 
	 */
	public ChronTest() {
		super();
		Runtime.getRuntime().addShutdownHook(new Thread()
	    {
	      public void run()
	      {
	        System.out.println("Shutdown Hook is running !");
	        hnAgent.stop(); // cycle every hour
	       }
	    });
        hnAgent.chronJob(); // cycle every hour	      

	}

}
