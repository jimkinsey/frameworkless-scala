package todo

class EscapeHTMLTests
{
  def testEmptyStringForElement(): Unit = {
    assert(EscapeHTML.forElement("") == "")
  }

  def testNonEmptyStringForElement(): Unit = {
    assert(EscapeHTML.forElement("Get milk") == "Get milk")
  }

  def testStringWithHTMLElementForElement(): Unit = {
    assert(EscapeHTML.forElement("Get <b>milk</b>") == "Get &lt;b&gt;milk&lt;&#x2F;b&gt;")
  }

  def testStringWithAmpersandForElement(): Unit = {
    assert(EscapeHTML.forElement("Get milk & honey") == "Get milk &amp; honey")
  }

  def testStringWithDoubleQuoteForElement(): Unit = {
    assert(EscapeHTML.forElement("""Get a "drink"""") == "Get a &quot;drink&quot;")
  }

  def testStringWithSingleQuoteForElement(): Unit = {
    assert(EscapeHTML.forElement("""Sort out the cat's dinner""") == "Sort out the cat&#x27;s dinner")
  }

  def testStringWithForwardSlashForElement(): Unit = {
    assert(EscapeHTML.forElement("""Get broccoli/cauliflower""") == "Get broccoli&#x2F;cauliflower")
  }

  def testEmptyStringForAttribute(): Unit = {
    assert(EscapeHTML.forAttribute("") == "")
  }

  def testNonEmptyStringForAttribute(): Unit = {
    assert(EscapeHTML.forAttribute("Get milk") == "Get milk")
  }

  def testStringWithASCIIValuesLessThan256(): Unit = {
    (0 to 255).map(_.toChar).filterNot(_.isLetterOrDigit).filterNot(_.isSpaceChar).foreach { c =>
      assert(EscapeHTML.forAttribute(c.toString) == s"&#x${c.toHexString};")
    }
  }
}
