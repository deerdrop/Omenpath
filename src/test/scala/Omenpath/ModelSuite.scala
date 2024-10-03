package Omenpath

import _root_.Omenpath.Func.rotateVector

class ModelSuite extends munit.FunSuite {
  test("basic") {

  }

  test("rotateVector") {
    val v1: Vector[Int] = Vector(1, 2, 3, 4, 5, 6, 7, 8)
    val v2: Vector[Int] = Vector(23, 63, 46, 89, 34, 13)
    val v3: Vector[String] = Vector("apple", "banana", "carrot", "durian")
    assertEquals(rotateVector(v1, 5, 1), Vector(4, 5, 6, 7, 8, 1, 2, 3))
    assertEquals(rotateVector(v1, 2, 4), Vector(6, 7, 8, 1, 2, 3, 4, 5))
    assertEquals(rotateVector(v2, 63, 0), Vector(63, 46, 89, 34, 13, 23))
    assertEquals(rotateVector(v3, "apple", 1), Vector("durian", "apple", "banana", "carrot"))
  }
}