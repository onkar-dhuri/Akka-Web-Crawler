package com.onkar.crawler

import akka.actor.{Actor, ActorRef}
import org.jsoup.Jsoup

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by onkar on 3/21/17.
  * Worker actors that crawl and parse the webpage and returns the set of links found
  */
case class index(url: String, level: Int)

class LinkCrawler(actorRef: ActorRef) extends Actor {

  val validURL = "^(http|https|ftp)\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~])*$".r

  override def receive = {
    case index(url: String, level: Int) => {
      println(s"Crawling - $url")
      Future {
        Jsoup.connect(url).ignoreContentType(true).execute()
      }.onComplete {
        case Success(response) => {
          if (response.toString().length() > 1) {
            val doc = response.parse()
            val links = doc.getElementsByTag("a").asScala.map(e => e.attr("href"))
              .filter(link => validURL.findFirstIn(link).isDefined).toSet
            actorRef ! Crawled(url, links, level)

          }
        }
        case Failure(e) => println(s"Exception while crawling $url")
      }
    }
  }
}
