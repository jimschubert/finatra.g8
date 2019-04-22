# How to use PipeOperator?

PipeOperator provides functions that help you glue pieces of code together as a linear statement of your computation.

PipeOperator provides the following functions:

+ `|>[U](f: T => U): U`
+ `$"$"$$"$"$[U](f: T => U): T`
+ `#!(str: String = ""): T`

The most often used functions should be `|>` and `$"$"$$"$"$`.
Arguably, `$"$"$$"$"$` allows a side-effect, thus it is not really recommended. However, it can be really helpful to do logging, for example, so use it with cautions. Let's focus on `|>` function.  

#### Demo

```scala mdoc
import $package$.util.PipeOperator._

val op1: Int => Int = i => i + 1
val op2: Int => Unit = i => println(s"The given number is $"$"$i.")

// Without PipeOperator
op2(op1(1))

// With PipeOperator
op1(1).|>(i => op2(i))
// or
op1(1).|>(op2)
```

You can see from the above sample code and understand how Pipe operator change the "shape" of your code logic. It shifts your programming logic from a *compose* style, `f(g(t))`, to a *andThen* one, `g(t).|>(f)`. From functional programming's point view this is a preferred shape that looks like `A => B => C => ...`. We can translate what it is in our mind to the code logic directly without translating our thinking to compose style.

You can read [this article](https://www.scala-academy.com/tutorials/scala-pipe-operator-tutorial) for details.
