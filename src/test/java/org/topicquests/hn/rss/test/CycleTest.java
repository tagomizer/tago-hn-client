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
public class CycleTest extends TestingRoot {

	/**
	 * 
	 */
	public CycleTest() {
		super();
		
		IResult r = hnAgent.cycle();
		System.out.println("A "+r.getErrorString());
		System.out.println("B "+r.getResultObject());
		System.exit(0);
	}

}
/*
{
	"by": "password4321",
	"descendants": 0,
	"id": 34467702,
	"score": 9,
	"time": 1674319708,
	"title": "Riot Games: development environment compromised via social engineering",
	"type": "story",
	"url": "https://twitter.com/riotgames/status/1616548651823935488"
}*/