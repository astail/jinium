package net.astail

import org.openqa.selenium.chrome.ChromeDriver

import scala.util.{Failure, Success, Try}

object selenium {

  def jinjer(x: String): String = {

    System.setProperty("webdriver.chrome.driver", "/Users/astel/Desktop/chromedriver80")
    val driver = new ChromeDriver()

    driver.get("https://kintai.jinjer.biz/sign_in")

    val id = driver.findElementById("company_code")
    id.sendKeys("xx")

    val mail = driver.findElementByName("email")
    mail.sendKeys("xxx")

    val password = driver.findElementByName("password")
    password.sendKeys("xxx")


    val loginButton = driver.findElementById("jbtn-login-staff")
    loginButton.click()

    Thread.sleep(5000)


    val css: String = if (x == "出社！")
      "#container > section > main > div.group.groupTimeBtn.cf > div.stampBtn.mt20.cf > ul > li:nth-child(1) > button"
    else if (x == "退社！")
      "#container > section > main > div.group.groupTimeBtn.cf > div.stampBtn.mt20.cf > ul > li:nth-child(2) > button"
    else ""

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
}
