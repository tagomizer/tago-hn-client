/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client;

/**
 * @author jackpark
 *
 */
public class Test {
	protected HnClientEnvironment environment;
	protected HnAgent hnAgent;

	public Test() {
		environment = new HnClientEnvironment();
		hnAgent = environment.getHnAgent();
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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Test();
	}

}
