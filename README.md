arete
=====

Uses Spring Boot and JavaFX to create a practice environment for programming. Still experimenting.

Right now I'm just working through the logistics of how to get this to work so all I have are some proof of concepts of things like:

- Rendering LaTeX as you type
- Fast (<= 10 ms) evaluation of Python scripts + hot reloading
- Simple database backup so that you can run locally but not worry about losing data if your hard drive dies

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
with a <a href="https://www.google.com/search?q=postgresql+create+user+superuser">superuser</a>.

The easiest thing to do is set your `pg_hba.conf` like so:

```
host    all             all             127.0.0.1/32            trust
host    all             all             ::1/128                 trust
host    replication     all             127.0.0.1/32            trust
host    replication     all             ::1/128                 trust

```

Since this is intended to be run locally, this should be fine. This will let us not have to bother with passwords.

The `trust` column differs from the default of `md5`.

- <a href="https://www.latex-project.org/get/">Install a LaTeX distribution</a>.
- Make sure your LaTeX distribution has the `standalone` package. If you install one of the popular distributions, you 
should be fine.

Running
=======

- You can import the project in an IDE of your choice and run `DemoApplication` directly or build a JAR and run from 
the command line:
    - Build a JAR with `mvn clean package`
    - Run the created JAR with `java -tar target/<output>.jar`.

- Make an `application.properties` like this when running the JAR that's in the same directory you're running the JAR from.

See `application.properties.sample` for a sample you can just do like:

```
cp application.properties.sample application.properties
vi application.properties # edit as needed
```

Fill in with suitable values for your machine.

It's on my todo list to glob the path to autoconfigure things where possible.

You can check for bugs with `mvn clean spotbugs:check`. I intend to run it before every commit and keep it clean.

LICENSE
=======

Apache
