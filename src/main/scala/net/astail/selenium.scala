package net.astail

import org.openqa.selenium.chrome.ChromeDriver

import scala.util.{Failure, Success, Try}

object selenium {

  def jinjer(x: String, slackUserUid: String, separator: String): Option[String] = {

    val data = models.Jinjer.findByUid(slackUserUid)

    if (data.isDefined) {
      val (companyId, uid, pass) = (data.get.companyId, data.get.uid, data.get.pass)
      val dPass = crypto.decryptString(pass, separator)

      val result = dPass match {
        case Some(p) => {

          System.setProperty("webdriver.chrome.driver", "/Users/astel/Desktop/chromedriver80")
          val driver = new ChromeDriver()

          driver.get("https://kintai.jinjer.biz/sign_in")

          val id = driver.findElementById("company_code")
          id.sendKeys(companyId)

          val mail = driver.findElementByName("email")
          mail.sendKeys(uid)

          val password = driver.findElementByName("password")
          password.sendKeys(p)


          val loginButton = driver.findElementById("jbtn-login-staff")
          loginButton.click()

          Thread.sleep(5000)

          //li:nth-child(1)が出社ボタン、li:nth-child(2)が退社ボタン
          val css: String = s"#container > section > main > div.group.groupTimeBtn.cf > div.stampBtn.mt20.cf > ul > li:nth-child($x) > button"

          val t = Try {
            driver.findElementByCssSelector(css).click()
          }

          driver.close()
          driver.quit()

          t match {
            case Success(x) => "成功です"
            case Failure(_) => "既に出社済みか失敗です"
          }
        }
        case None => "復号化失敗しました"
      }
      Some(result)
    } else None
  }
}
