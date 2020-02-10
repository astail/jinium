package net.astail

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}
import slack.models.Message
import slack.rtm.SlackRtmClient

object Main {
  def main(args: Array[String]): Unit = {
    val logger: Logger = LoggerFactory.getLogger(this.getClass)

    logger.info("start app")

    val token = ConfigFactory.load.getString("slack_jinium_token")

    implicit val system = ActorSystem("slack")
    implicit val ec = system.dispatcher

    val client = SlackRtmClient(token)

    client.onMessage { message =>

      val channel: String = message.channel
      val text = message.text
      val user = message.user

      logger.info(s"[getMessage] channel: ${channel}, text: ${text}, user: ${user}")

      if (user == "U054X0P0V") {

        val sendMessage = text match {
          case "出社！" => selenium.jinjer("出社！")
          case "退社！" => selenium.jinjer("退社！")
        }

        client.sendMessage(channel, sendMessage)
        logger.info(s"[sendMessage] channel: ${channel}, sendMessage: ${sendMessage}")
      }
    }
  }
}
