# How can I read system environment variables to the service?

Finatna.g8 project template provides you the support of reading system environment variables to your service via the `ConfigLib` in the package `util`.

#### Demo

```scala mdoc
import $package$.util.AppConfigLib._

val logLevelOpt: Option[String]       = getConfig[String]("LOG_LEVEL")
val isFastFailEnable: Option[Boolean] = getConfig[Boolean]("FAIL_FAST_ENABLE")
val finatraHttpPort: Option[Int]      = getConfig[Int]("FINATRA_HTTP_PORT")
```

You just import `<your package name>.AppConfigLib._` to your code and use `getConfig[T](variableName: String)` to read the system environment variable named after the given string, say "LOG_LEVEL" in the demo. The `getConfig` function's return type is an `Option[T]` where the type `T` is what you specify in the use of `getConfig[T]`. The reason of returning an Option type is that required system environment variable might not be defined.Thus you have the opportunity to deal with this case when you get a `None` in return.

### Supported data types

AppConfigLib supports the following data types (which you can specify `T` for `getConfig[T]` function):

+ Int
+ String
+ Boolean
+ Uri
+ Long

### System variables reading guidelines
+ Do not abruptly stop the service process, say using `System.exit(-1)` because NetOps has no way to know what happened to your service process.
+ Do not give a default value to a configuration if a default value cannot be decided. This might create issues when the production environment did not provide proper production settings.
