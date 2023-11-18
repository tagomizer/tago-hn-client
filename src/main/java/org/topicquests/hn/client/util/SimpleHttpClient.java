/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client.util;


import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @author jackpark
 *
 */
public class SimpleHttpClient {
	private int TIMEOUT = 10000;
	
	public SimpleHttpClient() {
	}

	public void setTimeout(int t) {
		TIMEOUT = t;
	}
	
	public IResult put(String url, String queryString) {
		return this.handle(url, queryString, "POST");
	}

	public IResult get(String url, String queryString) {
		return this.handle(url, queryString, "GET");
	}

	private IResult handle(String url, String queryString, String mode) {
		IResult result = new ResultPojo();
		BufferedReader rd = null;
		HttpURLConnection con = null;

		try {
			URL urx = new URL(url + queryString);
			con = (HttpURLConnection) urx.openConnection();
			con.setReadTimeout(TIMEOUT);
			con.setRequestMethod(mode);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			int code = con.getResponseCode();
			int counter = 0;
			while (counter++ < 10) {
				if (code != 200) {
					TimeUnit.SECONDS.sleep(2);
					code = con.getResponseCode();
				}
			}
			if (code == 200) {
				rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder buf = new StringBuilder();
	
				String line;
				while ((line = rd.readLine()) != null) {
					buf.append(line + '\n');
				}
	
				result.setResultObject(buf.toString());
			} else {
				result.setResultObject(null);
				result.addErrorString("Bad response "+code+" on "+url);
			}
		} catch (Exception var18) {
			var18.printStackTrace();
			result.addErrorString(var18.getMessage());
		} finally {
			try {
				if (rd != null) {
					rd.close();
				}

				if (con != null) {
					con.disconnect();
				}
			} catch (Exception var17) {
				var17.printStackTrace();
				result.addErrorString(var17.getMessage());
			}

		}

		return result;
	}
}