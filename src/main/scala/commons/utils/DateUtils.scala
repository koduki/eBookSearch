package cn.orz.pascal.commons.utils
// vim: set ts=2 sw=2 et:

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar

object DateUtils {
  def date(year: Int, month: Int, date: Int, hour: Int = 0, minute: Int = 0, second: Int = 0): Date = {
    val format = new SimpleDateFormat("yyyyMMddHHmmss")
    format.setLenient(false)
    format.parse("%d%2d%2d%2d%2d%2d" format (year, month, date, hour, minute, second))
  }

  def today() = {
    import java.util.Calendar._
    dateTrim(Calendar.getInstance().getTime())
  }

  def dateTrim(target: Date) = {
    import java.util.Calendar._
    val calendar = Calendar.getInstance()
    calendar.setLenient(false)
    calendar.setTime(target)

    calendar.set(HOUR_OF_DAY, 0)
    calendar.set(MINUTE, 0)
    calendar.set(SECOND, 0)
    calendar.set(MILLISECOND, 0)

    calendar.getTime()
  }

  class RichDate(baseDate: Date) extends Ordered[RichDate] {
    import java.util.Calendar._
    val calendar = Calendar.getInstance()
    calendar.setLenient(false)
    calendar.setTime(baseDate)

    def year = calendar.get(YEAR)
    def month = calendar.get(MONTH) + 1
    def date = calendar.get(DATE)
    def hour = calendar.get(HOUR_OF_DAY)
    def minute = calendar.get(MINUTE)
    def second = calendar.get(SECOND)

    def compare(that: RichDate) = this.calendar.getTime().compareTo(that.calendar.getTime())
  }
  implicit def date2richdate(baseDate: Date) = new RichDate(baseDate)
}



