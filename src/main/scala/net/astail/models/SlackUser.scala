package net.astail.models

import scalikejdbc.{DBSession, WrappedResultSet, autoConstruct}
import skinny.orm.feature.associations.HasOneAssociation
import skinny.orm.{Alias, SkinnyNoIdCRUDMapper}
import scalikejdbc._

case class SlackUser(
  uid: String,
  channel: String,
  jinjer: Option[Jinjer] = None
)

object SlackUser extends SkinnyNoIdCRUDMapper[SlackUser] {
  override lazy val tableName = "slack_users"
  override lazy val defaultAlias: Alias[SlackUser] = createAlias("slu")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[SlackUser]): SlackUser =
    autoConstruct(rs, n, "jinjer")

  def create(uid: String, channel: String)(implicit session: DBSession = autoSession) = {
    SlackUser.createWithNamedValues(
      column.uid -> uid,
      column.channel -> channel)
  }

  lazy val jinjerRef: HasOneAssociation[SlackUser] = hasOne[Jinjer](
    right = Jinjer,
    merge = (sl, jin) => sl.copy(jinjer = jin)
  )

  def findByUid(uid: String)(implicit session: DBSession = autoSession): Option[SlackUser] = {
    findBy(sqls.eq(defaultAlias.uid, uid))
  }

  def deleteByUid(uid: String)(implicit session: DBSession = autoSession) = {
    SlackUser.deleteBy(sqls.eq(SlackUser.column.uid, uid)) & Jinjer.deleteBy(sqls.eq(Jinjer.column.slackUserUid, uid))
  }
}
