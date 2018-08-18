arete
=====

Uses Spring Boot and JavaFX to create a practice environment for programming. Still experimenting.

Known issues
============

No provision for security. This is intended to be run locally. If you write code that runs `rm -rf /`, it will damage your system.
Please do not damage your system.

Setup
=====

- Install PostgreSQL
- Make a database called `arete`, username `arete`, password `arete`
- Build a JAR with `mvn clean package`
- Run the created JAR with `java -tar target/<output>.jar`.
- You can check for bugs with `mvn spotbugs:check`. I intend to run it before every commit and keep it clean.

LICENSE
=======

Apache