# BondLink scalafix rules

## Rules

### `StrictSubclassAccess`

This rule enforces that overridden `def`s and `val`s don't have stronger access privileges than their superclass, i.e.
that the `def`/`val` is not more accessible to outside code than it is in the superclass.

For example, the scala compiler allows the following code to compile:

```scala
trait Super {
  // Protected method in superclass
  protected def test: Unit = ()
}
object Sub extends Super {
  // Public method in subclass
  override def test: Unit = println("Sub.test")
}
```

This rule will identify that `override def test` in `Sub` is not defined with the same `protected` access modifier as
its superclass. The error looks like this:

```scala
object Sub extends Super {
  override def test: Unit = println("Sub.test")/*
  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  Subclass access 'public' does not match superclass access 'protected' */
}
```

See `def allowed` in [`scalafix/rules/.../StrictSubclassAccess.scala`](https://github.com/mblink/scalafix-rules/blob/main/scalafix/rules/src/main/scala/fix/StrictSubclassAccess.scala#L35-L72)
for the full list of super/subclass access combinations that are allowed by the rule.

## Development

### Compiling

Clone the repository, `cd` into the `scalafix` directory, and start SBT:

```bash
git clone git@github.com:mblink/scalafix-rules.git
cd scalafix-rules/scalafix
sbt
```

Once SBT is loaded, you can compile everything by simply running `compile`.

### Testing

Tests are implemented and run with `scalafix-testkit`. You can run the tests in SBT against all supported scala versions
with `tests/test`.

Each test must have two files:

```
scalafix/input/src/main/scala/fix/TestName.scala
scalafix/output/src/main/scala/fix/TestName.scala
```

The file in the `input` directory should list the scalafix rule to be tested in a comment at the top, and can contain
comments with assertions about expected scalafix linting behavior.

The file in the `output` directory should contain the equivalent code as the `input` file, but with the comments
removed, and with any automated scalafix changes applied.

`scalafix-testkit` will apply the rule to the `input` code and verify two things:

1. That all assertions in the `input` code were true
2. That the `output` code matches the result of applying the rule to the `input` code

For an example of a lint-only rule, imagine one named `ExampleLintRule` that adds a linting error when a `val` is
equal to the literal string `"example"`. The `input` and `output` files for its test might look like this:

```scala
// scalafix/input/src/main/scala/fix/ExampleLintRule.scala
/*
rule = ExampleLintRule
*/
package fix

object ExampleLintRule {
  val example = "example"/* assert: ExampleLintRule
  ^^^^^^^^^^^^^^^^^^^^^^^
  Don't use the literal string "example" */
}

// scalafix/output/src/main/scala/fix/ExampleLintRule.scala
package fix

object ExampleLintRule {
  val example = "example"
}
```

For an example of a rewrite rule, imagine one named `ExampleRewriteRule` that changes the literal string `"example"` to
`"foobar"`. The `input` and `output` files for its test might look like this:

```scala
// scalafix/input/src/main/scala/fix/ExampleRewriteRule.scala
/*
rule = ExampleRewriteRule
*/
package fix

object ExampleRewriteRule {
  val example = "example"
}

// scalafix/output/src/main/scala/fix/ExampleRewriteRule.scala
package fix

object ExampleRewriteRule {
  val example = "foobar"
}
```
