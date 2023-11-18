/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client;

import java.util.List;

import org.topicquests.hn.client.api.IBacksideDatabase;
import org.topicquests.hn.client.api.IDSL;
import org.topicquests.hn.client.nlp.DatabaseHarvester;
import org.topicquests.hn.client.nlp.NodeStudyThread;
import org.topicquests.hn.client.persist.PostgresDbDriver;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.support.RootEnvironment;

import net.minidev.json.JSONObject;
import org.tinylog.Logger;
import org.tinylog.provider.ProviderRegistry;
/**
 * @author jackpark
 *
 */
public class HnClientEnvironment extends RootEnvironment {
	private HnHttpClient hnClient;
	private HnAgent hnAgent;
	private JSONObject clientMappings;
	private IDSL dsl;
	private PostgresConnectionFactory dbDriver = null;
	private IBacksideDatabase database;
	private NodeStudyThread studyThread;
	private DatabaseHarvester nlp;
	/**
	 */
	public HnClientEnvironment() {
		super("rss-config.xml");
		String schemaName = getStringProperty("DatabaseSchema");
		String dbName = getStringProperty("DatabaseName");
		dbDriver = new PostgresConnectionFactory(dbName, schemaName);

		hnClient = new HnHttpClient(this);
		database = new PostgresDbDriver(this);
		hnAgent = new HnAgent(this);
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

	public HnAgent getHnAgent() {
		return hnAgent;
	}
	public HnHttpClient getHnClient() {
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
