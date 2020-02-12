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

      val sendMessage: Option[String] = text match {
        case e if e startsWith "登録" => Some(rdbData.subscribe(e, slackUserUid, separator))
        case "確認" => Some(rdbData.selectUserData(slackUserUid))
        case "削除" => Some(rdbData.deleteUserData(slackUserUid))
        case "出社！" => selenium.jinjer("1", slackUserUid, separator)
        case "退社！" => selenium.jinjer("2", slackUserUid, separator)
        case e if e contains "help" => Some(
          """登録: 自分のデータを登録します / 引数 <companyId> <uid> <pass>
             　　　既にデータがあった場合、上書き処理をします
             確認: 自分のデータを確認します パスワード以外が表示されます
             出社！: 登録しているデータで出社処理をします
             退社！: 登録しているデータで退社処理をします
          """
        )
      }

      if (sendMessage.isDefined) client.sendMessage(channel, s"<@$slackUserUid> ${sendMessage.get}")

      logger.info(s"[sendMessage] channel: ${channel}, sendMessage: ${sendMessage.getOrElse("None")}")
    }
  }
}
