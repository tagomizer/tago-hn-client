/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hn.client.api;

/**
 * @author jackpark
 * @see https://stackoverflow.com/questions/40356750/how-do-i-make-java-postgres-enums-work-together-for-update
 */
public enum PostType {
	story,
    comment,
    job,
    poll,  // will have PARTS_FIELD
    pollopt,
    tag
}
