/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client.spacy;

import org.topicquests.hn.client.HnClientEnvironment;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import java.io.*;
import java.net.*;
import org.tinylog.Logger;

/**
 * @author jackpark
 *
 */
public class HttpClient {
	protected HnClientEnvironment environment;

	/**
	 * 
	 */
	public HttpClient(HnClientEnvironment env) {
		environment = env;
	}

	/**
	 * Process a sentence {@code queryString}, a JSON string
	 * @param url
	 * @param queryString
	 * @return can return {@code null}
	 */
	public IResult post(String url, String queryString) {
		Logger.debug("HttpClient.put "+url+" | "+queryString);

		IResult result = new ResultPojo();
		BufferedReader rd = null;
		HttpURLConnection con = null;
		OutputStream os = null;
		PrintWriter p = null;
		int counter = 0;
		int code = 0;
		try {
			URL urx = new URL(url);
			while (code != HttpURLConnection.HTTP_OK && counter < 4) {
				Logger.debug("COUNT "+counter + " "+ code);
				try {
					con = (HttpURLConnection) urx.openConnection();
					//con.setReadTimeout(TIMEOUT);
					con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
					con.setRequestMethod("POST");
					con.setDoInput(true);
					con.setDoOutput(true);
					os = con.getOutputStream();
					p = new PrintWriter(os);
					p.print(queryString);
					p.flush();
					p.close();
					code = con.getResponseCode();
					Logger.debug("HttpClient.codeA "+code);
				} catch (Exception x) {
					Logger.error(x.getMessage()+" Count="+counter, x);
					try {
						wait(3000); // short delay
					} catch (Exception xyz) {};
					counter++;
				}
			}
			code = con.getResponseCode();
			Logger.debug("HttpClient.codeB "+code);
			if (code == 200) {
				rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder buf = new StringBuilder();
	
				String line;
				while ((line = rd.readLine()) != null) {
					buf.append(line + '\n');
				}
				result.setResultObject(buf.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.addErrorString(e.getMessage());
			Logger.error("HttpClient "+e.getMessage(), e);

		}
		return result;
	}
}
