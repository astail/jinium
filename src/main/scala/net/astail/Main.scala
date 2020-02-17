package net.astail

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}
import slack.rtm.SlackRtmClient

object Main {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    logger.info("start app")
    skinny.DBSettings.initialize()
    val longHash: String = net.astail.Git.longHash
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
      val startM = Seq("仕事開始！", "出勤！", "出社！", ":startwork:", "開店", "始業")
      val endM = Seq("仕事終了！", "退勤！", "退社！", ":endwork:", "閉店", "終業", "終わり", "帰る", ":end:")

      val sendMessage: Option[String] = text match {
        case e if e startsWith "登録" => Some(rdbData.subscribe(e, slackUserUid, separator))
        case "確認" => Some(rdbData.selectUserData(slackUserUid))
        case "削除" => Some(rdbData.deleteUserData(slackUserUid))
        case t if startM.exists(t.contains(_)) => selenium.jinjer("1", slackUserUid, separator)
        case t if endM.exists(t.contains(_)) => selenium.jinjer("2", slackUserUid, separator)
        case "version" => Some(longHash)
        case "help" => Some(
          s"""登録: 自分のデータを登録します(botとDMでやってください) / 引数 <companyId> <uid> <pass>
             　　　既にデータがあった場合、上書き処理をします
             確認: 自分のデータを確認します パスワード以外が表示されます
             削除: 自分の登録したデータを削除します
             ${startM.mkString(",")}: 登録しているデータで出社処理をします
             ${endM.mkString(",")}: 登録しているデータで退社処理をします
          """
        )
        case _ => None
      }

      if (sendMessage.isDefined) client.sendMessage(channel, s"<@$slackUserUid> ${sendMessage.get}")

      logger.info(s"[sendMessage] channel: ${channel}, sendMessage: ${sendMessage.getOrElse("None")}")
    }
  }
}
