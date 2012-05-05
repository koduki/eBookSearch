// vim: set ts=4 sw=4 et:
package cn.orz.pascal.scala.commons.utils

object LevenshteinDistance {
  def apply(str1: String, str2: String): Int = {
    // Make the edit graph array.
    val colsSize = str1.length
    val rowsSize = str2.length
    val distance = new Array[Array[Int]](rowsSize + 1).map(ys => new Array[Int](colsSize + 1))

    // Initialize the leftmost column.
    for (val row <- 0 to rowsSize) {
      distance(row)(0) = row
    }
    // Initialize the top row.
    for (val col <- 0 to colsSize) {
      distance(0)(col) = col
    }

    // Fill in the rest of the array.
    val chars1 = str1.toCharArray()
    val chars2 = str2.toCharArray()
    for (val col <- 1 to colsSize) {
      for (val row <- 1 to rowsSize) {
        // Fill in entry [r, c].
        // Check the three possible paths to here.
        val rightCost = distance(row - 1)(col) + 1 // right value +1
        val downCost = distance(row)(col - 1) + 1 // down value +1
        val diagonalCost = if (chars1(col - 1) == chars2(row - 1)) {
          // There is a diagonal link.
          distance(row - 1)(col - 1);
        } else {
          Int.MaxValue
        }

        distance(row)(col) = min(rightCost, downCost, diagonalCost);
      }
    }

    distance(rowsSize)(colsSize);
  }

  private def min(rightCost: Int, downCost: Int, diagonalCost: Int) = {
    if ((rightCost <= downCost) && (rightCost <= diagonalCost)) {
      // Come from above.
      rightCost;
    } else if (downCost <= diagonalCost) {
      // Come from the left.
      downCost;
    } else {
      // Come from the diagonal.
      diagonalCost;
    }
  }
}
