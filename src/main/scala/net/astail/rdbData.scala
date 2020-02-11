package net.astail

import scala.util.{Failure, Success, Try}

object rdbData {

  def int2bool(i: Int) = if (i == 1) true else false

  def subscribe(m: String, channel: String, slackUserUid: String, separator: String) = {
    val tmp = m.replaceAll("\u3000", " ")
    val saveData = tmp split "[ ]"

    val checkSlackUser = models.SlackUser.findByUid(slackUserUid)
    val checkJinjer = models.Jinjer.findByUid(slackUserUid)

    val (companyId, uid, pass) = (saveData(1), saveData(2), saveData(3))

    val ePass = crypto.encryptString(pass, separator)

    (checkSlackUser, checkJinjer) match {
      case (Some(x), Some(y)) =>
        val t = models.Jinjer.update(slackUserUid, companyId, uid, ePass)
        val result = if (int2bool(t)) "更新成功" else "更新失敗"
        "既にデータがあったので上書き処理しました " + result
      case (None, None) =>
        val t = Try {
          models.SlackUser.create(slackUserUid, channel)
          models.Jinjer.create(slackUserUid, companyId, uid, ePass)
        }
        t match {
          case Success(x) => "user,jinjer登録しました"
          case Failure(_) => "user,jinjer登録失敗"
        }
      case (_, _) => "データが壊れている可能性があります"
    }
  }

  def deleteUserData(slackUserUid: String) = {
    val t = models.SlackUser.deleteByUid(slackUserUid)
    if (int2bool(t)) "削除成功" else "削除失敗"
  }
}
