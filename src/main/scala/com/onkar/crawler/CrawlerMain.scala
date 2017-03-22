package com.onkar.crawler

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by onkar on 3/21/17.
  * Main crawler class/Supervisor for LinkCrawler actors
  */
case class Crawl(url: String, level: Int)

case class Crawled(url: String, linksFound: Set[String], level: Int)

class CrawlerMain extends Actor {
  val maxCrawl = 50
  val maxDepth = 2
  var crawledCounter = 0
  var crawlList = collection.mutable.Set.empty[String]

  override def receive = {

    case Crawl(url: String, level: Int) => {
      val linkCrawler = context.actorOf(Props(new LinkCrawler(self)))
      linkCrawler ! index(url, level)
      crawlList += url
    }

    case Crawled(url: String, linksFound: Set[String], level: Int) => {
      crawledCounter += 1
      println(s"Level : $level ; Crawled : $crawledCounter - $url and found ${(linksFound -- crawlList).size} new links")

      if (crawlList.size < maxCrawl && crawledCounter < maxCrawl && level < maxDepth) {
        if ((linksFound -- crawlList).size > 1) {
          (linksFound -- crawlList).map(url => {
            self ! Crawl(url, level + 1)
          })
          println(s"Started crawling ${(linksFound -- crawlList).size} \n (${(linksFound -- crawlList)}) \n with currentCrawlList - ${crawlList.size}")
          crawlList ++= linksFound
        }
      }
    }

    case _ => {
      println("Default case")
    }
  }
}

