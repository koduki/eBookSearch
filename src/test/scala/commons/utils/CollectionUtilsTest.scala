// vim: set ts=4 sw=4 et:
package cn.orz.pascal.commons.utils
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class CollectionUtilsTest extends WordSpec with ShouldMatchers {

  "splitGroup(3)([1, 2, 3, 4, 5, 6]" should {
    "be ([1, 2, 3], [4, 5, 6])" in {
      CollectionUtils.splitGroup(3)(List(1, 2, 3, 4, 5, 6)) should be(List(List(1, 2, 3), List(4, 5, 6)))
    }
  }
  
  "splitGroup(2)(['a', 'b', 'c', 'd', 'e']" should {
    "be ([['a', 'b'], ['c', 'd'], ['e']])" in {
      CollectionUtils.splitGroup(2)(List('a', 'b', 'c', 'd', 'e')) should be(List(List('a', 'b'), List('c', 'd'), List('e')))
    }
  }

  "splitGroup(1)(Set[1, 2]" should {
    "be ([1], [2)" in {
      CollectionUtils.splitGroup(1)(Set(1, 2)) should be(Set(Set(1), Set(2)))
    }
  }
  
}
