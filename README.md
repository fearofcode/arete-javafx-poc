arete
=====

Uses Spring Boot and JavaFX to create a practice environment for programming. Still experimenting.

Known issues
============

- No provision for security. This is intended to be run locally. If you write code that runs `rm -rf /`, it will damage your system.
Please do not damage your system.
- LaTeX will probably break if you give the application a path that contains spaces since LaTeX appears to handle
spaces in commands extremely poorly.

Setup
=====

- <a href="https://www.google.com/search?q=install+postgresql">Install PostgreSQL</a>
- <a href="https://www.google.com/search?q=postgresql+create+database">Make a database</a> 
called `arete` with a <a href="https://www.google.com/search?q=postgresql+create+user+superuser">user</a> of 
username `arete`, password `arete`. The user should be a superuser.
- <a href="https://www.latex-project.org/get/">Install a LaTeX distribution</a>.
- Make sure your LaTeX distribution has the `standalone` package. If you install one of the popular distributions, you 
should be fine.

Running
=======
- You can import the project in an IDE of your choice and run `DemoApplication` directly or build a JAR and run from 
the command line:
    - Build a JAR with `mvn clean package`
    - Run the created JAR with `java -tar target/<output>.jar`.

You can check for bugs with `mvn clean spotbugs:check`. I intend to run it before every commit and keep it clean.

LICENSE
=======

Apache