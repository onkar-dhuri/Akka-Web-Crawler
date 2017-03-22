package com.onkar.crawler

import akka.actor.{ActorSystem, PoisonPill, Props}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by onkar on 3/21/17.
  * Entry point for application
  */
object CrawlerApp extends App {
  val system = ActorSystem("WebCrawler")
  val firstActor = system.actorOf(Props(new CrawlerMain()))

  firstActor ! Crawl("http://akka.io/", 0)

  // wait for 10 minutes to finish crawling
  Await.result(system.whenTerminated, 10 minutes)

  firstActor ! PoisonPill
  system.terminate()
}
