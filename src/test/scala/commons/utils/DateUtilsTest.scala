package cn.orz.pascal.scala.commons.utils
// vim: set ts=4 sw=4 et:
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import java.text.SimpleDateFormat
import java.text.ParseException
import java.util.Calendar

class DateUtilsTest extends WordSpec with ShouldMatchers {
  import DateUtils._
  "date" should {
    "date '2011,12,1'" in {
      date(2011, 12, 1) should be(new SimpleDateFormat("yyyyMMdd") parse ("20111201"))
    }
    "date '2012,2,25'" in {
      date(2012, 2, 25) should be(new SimpleDateFormat("yyyyMMdd") parse ("20120225"))
    }

    "throw ParseException if date is '2012,2,31'" in {
      intercept[ParseException] {
        date(2012, 2, 31)
      }
    }
  }

  "RichDate(2011,12,1)" should {
    val d = date(2011, 12, 1)
    "year is 2011" in {
      d.year should be(2011)
    }
    "month is 12" in {
      d.month should be(12)
    }
    "date is 1" in {
      d.date should be(1)
    }
    "hour is 0" in {
      d.hour should be(0)
    }
    "minute is 0" in {
      d.minute should be(0)
    }
    "second is 0" in {
      d.second should be(0)
    }

  }

  "today" should {
    "year is current year" in {
      today.year should be(Calendar.getInstance.get(Calendar.YEAR))
    }
    "month is current month" in {
      today.month should be(Calendar.getInstance.get(Calendar.MONTH) + 1)
    }
    "date is current date" in {
      today.date should be(Calendar.getInstance.get(Calendar.DATE))
    }
    "hour is 0" in {
      today.hour should be(0)
    }
    "munute is 0" in {
      today.minute should be(0)
    }
    "second is 0" in {
      today.second should be(0)
    }
  }
}

