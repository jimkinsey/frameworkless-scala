TODO MVP - A frameworkless Scala TODO list app
===
This is the result of a SpringerNature hack day where I decided to build a fully functional Scala webapp with the use of no frameworks and to keep dependencies generally to an absolute minimum.

* no web-frameworks
* no unnecessary libraries - standard lib as much as humanly possible
* no test frameworks
* no client-side JavaScript libs/frameworks
* no complex build management tool

So far the only dependencies are that Java, Scala and Make are installed.

HTTP
---
It uses the `com.sun.net.httpserver.HttpServer` and `java.net.URL` for server / client HTTP with a thin layer of Scala sugar to make it more idiomatic to work with.

Testing
---
For testing, a home-made test runner app looks for classes ending `Tests` and executes methods starting `test` which use the standard Java `assert` statement.

Inspired by golang, the test classes are located in the same source tree as the production code, so that the tests are close to the code under test.

Templating
---
HTML rendering is done purely through Scala template strings.

Project management
---
The app is managed using `make` with the following commands:

    make compile
     
invokes `scalac` to compile the application to `./bin`.

    make test
    
invokes `scala` to launch the `todo.TestRunner` application.

    make run
    
requires the application to have been compiled, and launches the main class `todo.App` using `scala`.

    make deps
    
fetches the dependencies required by the app and installs them in `./lib` , currently just the Scala standard lib. 

    make assemble
    
requires the application to have been compiled and dependencies fetched, and assembles a "fat jar" in `./bin` which can be launched by the standard `java` command.

    make clean
    
removes the `./bin` and `./lib` directories.