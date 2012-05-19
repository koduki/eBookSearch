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
    "date '2012,2,25 1:20'" in {
      date(2012, 2, 25, 1, 20) should be(new SimpleDateFormat("yyyyMMddHHmm") parse ("201202250120"))
    }
    "date '2012,3,25 21:20:30'" in {
      date(2012, 3, 25, 21, 20, 30) should be(new SimpleDateFormat("yyyyMMddHHmmss") parse ("20120325212030"))
    }
    "throw ParseException if date is '2012,2,31'" in {
      intercept[ParseException] {
        date(2012, 2, 31)
      }
    }
  }

  "RichDate" when {
    "(2011,12,1)" should {
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

    "(2011,12,1)" should {
      val d = date(2011, 12, 1)
      "> (2012,12,1) is false" in {
        d > date(2012, 12, 1) should be(false)
      }
      "< (2012,12,1) is ture" in {
        d < date(2012, 12, 1) should be(true)
      }
      ">= (2011,12,1) is true" in {
        d >= date(2011, 12, 1) should be(true)
      }
      "<= (2010,12,1) is false" in {
        d <= date(2010, 12, 1) should be(false)
      }

    }
  }

  "dateTrim" when {
    "2012-11-03 04:05:11" should {
      "be 2012-11-03 00:00:00" in {
        dateTrim(date(2012, 11, 3, 4, 5, 11)) should be(date(2012, 11, 03))
      }
    }
    "2012-4-01 14:05:11" should {
      "be 2012-04-01 00:00:00" in {
        dateTrim(date(2012, 4, 1, 14, 5, 11)) should be(date(2012, 4, 1))
      }
    }
  }

}

