/*
 * Copyright 2022 TopicQuests
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.topicquests.hn.rss.spacy;

import org.topicquests.hn.rss.RssEnvironment;
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
	protected RssEnvironment environment;

	/**
	 * 
	 */
	public HttpClient(RssEnvironment env) {
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
