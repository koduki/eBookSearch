package cn.orz.pascal.scala.ebooksearch.batch

// vim: set ts=2 sw=2 et:
import javax.servlet.http.HttpServlet
import commonj.timers.Timer
import commonj.timers.TimerListener;
import commonj.timers.TimerManager;
import java.io._
import java.net._
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet._
import javax.servlet.http._

class TaskScheduler extends HttpServlet {
  var myTimerManager:TimerManager = null;

  override def init() {
    println
    println("hello cron3")  
    println

    val ic = new InitialContext();
    println(ic)
    println("testei")
    try{
    this.myTimerManager = ic.lookup("java:comp/env/tm/MyTimerManager").asInstanceOf[de.myfoo.commonj.timers.FooTimerManagerFactory].getObjectInstance(null, null, null, null).asInstanceOf[TimerManager]
  //  this.myTimerManager = ic.lookup("java:comp/env/jdbc/db").asInstanceOf[TimerManager]
    println("teste2")
    myTimerManager.schedule(new MyTimerListener(), 0, 1000);
    println("TimerServlet: タイマーがスケジュールされました.");
  }catch{
    case e:Exception => e.printStackTrace
  
  }

  }
}

class MyTimerListener extends TimerListener {
  override def timerExpired(timer:Timer) {
    println("run task.") 
  }
}
