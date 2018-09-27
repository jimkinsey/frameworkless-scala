package todo

class ListDescriptionTests
{
  def testEmptyListIsEmptyString(): Unit = {
    assert(ListDescription(Seq.empty) == "")
  }

  def testSingleItemIsUnchanged(): Unit = {
    assert(ListDescription(Seq("A thing")) == "A thing")
  }

  def testTwoItemsSeparatedByAnd(): Unit = {
    assert(ListDescription(Seq("A one", "A two")) == "A one and A two")
  }

  def testThreeItemsSeparatedByCommaAndAnd(): Unit = {
    assert(ListDescription(Seq("This", "that", "the other")) == "This, that and the other")
  }
}
