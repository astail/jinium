package net.astail.models

import scalikejdbc.{DBSession, WrappedResultSet, autoConstruct}
import skinny.orm.{Alias, SkinnyNoIdCRUDMapper}
import scalikejdbc._

case class SlackUser(
  uid: String,
  jinjer: Option[Jinjer] = None
)

object SlackUser extends SkinnyNoIdCRUDMapper[SlackUser] {
  override lazy val tableName = "slack_users"
  override lazy val defaultAlias: Alias[SlackUser] = createAlias("slu")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[SlackUser]): SlackUser =
    autoConstruct(rs, n, "jinjer")

  def create(uid: String)(implicit session: DBSession = autoSession) = {
    SlackUser.createWithNamedValues(
      column.uid -> uid
    )
  }

  lazy val jinjerRef = belongsToWithFkAndJoinCondition[Jinjer](
    right = Jinjer,
    fk = "uid",
    on = sqls.eq(defaultAlias.uid, Jinjer.defaultAlias.slackUserUid),
    merge = (sl, jin) => sl.copy(jinjer = jin)
  )

  def findByUid(uid: String)(implicit session: DBSession = autoSession): Option[SlackUser] = {
    SlackUser.joins(jinjerRef).findBy(sqls.eq(defaultAlias.uid, uid))
  }

  def deleteByUid(uid: String)(implicit session: DBSession = autoSession) = {
    SlackUser.deleteBy(sqls.eq(SlackUser.column.uid, uid)) & Jinjer.deleteBy(sqls.eq(Jinjer.column.slackUserUid, uid))
  }
}
