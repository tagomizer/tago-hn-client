/**
 * 
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
