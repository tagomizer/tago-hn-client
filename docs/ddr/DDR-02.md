# DDR-02
Date: 20230527
## Design Decisions
Basically update DDR-01<br/>
We now are able to ping the HN server and populate the database. We are still missing full query functionality on that database, and we do not yet have either a server or a client.<br/>
There is a candidate Vue.js client here:<br/>
[tq-th-vue](https://github.com/KnowledgeGarden/tq-th-vue)<br/>
### Minimum Functionality
Minimum functionality includes ability to:
* Fetch latest HackerNews Stories and comments (Items)
* Index those into a database
* Fetch stories and items from the database
* Create a simple view of those stories
* Perform simple queries on the database

#### Necessary Queries
These must support core Pivot functionality. There will be these pivot topics:
* Stories
* URLs
* Authors
* Tags (eventually)

When any pivot is in view, that sets the tone for other pivots. Generally speaking, when one views all Stories in a scrolling list, one can select a specific story. Doing so gates more pivots

##### Basic Queries
* FetchAllStories
* FetchAllURLS
* FetchAllAuthors
* FetchAllTags (eventually)
##### Supporting Queries
* FetchSpecificStory - with scrolling list of Comments
* ForGivenStory_FetchTags
* ForGivenStory_FetchAuthors
* FetchStoriesByURL (in theory, no duplicates allowed)
* FetchStoriesByAuthor
* FetchCommentsByAuthor
* FetchStoriesByTag (eventually)
* FetchCommentsByTag (eventually)

### Present Architectural Choices
* Java-based  backside engine
* Postgres as the database
* Simple Javascript frontside display OR:
* Java Servlet with server-side html generation

## Justification
The goal is to debug the backside platform.

## Next Steps
- Add NLP to detect named entities and relations - the idea being to use NamedEntities as tags