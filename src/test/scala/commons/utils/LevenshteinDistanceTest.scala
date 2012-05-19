// vim: set ts=4 sw=4 et:
package cn.orz.pascal.commons.utils
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class LevenshteinDistanceTest extends WordSpec with ShouldMatchers {

  "這いよれ！　ニャル子さん4" when {
    val str1 = "這いよれ！　ニャル子さん4"
    "這いよれ！ニャル子さん 9 (GA文庫)" should {
      "return 11" in {
        LevenshteinDistance(str1, "這いよれ！ニャル子さん 9 (GA文庫)") should be(11)
      }
    }
    "這いよれ！ニャル子さん 4 (GA文庫)" should {
      "return 9" in {
        LevenshteinDistance(str1, "這いよれ！ニャル子さん 4 (GA文庫)") should be(9)
      }
    }
    "這いよれ！ニャル子さん 3 (GA文庫)" should {
      "return 11" in {
        LevenshteinDistance(str1, "這いよれ！ニャル子さん 3 (GA文庫)") should be(11)
      }
    }
    "這いよれ!ニャル子さん4 スペシャルボックス(DVD付き)" should {
      "return 20" in {
        LevenshteinDistance(str1, "這いよれ!ニャル子さん4 スペシャルボックス(DVD付き)") should be(20)
      }
    }

  }
}
