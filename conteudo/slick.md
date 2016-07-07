# Slick

Este resumo é uma adaptação de [Hello-Slick](http://www.lightbend.com/activator/template/hello-slick-3.1)

## Introdução ao Slick

Slick é uma biblioteca de mapeamento relacional funcional (FRM) para Scala com a qual se pode lidar com dados relacionais de maneira funcional. Veja um exemplo:

  ````
  coffees.filter(_.price < 10.0).map(_.name)
  ````
Isto produzirá a seguinte consulta SQL:

````
select COF_NAME from COFFEES where PRICE < 10.0
````

## Configuração

Para usar o Slick basta colocar as dependências no arquivo ````build.sbt````. Aqui usamos o banco de dados H2.

````scala
name := """hello-slick-3.1"""

mainClass in Compile := Some("HelloSlick")

scalaVersion := "2.11.8"

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.7.21",
  "com.h2database" % "h2" % "1.4.192",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

fork in run := true
````

## Esquema - Mapeamento de Tabelas

````scala
import slick.driver.H2Driver.api._

// A Suppliers table with 6 columns: id, name, street, city, state, zip
class Suppliers(tag: Tag)
  extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {

  // This is the primary key column:
  def id     = column[Int]("SUP_ID", O.PrimaryKey)
  def name   = column[String]("SUP_NAME")
  def street = column[String]("STREET")
  def city   = column[String]("CITY")
  def state  = column[String]("STATE")
  def zip    = column[String]("ZIP")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, name, street, city, state, zip)
}

// A Coffees table with 5 columns: name, supplier id, price, sales, total
class Coffees(tag: Tag)
  extends Table[(String, Int, Double, Int, Int)](tag, "COFFEES") {

  def name  = column[String]("COF_NAME", O.PrimaryKey)
  def supID = column[Int]("SUP_ID")
  def price = column[Double]("PRICE")
  def sales = column[Int]("SALES")
  def total = column[Int]("TOTAL")

  def * = (name, supID, price, sales, total)

  // A reified foreign key relation that can be navigated to create a join
  def supplier = foreignKey("SUP_FK", supID, TableQuery[Suppliers])(_.id)
}
````

This template includes a simple Scala application that does basic FRM operations with Slick.  

    <p>Slick is a library that is easy to include in any project.  This project uses the sbt build tool so the dependency for Slick is specified in the <a href="#code/build.sbt" class="shortcut">build.sbt</a> file.  </p>

    <p><a href="http://slick.typesafe.com/doc/3.1.1/database.html#database-configuration" target="_blank">Learn more about connecting to databases in the Slick docs</a>.</p>

</div>

<div>
    <h2>Schema / Table Mapping</h2>

    <p>The <a href="#code/src/main/scala/Tables.scala" class="shortcut">Tables.scala</a> file contains the mappings for a <code>Suppliers</code> and a <code>Coffees</code> table.  These <code>Table</code> create a mapping between a database table and a class.  The table's columns are also mapped to functions.  This mapping is called <a href="http://slick.typesafe.com/doc/2.1.0-M2/introduction.html#lifted-embedding" target="_blank">Lifted Embedding</a> since the types of a column mappings are not the actual column value's type, but a wrapper type.  For a column that contains a <code>Double</code> value the type of mapping will be <code>Rep[Double]</code>.  This enables type-safe queries to be built around meta-data and then executed against the database.</p>

    <p>Using a table mapping object requires creating a <code>TableQuery</code> instance for the <code>Table</code> classes.  For example, in <a href="#code/src/main/scala/HelloSlick.scala" class="shortcut">HelloSlick.scala</a> the <code>suppliers</code> val is the <code>TableQuery</code> instance for the <a href="#code/src/main/scala/Tables.scala" class="shortcut">Suppliers</a> class.</p>

    <p><a href="http://slick.typesafe.com/doc/3.1.1/schemas.html" target="_blank">Learn more about mapping tables and columns in the Slick docs</a>.</p>

</div>

<div>
    <h2>Database Connection</h2>

    <p>Database connections are usually configured via <a href="https://github.com/typesafehub/config" target="_blank">Typesafe Config</a> in your <a href="#code/src/main/resources/application.conf" class="shortcut">application.conf</a>, which is also used by <a href="https://playframework.com/" target="_blank">Play</a> and <a href="http://akka.io/" target="_blank">Akka</a> for their configuration:</p>

    <pre><code>h2mem1 = {
  url = "jdbc:h2:mem:test1"
  driver = org.h2.Driver
  connectionPool = disabled
  keepAliveConnection = true
}</code></pre>

    <p>The default connection pool is <a href="http://brettwooldridge.github.io/HikariCP/" target="_blank">HikariCP</a>. Since a connection pool is not necessary for an embedded H2 database, we disable it here. When you use a real, external database server, the connection pool provides improved performance and resilience. The <code>keepAliveConnection</code> option (which is only available without a connection pool) keeps an extra connection open for the lifetime of the <code>Database</code> object in the application. It is useful for managing the lifecycle of named in-memory databases which keep their data as long as there are still open connections.</p>

    <p>In the body of <a href="#code/src/main/scala/HelloSlick.scala" class="shortcut">HelloSlick.scala</a> we create a <code>Database</code> object from the configuration. This causes a thread pool (and usually also a connection pool) to be created in the background. You should always close the <code>Database</code> object at the end to release these resources. HelloSlick is a standalone command-line application, not running inside of a container which takes care of resource management, so we have to do it ourselves. Since all database calls in Slick are asynchronous, we are going to compose Futures throughout the app, but eventually we have to wait for the result. This gives us the following scaffolding:</p>

    <pre><code>val db = Database.forConfig("h2mem1")
try {
  val f: Future[_] = {
    // body of the application
  }
  Await.result(f, Duration.Inf)
} finally db.close</code></pre>

    <p>If you are not familiar with asynchronous, Future-based programming Scala, you can learn more about <a href="http://docs.scala-lang.org/overviews/core/futures.html" target="_blank">Futures and Promises</a> in the Scala documentation.</p>
</div>

<div>
    <h2>Creating and Inserting</h2>

    <p>To create corresponding tables from a mapping you can get the schema via its <code>TableQuery</code> and then call the <code>create</code> method, like:</p>

    <pre><code>suppliers.schema.create</code></pre>

    <p>Multiple schemas can also be combined as in <a href="#code/src/main/scala/HelloSlick.scala" class="shortcut">HelloSlick.scala</a>, to create all database entities and links (like foreign key references) in the correct order, even in the presence of cyclic dependencies between tables:</p>

    <pre><code>(suppliers.schema ++ coffees.schema).create</code></pre>

    <p>The result of <code>.create</code> is a <em>database I/O action</em> which encapsulates the DDL statements.</p>

    <p>Creates / Inserts are as simple as appending the values to a <code>TableQuery</code> instance using either the <code>+=</code> operator for a single row, or <code>++=</code> for multiple rows.  In <a href="#code/src/main/scala/HelloSlick.scala" class="shortcut">HelloSlick.scala</a> both of these ways of doing inserts are used.</p>
</div>

<div>
    <h2>Running Database I/O Actions</h2>

    <p>Like all other database operations, <code>+=</code> and <code>++=</code> return database I/O actions. If you do not care about more advanced features like streaming, effect tracking or extension methods for certain actions, you can denote their type as <code>DBIO[T]</code> (for an operation which will eventually produce a value of type <code>T</code>). Instead of running all actions separately, you can combine them with other actions in various ways. The simplest combinator is <code>DBIO.seq</code> which takes a variable number of actions of any type and combines them into a single <code>DBIO[Unit]</code> that runs the actions in the specified order. We use it in <a href="#code/src/main/scala/HelloSlick.scala" class="shortcut">HelloSlick.scala</a> to define <code>setupAction</code> which combines schema creation with some insert actions.</p>

    <p>So far we have only staged the operations. We can run them with <code>db.run</code>:</p>

    <pre><code>val setupFuture: Future[Unit] =
  db.run(setupAction)</code></pre>

    <p>This performs the database calls asynchronously, eventually completing the returned <code>Future</code>.</p>

    <p>When inserting data, the database usually returns the number of affected rows, therefore the return type is <code>Option[Int]</code> as can be seen in the definition of <code>insertAction</code>:</p>

    <pre><code>val insertAction: DBIO[Option[Int]] = ...</code></pre>

    <p>We can use the <code>map</code> combinator to run some code and compute a new value from the value returned by the action (or in this case run it only for its side effects and return <code>Unit</code>):

    <pre><code>val insertAndPrintAction: DBIO[Unit] = insertAction.map { coffeesInsertResult =>
  // Print the number of rows inserted
  coffeesInsertResult foreach { numRows =>
    println(s"Inserted $numRows rows into the Coffees table")
  }
}</code></pre>

    <p>Note that <code>map</code> and all other combinators which run user code (e.g. flatMap, cleanup, filter) take an implicit <code>ExecutionContext</code> on which to run this code. Slick uses its own ExecutionContext internally for running blocking database I/O but it always maintains a clean separation and prevents you from running non-I/O code on it.</p>

</div>

<div>
    <h2>Querying and Streaming</h2>

    <p>Queries usually start with a <code>TableQuery</code> instance. In the simplest case you read the contents of an entire table by calling <code>.result</code> directly on the <code>TableQuery</code> to get a <code>DBIO</code> action, as shown in <a href="#code/src/main/scala/HelloSlick.scala" class="shortcut">HelloSlick.scala</a>:</p>

    <pre><code>val allSuppliersAction: DBIO[Seq[(Int, String, String, String, String, String)]] =
  suppliers.result</code></pre>

    <p>This produces a <code>Seq[(Int, String, String, String, String, String)]</code> that corresponds to the columns defined in the <code>Table</code> mapping.  Filtering, sorting, and joining will be covered in the next few sections of the tutorial.  We use another new combinator to combine the previously defined <code>insertAndPrintAction</code> with the new <code>allSuppliersAction</code>:</p>

    <pre><code>val combinedAction: DBIO[Seq[(Int, String, String, String, String, String)]] =
  insertAndPrintAction >> allSuppliersAction

val combinedFuture: Future[Seq[(Int, String, String, String, String, String)]] =
  db.run(combinedAction)</code></pre>

    <p>The <code>&gt;&gt;</code> combinator (also available under the name <code>andThen</code>) runs the second action after the first, similar to <code>DBIO.seq</code> but it does not discard the return value of the second action.</p>

    <p>The default query we've been using uses the <code>*</code> method on the <code>Table</code> mapping class.  For instance, the <code>suppliers</code> <code>TableQuery</code> uses the <code>*</code> method defined in <a href="#code/src/main/scala/Tables.scala" class="shortcut">Tables.scala</a> and returns all of the columns when executed because the <code>*</code> combines all of the columns.  Often we just want to select a subset of the columns.  To do this use the <code>map</code> method on a query, like:</p>

    <pre><code>val coffeeNamesAction: StreamingDBIO[Seq[String], String] =
  coffees.map(_.name).result</code></pre>

    <p>This will create a new query that when executed just returns the <code>name</code> column.  The generated SQL will be something like:</p>

    <pre><code>select SUP_NAME from SUPPLIERS</code></pre>

    <p>The type annotation above uses the type <code>StreamingDBIO[Seq[String], String]</code> instead of <code>DBIO[Seq[String]]</code> to also allow streaming. The first type parameter denotes the fully materialized result (as in <code>DBIO</code>) whereas the second type parameter is only the element type. Note that these types can always be inferred by the compiler. They are only spelled out explicitly in this tutorial to facilitate understanding. If you have a streaming action, you can use <code>db.stream</code> instead of <code>db.run</code> to get a <a href="http://reactive-streams.org/" target="_blank">Reactive Streams</a> <code>Publisher</code> instead of a <code>Future</code>. This allows data to be streamed asynchronously from the database with any compatible library like <em>Akka Streams</em>. Slick itself does not provide a full set of tools for working with streams but it has a <code>.foreach</code> utility method for consuming a stream:</p>

    <pre><code>val coffeeNamesPublisher: DatabasePublisher[String] =
  db.stream(coffeeNamesAction)

coffeeNamesPublisher.foreach(println)</code></pre>

    <p>Note that a database I/O action does <em>not</em> yet start running when you call <code>db.stream</code>. You must attach a <code>Subscriber</code> to the stream (i.e. start consuming the stream) to actually run the action.</p>

</div>

<div>
    <h2>More CRUD Operations</h2>

    <p>Filtering / adding <code>where</code> statements to a query is done using methods like <code>filter</code> and <code>take</code> on a <code>Query</code> to construct a new query.  For example, to create a new query on the <code>Coffees</code> table that selects only rows where the price is higher than 9.0, we use the folling code in <a href="#code/src/main/scala/HelloSlick.scala" class="shortcut">HelloSlick.scala</a>:<p>

    <pre><code>coffees.filter(_.price &gt; 9.0)</code></pre>

    <p>This produces a SQL statement equivalent to:</p>

    <pre><code>select * from COFFEES where PRICE &gt; 9.0</code></pre>

    <p>Updates are done through a <code>Query</code> object by calling the <code>update</code> method.  To update the <code>sales</code> column on all rows of the <code>Suppliers</code> table, create a new query for just that column:</p>

    <pre><code>val updateQuery: Query[Column[Int], Int] =
  coffees.map(_.sales)</code></pre>

    <p>Then call the <code>update</code> with the new value to produce an action that will perform the update. Updates, like inserts, return the number of affected rows:</p>

    <pre><code>val updateAction: DBIO[Int] =
  updateQuery.update(1)</code></pre>

    <p>Deletes are done by just calling <code>delete</code> on a query to get an action.  So to delete coffees with a price less than 8.0, you do:</p>

    <pre><code>val deleteQuery: Query[Coffees,(String, Int, Double, Int, Int), Seq] =
  coffees.filter(_.price < 8.0)

val deleteAction = deleteQuery.delete</code></pre>

    <p>This will produce SQL equivalent to:</p>

    <pre><code>delete from COFFEES where PRICE &lt; 8.0</code></pre>
</div>

<div>
    <h2>Query Composition</h2>

    <p>Sorting / adding <code>order by</code> clauses is done using methods like <code>sortBy</code> on a <code>Query</code> to create a new query.  For example in <a href="#code/src/main/scala/HelloSlick.scala" class="shortcut">HelloSlick.scala</a> you can see an example sorting of coffees by price:</p>

    <pre><code>coffees.sortBy(_.price)</code></pre>

    <p>This would produce SQL equivalent to:</p>

    <pre><code>select * from COFFEES order by PRICE</code></pre>

    <p>The examples so far have taken a basic <code>TableQuery</code> and used a method to produce a new, more specific query.  Due to the functional nature of the query API, this can be done repeatedly to produce more specific queries.  For example, to create a query on the <code>Coffees</code> table that sorts them by name, takes the first three rows, filters those with a prices greater than 9.0, and finally just returns the names, simply do:</p>

    <pre><code>coffees.sortBy(_.name).take(3).filter(_.price &gt; 9.0).map(_.name)</code></pre>

    <p>This results in a new query that has a fairly complex implementation in SQL.</p>
</div>

<div>
    <h2>Joins</h2>

    <p>
        The <code>Coffees</code> table mapping in the <a href="#code/src/main/scala/Tables.scala" class="shortcut">Tables.scala</a> file includes a foreign key mapping to the <code>Suppliers</code> table:
        <pre><code>foreignKey("SUP_FK", supID, TableQuery[Suppliers])(_.id)</code></pre>
        To use this foreign key in a joined query it is easiest to use a <code>for</code> comprehension, like:
        <pre><code>for {
  c &lt;- coffees if c.price &gt; 9.0
  s &lt;- c.supplier
} yield (c.name, s.name)</code></pre>
        This creates a new query that gets the <code>coffees</code> with a price greater than 9.0 and then joins them with their <code>suppliers</code> and returns their names.
    </p>
</div>

<div>
    <h2>Computed Values</h2>

    <p>
        Computed values like minimum, maximum, summation, and average can be done in the database using the query functions <code>min</code>, <code>max</code>, <code>sum</code> and <code>avg</code> like:
        <pre><code>coffees.map(_.price).max</code></pre>
        This creates a new <code>Column</code> where you can run the query by calling <code>run</code> to get back the value.  Check out the example in <a href="#code/src/main/scala/HelloSlick.scala" class="shortcut">HelloSlick.scala</a>.
    </p>
</div>

<div>
    <h2>Plain SQL / String Interpolation</h2>

    <p>Sometimes writing manual SQL is the easiest and best way to go but we don't want to lose SQL injection protection that Slick includes.  SQL String Interpolation provides a nice API for doing this.  In <a href="#code/src/main/scala/HelloSlick.scala" class="shortcut">HelloSlick.scala</a> we use the <code>sql</code> interpolator:</p>

    <pre><code>val state = "CA"
val plainQuery = sql"select SUP_NAME from SUPPLIERS where STATE = $state".as[String]</code></pre>

    <p>This produces a database I/O action that can be run or streamed in the usual way.</p>

    <p>You can learn more about Slick's <em>Plain SQL</em> queries in the <a href="https://typesafe.com/activator/template/slick-plainsql-3.0" target="_blank">Slick Plain SQL Queries (Slick 3.0)</a> template for Activator.</p>
</div>

<div>
    <h2>Case Class Mapping</h2>

    <p>
        The <a href="#code/src/main/scala/CaseClassMapping.scala" class="shortcut">CaseClassMapping.scala</a> file provides an example which uses a case class instead of tupled values.  Run this example by selecting <code>CaseClassMapping</code> in <a href="#run" class="shortcut">Run</a>.  To use case classes instead of tuples setup a <code>def *</code> projection which transforms the tuple values to and from the case class.  For example:
        <pre><code>def * = (id.?, name) <> (User.tupled, User.unapply)</code></pre>
        This uses the <code>User</code>'s <code>tupled</code> function to convert a <code>(Option[Int], String)</code> to a <code>User</code> and the <code>unapply</code> function to do the opposite.  Now all of the queries can work with a <code>User</code> instead of the tuples.
    </p>
</div>

<div>
    <h2>Auto-Generated Primary Keys</h2>

    <p>
        The <code>Users</code> table mapping in <a href="#code/src/main/scala/CaseClassMapping.scala" class="shortcut">CaseClassMapping.scala</a> defines an id column which uses an auto-incrementing primary key:
    <pre><code>def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)</code></pre>

    </p>
</div>

<div>
    <h2>Running Queries</h2>

    <p>So far you have seen how to get a <code>Seq</code> from a collection-valued query and how to stream individual elements. There are several other useful methods which are shown in <a href="#code/src/main/scala/QueryActions.scala" class="shortcut">QueryActions.scala</a>. They are equally applicable to <em>Lifted Embedding</em> and <em>Plain SQL</em> queries.</p>

    <p>Note the use of <code>Compiled</code> in this app. It is used to define a pre-compiled query that can be executed with different parameters without having to recompile the SQL statement each time. This is the prefered way of defining queries in real-world applications. It prevents the (possibly expensive) compilation each time and leads to the same SQL statement (or a small, fixed set of SQL statements) so that the database system can also reuse a previously computed execution plan. As a side-effect, all parameters are automatically turned into bind variables:

    <pre><code>val upTo = Compiled { k: Rep[Int] =>
  ts.filter(_.k <= k).sortBy(_.k)
}</code></pre>
    </p>

</div>

<div>
    <h2>Next Steps</h2>

    <p>Check out the full <a href="http://slick.typesafe.com/doc/3.1.1/" target="_blank">Slick manual and API docs</a>.</p>

    <p>You can also find more Slick templates, contributed by both, the Slick team and the community, <a href="/home" class="shortcut">here in Activator</a>.</p>
</div>

</body>
</html>
