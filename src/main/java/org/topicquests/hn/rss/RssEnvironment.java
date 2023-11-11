/**
 * 
 */
package org.topicquests.hn.rss;

import java.util.List;

import org.topicquests.hn.rss.api.IDSL;
import org.topicquests.hn.rss.nlp.DatabaseHarvester;
import org.topicquests.hn.rss.nlp.NodeStudyThread;
import org.topicquests.hn.rss.persist.PostgresDbDriver;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.support.RootEnvironment;
import org.topicquests.hn.rss.api.IBacksideDatabase;

import net.minidev.json.JSONObject;
import org.tinylog.Logger;
import org.tinylog.provider.ProviderRegistry;
/**
 * @author jackpark
 *
 */
public class RssEnvironment extends RootEnvironment {
	private RssHnClient hnClient;
	private RssHnAgent hnAgent;
	private JSONObject clientMappings;
	private IDSL dsl;
	private PostgresConnectionFactory dbDriver = null;
	private IBacksideDatabase database;
	private NodeStudyThread studyThread;
	private DatabaseHarvester nlp;
	/**
	 */
	public RssEnvironment() {
		super("rss-config.xml");
		String schemaName = getStringProperty("DatabaseSchema");
		String dbName = getStringProperty("DatabaseName");
		dbDriver = new PostgresConnectionFactory(dbName, schemaName);

		hnClient = new RssHnClient(this);
		database = new PostgresDbDriver(this);
		hnAgent = new RssHnAgent(this);
		studyThread = new NodeStudyThread(this);
		nlp = new DatabaseHarvester(this);
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		    	studyThread.shutDown();
		    	try {
		    		ProviderRegistry.getLoggingProvider().shutdown();
		    	} catch (Exception e) {e.printStackTrace();}
		    }
		});
		Logger.debug("Starting");
	}
	
	public DatabaseHarvester getHarvester() {
		return nlp;
	}
	
	public NodeStudyThread getStudyThread() {
		return studyThread;
	}
	public IBacksideDatabase getDatabase() {
		return database;
	}
	public PostgresConnectionFactory getPostgresFactory() {
		return dbDriver;
	}

	public RssHnAgent getHnAgent() {
		return hnAgent;
	}
	public RssHnClient getHnClient() {
		return hnClient;
	}
	
	
	public String getIndexName() {
		List<List<String>>l = (List<List<String>>)super.getProperty("IndexNames");
		List<String>ls = l.get(0);
		return ls.get(0);
	}
	
	@Override
	public void shutDown() {
		studyThread.shutDown();

	}

}
