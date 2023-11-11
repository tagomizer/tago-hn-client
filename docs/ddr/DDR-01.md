# DDR-01
Date: 20220703
## Design Decisions
### Minimum Functionality
Minimum functionality includes ability to:
* Fetch latest HackerNews Stories and comments (Items)
* Index those into a database
* Fetch stories and items from the database
* Create a simple view of those stories
* Perform simple queries on the database

### Present Architectural Choices
* Java-based  backside engine
* Elasticsearch as the database
* Simple Javascript frontside display

## Justification
The goal is to debug the backside platform.

## Next Steps
Goals are these:
- Create a TopicMap view engine which organizes around the simple concepts:URLs and Authors
- Add NLP to detect named entities and relations

