package cn.orz.pascal.scala.ebooksearch.utils
// vim: set ts=2 sw=2 et:

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar

object DateUtils {
  def date(year: Int, month: Int, date: Int): Date = {
    val format = new SimpleDateFormat("yyyyMMdd")
    format.setLenient(false)
    format.parse("%d%2d%2d" format (year, month, date))
  }
  
  def today() = {
    import java.util.Calendar._
    val calendar = Calendar.getInstance()
    calendar.setLenient(false)
    
    calendar.set(HOUR_OF_DAY, 0)
    calendar.set(MINUTE, 0)
    calendar.set(SECOND, 0)
    calendar.set(MILLISECOND, 0)
    
    calendar.getTime()
  }
  
  class RichDate(date: Date) {
    import java.util.Calendar._
    val calendar = Calendar.getInstance()
    calendar.setLenient(false)
    calendar.setTime(date)
    
    def year = calendar.get(YEAR)
    def month = calendar.get(MONTH) + 1
    def date = calendar.get(DATE)
    def hour = calendar.get(HOUR_OF_DAY)
    def minute = calendar.get(MINUTE)
    def second = calendar.get(SECOND)
  }
  implicit def date2richdate(date: Date) = new RichDate(date)
}



