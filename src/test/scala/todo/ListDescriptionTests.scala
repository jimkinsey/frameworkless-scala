package todo

import utest._

object ListDescriptionTests
extends TestSuite
{
  val tests = Tests {
    "Empty list is empty string" - {
      assert(ListDescription(Seq.empty) == "")
    }

    "List with one item is unchanged" - {
      assert(ListDescription(Seq("A thing")) == "A thing")
    }

    "List with two items separates by 'and'" - {
      assert(ListDescription(Seq("A one", "A two")) == "A one and A two")
    }

    "List with three items uses comma plus and" - {
      assert(ListDescription(Seq("This", "that", "the other")) == "This, that and the other")
    }
  }
}
