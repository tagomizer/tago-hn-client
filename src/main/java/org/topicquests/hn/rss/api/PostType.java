/**
 * 
 */
package org.topicquests.hn.rss.api;

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
