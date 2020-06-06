# App

A templated Finatra application. See [forthy/finatra.g8](https://github.com/forthy/finatra.g8).

# Build

To compile and run tests:

```
sbt compile test
```

# Run

## Development

This project uses [sbt-revolver](https://github.com/spray/sbt-revolver) to spin up a development instance quickly.

From an SBT console:

```
~re-start
```

This will run your application in a forked JVM, reloading it whenever files change locally.

## Regenerate project site

This project use [sbt-microsites](https://47deg.github.io/sbt-microsites/) to generate a project website.

The site's root directory is in the `<project root directory>/src/main/tut`.

From an SBT console:

```
sbt clean makeMicrosite
```

Then go to `<project root directory>/target/site`, then:

```
jekyll server
```

to publish the project website locally.

**You need to install Jekyll first!**

## Elsewhere

Finatra is a standlone Java application. You can build and package this application in any way you choose (e.g. sbt-assembly).

To make this process as simple as possible, the project template includes [sbt/sbt-native-packager](https://github.com/sbt/sbt-native-packager). This allows you to generate:

* A docker image
* A linux package
* A Redhat package (`rpm`)
* A Debian package
* A Windows installer (`msi`)
* A native package (via `javapackager`)

For full details, see [sbt-native-packger's docs](http://www.scala-sbt.org/sbt-native-packager/formats/index.html)

## Docker

As an example, publishing this application to Docker locally is a cinch.

```
sbt docker:publishLocal
```

Check out the full sbt-native-packager documentation for all configuration options.

When this completes, run the image:

```
docker run $(docker images -a -q | head -1)
```

<kbd>CTRL</kbd>+<kbd>C</kbd> to kill the container.

The native packager's docker plugin gets you up and running in seconds. For longer term development, you'll probably find a `Dockerfile` to be a more maintainable solution.
