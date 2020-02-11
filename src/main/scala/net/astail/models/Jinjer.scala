package net.astail.models

import scalikejdbc.{DBSession, WrappedResultSet, autoConstruct}
import skinny.orm.{Alias, SkinnyNoIdCRUDMapper}
import scalikejdbc._

case class Jinjer(
  slackUserUid: String,
  companyId: String,
  uid: String,
  pass: String,
)

object Jinjer extends SkinnyNoIdCRUDMapper[Jinjer] {
  override lazy val tableName = "jinjer"
  override lazy val defaultAlias: Alias[Jinjer] = createAlias("jin")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Jinjer]): Jinjer =
    autoConstruct(rs, n)

  def create(slackUserUid: String, companyId: String, uid: String, pass: String)(implicit session: DBSession = autoSession) = {
    Jinjer.createWithNamedValues(
      column.companyId -> companyId,
      column.uid -> uid,
      column.pass -> pass,
      column.slackUserUid -> slackUserUid
    )
  }

  def update(slackUserUid: String, companyId: String, uid: String, pass: String)(implicit session: DBSession = autoSession) = {
    Jinjer.updateBy(sqls.eq(Jinjer.column.slackUserUid, slackUserUid)).withNamedValues(
      column.companyId -> companyId,
      column.uid -> uid,
      column.pass -> pass
    )
  }

  def findByUid(slackUserUid: String)(implicit session: DBSession = autoSession): Option[Jinjer] = {
    findBy(sqls.eq(defaultAlias.slackUserUid, slackUserUid))
  }
}
