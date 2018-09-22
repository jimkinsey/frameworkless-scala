package todo

class FormTests
{
  def testFormBodyEmpty(): Unit = {
    val body = Form.body(Map.empty)
    assert(body isEmpty)
  }

  def testFormBodyWithASingleValue(): Unit = {
    val body = Form.body(Map("name" -> Seq("value")))
    assert(body == "name=value")
  }

  def testFormBodyWithMultipleValuesForTheSameKey(): Unit = {
    val body = Form.body(Map("name" -> Seq("one", "two")))
    assert(body == "name=one&name=two")
  }

  def testFormBodyWithMultipleKeys(): Unit = {
    val body = Form.body(Map("name" -> Seq("Jim"), "age" -> Seq("21")))
    assert(body == "name=Jim&age=21")
  }

  def testFormBodyWithValuesToEncode(): Unit = {
    val body = Form.body(Map("fullname" -> Seq("Jim Kinsey")))
    assert(body == "fullname=Jim+Kinsey")
  }

  def testFormValuesWithEmptyBody(): Unit = {
    val values = Form.values("")
    assert(values.isEmpty)
  }

  def testFormValuesWithSingleValueInBody(): Unit = {
    val values = Form.values("name=Jim")
    assert(values == Map("name" -> Seq("Jim")))
  }

  def testFormValuesWithMultipleValuesInBody(): Unit = {
    val values = Form.values("name=Jim&age=21")
    assert(values == Map("name" -> Seq("Jim"), "age" -> Seq("21")))
  }

  def testFormValuesWithMultipleValuesForTheSameKeyInBody(): Unit = {
    val values = Form.values("name=Jim&name=James")
    assert(values == Map("name" -> Seq("Jim", "James")))
  }

  def testFormValuesWithValuesToDecode(): Unit = {
    val values = Form.values("fullname=Jim+Kinsey")
    assert(values == Map("fullname" -> Seq("Jim Kinsey")))
  }
}
