/**
 * 
 */
package org.topicquests.hn.rss;

/**
 * @author jackpark
 *
 */
public class Test {
	protected RssEnvironment environment;
	protected RssHnAgent hnAgent;

	public Test() {
		environment = new RssEnvironment();
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
