/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
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
