/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.rss.test;

import org.topicquests.hn.client.spacy.SpacyHttpClient;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 * @see https://github.com/ninia/jep/wiki
 * pip3 install jep
 */
public class PythonTest extends TestingRoot {
	private SpacyHttpClient client;
	private final String
	txt4 = "The three fruits were mixed to form a supercoagulant (a milk coagulant mixture of the extracts at a ratio of 1:1:1), and the milk coagulation time was measured. The milk was coagulated by the supercoagulant, and thus fortified curd was tested for its ability to inhibit α-glucosidase and α-amylase activities. Then, the fortified curd was fed daily to streptozotocin-induced diabetic rats and their biochemical markers such as blood glucose level, aspartate aminotransferase, alanine transaminase, etc. as well as histopathology of their liver and kidney tissues were compared with the untreated diabetic rats and normal rats."
;
	/**
	 * 
	 */
	public PythonTest() {
		super();
		client = new SpacyHttpClient(environment);
		IResult r = client.processSentence(txt4);
		System.out.println("A "+r.getErrorString());
		System.out.println("B "+r.getResultObject());
		
		environment.shutDown();
		System.exit(0);
		
	}

}
/*
 * text4
 A 
B {"data":["The three fruits","a supercoagulant","a milk coagulant mixture","the extracts","a ratio","the milk coagulation time","The milk","the supercoagulant","thus fortified curd","its ability","α-glucosidase","α-amylase activities","the fortified curd","streptozotocin-induced diabetic rats","their biochemical markers","blood glucose level","aspartate aminotransferase","alanine transaminase","histopathology","their liver and kidney tissues","the untreated diabetic rats","normal rats"]}

 * 
 */
