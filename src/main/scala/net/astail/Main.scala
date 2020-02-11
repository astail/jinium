package net.astail

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}
import slack.rtm.SlackRtmClient

object Main {
  def main(args: Array[String]): Unit = {

    val logger: Logger = LoggerFactory.getLogger(this.getClass)
    skinny.DBSettings.initialize()

    logger.info("start app")

    val token = ConfigFactory.load.getString("slack_jinium_token")
    val separator = "eriohaorenoyome"

    implicit val system = ActorSystem("slack")
    implicit val ec = system.dispatcher

    val client = SlackRtmClient(token)

    client.onMessage { message =>

      val channel: String = message.channel
      val text = message.text
      val slackUserUid: String = message.user

      logger.info(s"[getMessage] channel: ${channel}, text: ${text}, user: ${slackUserUid}")

      val sendMessage = text match {
        case e if e startsWith "登録" => rdbData.subscribe(e, channel, slackUserUid, separator)
        case "削除" => rdbData.deleteUserData(slackUserUid)
        case "出社！" => selenium.jinjer("1", slackUserUid, separator)
        case "退社！" => selenium.jinjer("2", slackUserUid, separator)
      }

      client.sendMessage(channel, sendMessage)
      logger.info(s"[sendMessage] channel: ${channel}, sendMessage: ${sendMessage}")
    }
  }
}
