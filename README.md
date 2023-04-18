# Implementation details

### APIs used
* [Notion SDK JVM API](https://github.com/seratch/notion-sdk-jvm) - was a big PITA to use, written in kotlin, poor documentation, much of the implementation time was about figurng this one out
* [Discord4J API](https://discord4j.com/) - worked like a charm for this use case
* [Lombok](https://projectlombok.org/) - to reduce some boiler plate

### Application info

* Discord channel where posts are run: https://discord.com/channels/1091327559599407145/1091327560069156876 
* Configuration is in the application.properties file
* The following JVM enviroment variables need to be added to the run configuration for compatibility of the notion-sdk with the java17: --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.https=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED
* Task runs every at every query-interval, gets all the posts from the Notion table, selects the ones to be posted in the next interval posts them and changes their status
* Exception handling / null checks when using the notion / discord apis is not extensive and I don't think it was in the scope of the project. Complete handling of all the edge cases would have taken quite a bit of time

### Quiz answers

* _Question 1 (Elasticity): Assume that Notion2Social should serve many thousands of
  customers as a SaaS tool. What changes need to be done to the architecture to guarantee
  low response times and high availability? (Focus on your implementation)_
  * separate the app into two microservices
    * a Master/Controller Service one which run's the schedueled job for the map of NotionTables -> SocialChannels and calls REST calls to the Poster Service
    * a Poster microservice which queries the database and calls apropriate channel REST endpoints
  * containerize the app using Docker and integrate it a kubernetes kluster
  * as the Master/Controller Service is not cpu/resource intesive one deployment / pod would suffice
  * scale the Poster microservice automatically when limits are reached
* _Question 2 (Race Conditions): Assume that Notion2Social is architectured as a
  microservice system and Notion updates are triggered by each service internally (e.g. query
  all posts which need to be published and publish them). How can you prevent race
  conditions in this scenario and what tools/architecture would you choose?_
    * Apache Kafka could be used in order to handle / consume Post Events by the Poster Microservice
* _Question 3 (API Limits and Databases): Assume Notion has a query API limit which
  restricts queries to 1 query per hour. Further assume that the scheduled date of notion posts
  is not changed when initially set and they are scheduled at least 1 hour in future. How can
  you guarantee that all posts are still sent at the exact publish time (e.g. 1:36PM)?_
    * if the requirement changes and if posts need to be posted at the exact publishing time then the architecture of the app needs to be changed:
      * break down the Poster microservice into a Notion Microserver and a Poster Microservice
      * The Notion Microservice queries the database once every hour and extracts the posts which should be posted in the next hour
      * These posts are then submitted to Kafka, and each event / post would have the desired posting time set
      * Delay the posting of these events on the Consumer Side which in this case would be a Poster Microservice which just handles the posting of these services the social media channels
* _Question 4 (Monitoring): Assume the Notion API or one of the social APIs are down and
  throwing errors while calling their API. How would you integrate Monitoring into the systems
  so that such scenarios are recognized early. Which tools would you choose??_
    * Apache Kafka and the log aggregation / monitoring part of it could also be used in order to scan / detect this scenarios
