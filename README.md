# Akka-Web-Crawler
Async web crawler using akka.

The main crawler acts as a supervisor and spawn LinkCrawler actors to crawl and parse the links.
Link crawler when finished sends the message back to supervisor along with the parsed links.
