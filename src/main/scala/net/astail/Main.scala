package net.astail

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}
import slack.rtm.SlackRtmClient
import scala.util.{Failure, Success, Try}

object Main {
  def main(args: Array[String]): Unit = {
    val logger: Logger = LoggerFactory.getLogger(this.getClass)
    skinny.DBSettings.initialize()

    logger.info("start app")

    val token = ConfigFactory.load.getString("slack_jinium_token")

    implicit val system = ActorSystem("slack")
    implicit val ec = system.dispatcher

    val client = SlackRtmClient(token)

    client.onMessage { message =>

      val channel: String = message.channel
      val text = message.text
      val slackUserUid: String = message.user

      logger.info(s"[getMessage] channel: ${channel}, text: ${text}, user: ${slackUserUid}")

      def int2bool(i: Int) = if (i == 1) true else false

      val sendMessage = text match {
        case e if e startsWith "登録" => {
          val tmp = e.replaceAll("\u3000", " ")
          val saveData = tmp split "[ ]"

          val checkSlackUser = models.SlackUser.findByUid(slackUserUid)
          val checkJinjer = models.Jinjer.findByUid(slackUserUid)

          (checkSlackUser, checkJinjer) match {
            case (Some(x), Some(y)) =>
              val t = models.Jinjer.update(slackUserUid, saveData(1), saveData(2), saveData(3))
              val b = if (int2bool(t)) "更新成功" else "更新失敗"
              "既にデータがあったので上書き処理しました " + b
            case (Some(x), None) =>
              val t = Try {
                models.Jinjer.create(slackUserUid, saveData(1), saveData(2), saveData(3))
              }
              t match {
                case Success(x) => "jinjerのデータを登録しました"
                case Failure(_) => "jinjerのデータを登録失敗"
              }
            case (None, None) =>
              val t = Try {
                models.SlackUser.create(slackUserUid, channel)
                models.Jinjer.create(slackUserUid, saveData(1), saveData(2), saveData(3))
              }
              t match {
                case Success(x) => "データを登録しました"
                case Failure(_) => "データを登録失敗"
              }
          }

        }
        case "出社！" => selenium.jinjer("出社！")
        case "退社！" => selenium.jinjer("退社！")
      }

      client.sendMessage(channel, sendMessage)
      logger.info(s"[sendMessage] channel: ${channel}, sendMessage: ${sendMessage}")

    }
  }
}
