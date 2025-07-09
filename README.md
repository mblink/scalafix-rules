# BondLink scalafix rules

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Rules](#rules)
  - [`GivenUsing` (specific to Scala 3)](#givenusing-specific-to-scala-3)
  - [`MapGetOrElse`](#mapgetorelse)
  - [`NoUnnecessaryCase` (specific to Scala 3)](#nounnecessarycase-specific-to-scala-3)
  - [`NoUnnecessaryForComprehension`](#nounnecessaryforcomprehension)
  - [`NoUnnecessaryPure` (specific to Scala 3)](#nounnecessarypure-specific-to-scala-3)
  - [`NoWithForExtends` (specific to Scala 3)](#nowithforextends-specific-to-scala-3)
  - [`StrictSubclassAccess`](#strictsubclassaccess)
- [Development](#development)
  - [ServiceLoader configuration](#serviceloader-configuration)
  - [Compiling](#compiling)
  - [Testing](#testing)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Rules

### `GivenUsing` (specific to Scala 3)

This rule enforces that the `given` and `using` keywords are used in place of the `implicit` keyword.

See the [test input file](input/src/main/scala-3/fix/GivenUsing.scala) for examples of what's reported.

### `MapGetOrElse`

This rule enforces the use of `fold` instead of `map...getOrElse`. This applies to any types for which `map...getOrElse`
can be used, e.g. `Option` and `Either`.

See the [test input file](input/src/main/scala/fix/MapGetOrElse.scala) for examples of what's reported.

### `NoUnnecessaryCase` (specific to Scala 3)

This rule enforces that the `case` keyword is not used unnecessarily in functions.
For example, the following code compiles:

```scala
Some((1, 2)).map { case (i, j) => i + j }
```

but the `case` keyword adds runtime overhead and is no longer necessary in Scala 3. This rule will warn that `case` is unnecessary:

```scala
Some((1, 2)).map { case (i + j => i + j }/*
                   ^^^^^^^^^^^^^^^^^^^^
The `case` keyword is unnecessary here */
```

Instead you can write:

```scala
Some((1, 2)).map((i, j) => i + j)
```

See the [test input file](input/src/main/scala-3/fix/NoUnnecessaryCase.scala) for examples of what's reported.

### `NoUnnecessaryForComprehension`

This rule enforces that you don't use for comprehensions when the for comprehension contains a single statement.

See the [test input file](input/src/main/scala/fix/NoUnnecessaryForComprehension.scala) for examples of what's reported.

### `NoUnnecessaryPure` (specific to Scala 3)

This rule enforces that you don't use `x <- foo.pure[...]` on the first line of a for comprehension.
Instead you can just write `x = foo`.

This assumes that you're compiling your Scala 3 code with
[for comprehension improvements](https://docs.scala-lang.org/sips/better-fors.html) enabled.
In Scala 3.6, you can enable this with the `-language:experimental.betterFors` compiler flag.
In Scala 3.7, you can enable it with the `-preview` compiler flag.

See the [test input file](input/src/main/scala-3/fix/NoUnnecessaryPure.scala) for examples of what's reported.

### `NoWithForExtends` (specific to Scala 3)

This rule enforces that you use commas when extending multiple classes/traits instead of the `with` keyword. For example:

```scala
trait Foo
trait Bar

object Baz extends Foo with Bar/*
                       ^^^^
The `with` keyword is unnecessary here, replace with a comma */
```

See the [test input file](input/src/main/scala-3/fix/NoWithForExtends.scala) for examples of what's reported.

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

See `def allowed` in [`rules/.../StrictSubclassAccess.scala`](https://github.com/mblink/scalafix-rules/blob/main/rules/src/main/scala/fix/StrictSubclassAccess.scala#L35-L72)
for the full list of super/subclass access combinations that are allowed by the rule.

## Development

### ServiceLoader configuration

Each rule must be included in the ServiceLoader configuration file in order to be loaded. This
file is located in the `rules/src/main/resources/META-INF/services` directory.

### Compiling

Clone the repository, `cd` into the `scalafix` directory, and start SBT:

```bash
git clone git@github.com:mblink/scalafix-rules.git
cd scalafix-rules
sbt
```

Once SBT is loaded, you can compile everything by simply running `compile`.

### Testing

Tests are implemented and run with `scalafix-testkit`. You can run the tests in SBT against all supported scala versions
with `tests/test`.

Each test must have two files:

```
input/src/main/scala/fix/TestName.scala
output/src/main/scala/fix/TestName.scala
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
// input/src/main/scala/fix/ExampleLintRule.scala
/*
rule = ExampleLintRule
*/
package fix

object ExampleLintRule {
  val example = "example"/* assert: ExampleLintRule
  ^^^^^^^^^^^^^^^^^^^^^^^
  Don't use the literal string "example" */
}

// output/src/main/scala/fix/ExampleLintRule.scala
package fix

object ExampleLintRule {
  val example = "example"
}
```

For an example of a rewrite rule, imagine one named `ExampleRewriteRule` that changes the literal string `"example"` to
`"foobar"`. The `input` and `output` files for its test might look like this:

```scala
// input/src/main/scala/fix/ExampleRewriteRule.scala
/*
rule = ExampleRewriteRule
*/
package fix

object ExampleRewriteRule {
  val example = "example"
}

// output/src/main/scala/fix/ExampleRewriteRule.scala
package fix

object ExampleRewriteRule {
  val example = "foobar"
}
```
