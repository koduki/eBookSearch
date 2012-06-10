// vim: set ts=4 sw=4 et:
package cn.orz.pascal.commons.utils
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class ISBNTest extends WordSpec with ShouldMatchers {

  "ISBN13(4844330845)" should {
    "be 9784844330844" in {
      ISBN.to13("4844330845") should be("9784844330844")
    }
  }

  "ISBN13(4873114810)" should {
    "be 9784873114811" in {
      ISBN.to13("4873114810") should be("9784873114811")
    }
  }

}
