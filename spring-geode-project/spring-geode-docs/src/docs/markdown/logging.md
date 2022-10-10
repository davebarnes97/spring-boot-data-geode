<div id="header">

</div>

<div id="content">

<div class="sect1">

## Logging

<div class="sectionbody">

<div class="paragraph">

{pivotal-gemfire-name} `1.9.2` was modularized to separate its use of
the Apache Log4j API to log output in {pivotal-gemfire-name} code from
the underlying implementation of logging, which uses Apache Log4j as the
logging provider by default.

</div>

<div class="paragraph">

Prior to `1.9.2`, the Apache Log4j API (`log4j-api`) and the Apache
Log4j service provider (`log4j-core`) were automatically pulled in by
{pivotal-gemfire-name} core (`org.apache.geode:geode-core`), thereby
making it problematic to change logging providers when using
{pivotal-gemfire-name} in Spring Boot applications.

</div>

<div class="paragraph">

However, now, in order to get any log output from {pivotal-gemfire-name}
whatsoever, {pivotal-gemfire-name} requires a logging provider declared
on your Spring Boot application classpath. Consequently, this also means
the old {pivotal-gemfire-name} `Properties` (such as `log-level`) no
longer have any effect, regardless of whether the property is specified
in `gemfire.properties`, in Spring Boot `application.properties`, or
even as a JVM System Property (`-Dgemfire.log-level`).

</div>

<div class="admonitionblock tip">

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Tip
</div></td>
<td class="content">See {pivotal-gemfire-name}'s
{apache-geode-docs}/reference/topics/gemfire_properties.html[documentation]
for a complete list of valid <code>Properties</code>, including the
<code>Properties</code> used to configure logging.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Unfortunately, this also means the Spring Data for
{pivotal-gemfire-name}
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/EnableLogging.html\[`@EnableLogging`\]
annotation no longer has any effect on {pivotal-gemfire-name} logging
either. Consequently, it has been
[deprecated](https://jira.spring.io/browse/DATAGEODE-299). The reason
`@EnableLogging` no longer has any effect on logging is because this
annotation’s attributes and associated SDG properties indirectly set the
corresponding {pivotal-gemfire-name} properties, which, again, are
useless from {pivotal-gemfire-name} `1.9.2` onward.

</div>

<div class="paragraph">

By way of example, and to make this concrete, **none** of the following
approaches have any effect on {pivotal-gemfire-name} logging:

</div>

<div class="exampleblock">

<div class="title">

Example 1. Command-line configuration

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
$ java -classpath ...:/path/to/MySpringBootApacheGeodeClientCacheApplication.jar -Dgemfire.log-level=DEBUG
    example.app.MySpringBootApacheGeodeClientCacheApplication
```

</div>

</div>

</div>

</div>

<div class="exampleblock">

<div class="title">

Example 2. Externalized configuration using {pivotal-gemfire-name}
`gemfire.properties`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# {geode-name} only/specific properties
log-level=INFO
```

</div>

</div>

</div>

</div>

<div class="exampleblock">

<div class="title">

Example 3. Externalized configuration using Spring Boot
`application.properties`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
spring.data.gemfire.cache.log-level=DEBUG
spring.data.gemfire.logging.level=DEBUG
```

</div>

</div>

</div>

</div>

<div class="exampleblock">

<div class="title">

Example 4. Java configuration using SDG’s `@EnableLogging` annotation

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@EnableLogging(logLevel = "DEBUG")
class MySpringBootApacheGeodeClientApplication {

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

None of the preceding approaches have any effect without the **new**
SBDG logging starter.

</div>

<div class="sect2">

### Configure {pivotal-gemfire-name} Logging

<div class="paragraph">

So, how do you configure logging for {pivotal-gemfire-name}?

</div>

<div class="paragraph">

Three things are required to get {pivotal-gemfire-name} to log output:

</div>

<div class="olist arabic">

1.  You must declare a logging provider (such as Logback, or Log4j) on
    your Spring Boot application classpath.

2.  (optional) You can declare an adapter (a bridge JAR) between Log4j
    and your logging provider if your declared logging provider is not
    Apache Log4j.

    <div class="paragraph">

    For example, if you use the SLF4J API to log output from your Spring
    Boot application and use Logback as your logging provider or
    implementation, you must include the
    `org.apache.logging.log4j.log4j-to-slf4j` adapter or bridge JAR as
    well.

    </div>

    <div class="paragraph">

    Internally, {pivotal-gemfire-name} uses the Apache Log4j API to log
    output from Geode components. Therefore, you must bridge Log4j to
    any other logging provider (such as Logback) that is not Log4j
    (`log4j-core`). If you use Log4j as your logging provider, you need
    not declare an adapter or bridge JAR on your Spring Boot application
    classpath.

    </div>

3.  Finally, you must supply logging provider configuration to configure
    Loggers, Appenders, log levels, and other details.

    <div class="paragraph">

    For example, when you use Logback, you must provide a `logback.xml`
    configuration file on your Spring Boot application classpath or in
    the filesystem. Alternatively, you can use other means to configure
    your logging provider and get {pivotal-gemfire-name} to log output.

    </div>

</div>

<div class="admonitionblock note">

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Note
</div></td>
<td class="content">{pivotal-gemfire-name}'s <code>geode-log4j</code>
module covers the required configuration for steps 1-3 above and uses
Apache Log4j (<code>org.apache.logging.log4j:log4j-core</code>) as the
logging provider. The <code>geode-log4j</code> module even provides a
default <code>log4j2.xml</code> configuration file to configure Loggers,
Appenders, and log levels for {pivotal-gemfire-name}.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

If you declare Spring Boot’s own
`org.springframework.boot:spring-boot-starter-logging` on your
application classpath, it covers steps 1 and 2 above.

</div>

<div class="paragraph">

The `spring-boot-starter-logging` dependency declares Logback as the
logging provider and automatically adapts (bridges) `java.util.logging`
(JUL) and Apache Log4j to SLF4J. However, you still need to supply
logging provider configuration (such as a `logback.xml` file for
Logback) to configure logging not only for your Spring Boot application
but for {pivotal-gemfire-name} as well.

</div>

<div class="admonitionblock note">

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Note
</div></td>
<td class="content">If no user-specified logging configuration is
supplied, Logback will apply default configuration using the
<code>BasicConfigurator</code>. See Logback <a
href="https://logback.qos.ch/manual/configuration.html#auto_configuration">documentation</a>
for complete details.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

SBDG has simplified the setup of {pivotal-gemfire-name} logging. You
need only declare the
`org.springframework.geode:spring-geode-starter-logging` dependency on
your Spring Boot application classpath.

</div>

<div class="paragraph">

Unlike {pivotal-gemfire-name}'s default Log4j XML configuration file
(`log4j2.xml` from `geode-log4j`), SBDG’s provided `logback.xml`
configuration file is properly parameterized, letting you adjust log
levels, add Appenders as well as adjust other logging settings.

</div>

<div class="paragraph">

In addition, SBDG’s provided Logback configuration uses templates so
that you can compose your own logging configuration while still
including snippets from SBDG’s provided logging configuration, such as
Loggers and Appenders.

</div>

<div class="sect3">

#### Configuring Log Levels

<div class="paragraph">

One of the most common logging tasks is to adjust the log level of one
or more Loggers or the ROOT Logger. However, you may want to only adjust
the log level for specific components of your Spring Boot application,
such as for {pivotal-gemfire-name}, by setting the log level for only
the Logger that logs {pivotal-gemfire-name} events.

</div>

<div class="paragraph">

SBDG’s Logback configuration defines three Loggers to control the log
output from {pivotal-gemfire-name}:

</div>

<div class="exampleblock">

<div class="title">

Example 5. {pivotal-gemfire-name} Loggers by Name

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
<comfiguration>
  <logger name="com.gemstone.gemfire" level="${spring.boot.data.gemfire.log.level:-INFO}"/>
  <logger name="org.apache.geode" level="${spring.boot.data.gemfire.log.level:-INFO}"/>
  <logger name="org.jgroups" level="${spring.boot.data.gemfire.jgroups.log.level:-WARN}"/>
</comfiguration>
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The `com.gemstone.gemfire` Logger covers old GemFire components that are
still present in {pivotal-gemfire-name} for backwards compatibility. By
default, it logs output at `INFO`. This Logger’s use should be mostly
unnecessary.

</div>

<div class="paragraph">

The `org.apache.geode` Logger is the primary Logger used to control log
output from all {pivotal-gemfire-name} components during the runtime
operation of {pivotal-gemfire-name}. By default, it logs output at
`INFO`.

</div>

<div class="paragraph">

The `org.jgroups` Logger is used to log output from
{pivotal-gemfire-name}'s message distribution and membership system.
{pivotal-gemfire-name} uses JGroups for membership and message
distribution between peer members (nodes) in the cluster (distributed
system). By default, JGroups logs output at `WARN`.

</div>

<div class="paragraph">

You can configure the log level for the `com.gemstone.gemfire` and
`org.apache.geode` Loggers by setting the
`spring.boot.data.gemfire.log.level` property. You can independently
configure the `org.jgroups` Logger by setting the
`spring.boot.data.gemfire.jgroups.log.level` property.

</div>

<div class="paragraph">

You can set the SBDG logging properties on the command line as JVM
System properties when you run your Spring Boot application:

</div>

<div class="exampleblock">

<div class="title">

Example 6. Setting the log-level from the CLI

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
$ java -classpath ...:/path/to/MySpringBootApplication.jar -Dspring.boot.data.gemfire.log.level=DEBUG
    package.to.MySpringBootApplicationClass
```

</div>

</div>

</div>

</div>

<div class="admonitionblock note">

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Note
</div></td>
<td class="content">Setting JVM System properties by using
<code>$ java -jar MySpringBootApplication.jar -Dspring.boot.data.gemfire.log.level=DEBUG</code>
is not supported by the Java Runtime Environment (JRE).</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Alternatively, you can configure and control {pivotal-gemfire-name}
logging in Spring Boot `application.properties`:

</div>

<div class="exampleblock">

<div class="title">

Example 7. Setting the log-level in Spring Boot `application.properties`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
spring.boot.data.gemfire.log.level=DEBUG
```

</div>

</div>

</div>

</div>

<div class="paragraph">

For backwards compatibility, SBDG additionally supports the Spring Data
for {pivotal-gemfire-name} (SDG) logging properties as well, by using
either of the following properties:

</div>

<div class="exampleblock">

<div class="title">

Example 8. Setting log-level using SDG Properties

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
spring.data.gemfire.cache.log-level=DEBUG
spring.data.gemfire.logging.level=DEBUG
```

</div>

</div>

</div>

</div>

<div class="paragraph">

If you previously used either of these SDG-based logging properties,
they continue to work as designed in SBDG `1.3` or later.

</div>

</div>

<div class="sect3">

#### Composing Logging Configuration

<div class="paragraph">

As mentioned earlier, SBDG lets you compose your own logging
configuration from SBDG’s default Logback configuration metadata.

</div>

<div class="paragraph">

SBDG conveniently bundles the Properties, Loggers and Appenders from
SBDG’s logging starter into several template files that you can include
into your own custom Logback XML configuration file.

</div>

<div class="paragraph">

The Logback configuration template files are broken down into:

</div>

<div class="ulist">

- `org/springframework/geode/logging/slf4j/logback/properties-include.xml`

- `org/springframework/geode/logging/slf4j/logback/loggers-include.xml`

- `org/springframework/geode/logging/slf4j/logback/appenders-include.xml`

</div>

<div class="admonitionblock warning">

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Warning
</div></td>
<td class="content">As of SBDG <code>3.0</code>, the
<code>logback-include.xml</code> file was removed.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

The `properties-include.xml` defines Logback "*local*" scoped properties
or variables common to SBDG’s configuration of {pivotal-gemfire-name}
logging.

</div>

<div class="exampleblock">

<div class="title">

Example 9. properties-include.xml

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
<?xml version="1.0" encoding="UTF-8"?>
<included>

    <property name="SPRING_BOOT_LOG_CHARSET" value="${SPRING_BOOT_LOG_CHARSET:-${file.encoding:-UTF-8}}"/>
    <property name="SPRING_BOOT_LOG_PATTERN" value="${SPRING_BOOT_LOG_PATTERN:-%d %5p %40.40c:%4L - %msg%n}"/>
    <property name="APACHE_GEODE_LOG_CHARSET" value="${APACHE_GEODE_LOG_CHARSET:-${file.encoding:-UTF-8}}"/>
    <property name="APACHE_GEODE_LOG_PATTERN" value="${APACHE_GEODE_LOG_PATTERN:-[%level{lowerCase=true} %date{yyyy/MM/dd HH:mm:ss.SSS z} &lt;%thread&gt;] %message%n%throwable%n}"/>

</included>
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The `loggers-include.xml` file defines the `Loggers` used to log output
from {pivotal-gemfire-name} components.

</div>

<div class="exampleblock">

<div class="title">

Example 10. loggers-include.xml

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
<?xml version="1.0" encoding="UTF-8"?>
<included>

    <logger name="com.gemstone.gemfire" level="${spring.boot.data.gemfire.log.level:-INFO}" additivity="false">
        <appender-ref ref="${spring.geode.logging.appender-ref:-CONSOLE}"/>
        <appender-ref ref="delegate"/>
    </logger>

    <logger name="org.apache.geode" level="${spring.boot.data.gemfire.log.level:-INFO}" additivity="false">
        <appender-ref ref="${spring.geode.logging.appender-ref:-CONSOLE}"/>
        <appender-ref ref="delegate"/>
    </logger>

    <logger name="org.jgroups" level="${spring.boot.data.gemfire.jgroups.log.level:-WARN}" additivity="false">
        <appender-ref ref="${spring.geode.logging.appender-ref:-CONSOLE}"/>
        <appender-ref ref="delegate"/>
    </logger>

</included>
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The `appenders-include.xml` file defines Appenders to send the log
output to. If Spring Boot is on the application classpath, then Spring
Boot logging configuration will define the "CONSOLE" `Appender`,
otherwise, SBDG will provide a default definition.

</div>

<div class="paragraph">

The "geode" `Appender` defines the {pivotal-gemfire-name} logging
pattern as seen in {pivotal-gemfire-name}'s Log4j configuration.

</div>

<div class="exampleblock">

<div class="title">

Example 11. appenders-include.xml

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
<?xml version="1.0" encoding="UTF-8"?>
<included>

    <if condition='property("bootPresent").equals("false")'>
        <then>
            <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
                <encoder>
                    <charset>${SPRING_BOOT_LOG_CHARSET}</charset>
                    <pattern>${SPRING_BOOT_LOG_PATTERN}</pattern>
                </encoder>
            </appender>
        </then>
    </if>

    <appender name="delegate" class="org.springframework.geode.logging.slf4j.logback.DelegatingAppender"/>

    <appender name="geode" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <charset>${APACHE_GEODE_LOG_CHARSET}</charset>
            <pattern>${APACHE_GEODE_LOG_PATTERN}</pattern>
        </encoder>
    </appender>

</included>
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Then you can include any of SBDG’S Logback configuration metadata files
as needed in your application-specific Logback XML configuration file,
as follows:

</div>

<div class="exampleblock">

<div class="title">

Example 12. application-specific logback.xml

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <include resource="org/springframework/geode/logging/slf4j/logback/properties-include.xml"/>
    <include resource="org/springframework/geode/logging/slf4j/logback/appender-include.xml"/>

    <logger name="org.apache.geode" level="INFO" additivity="false">
        <appender-ref ref="geode"/>
    </logger>

    <root level="${logback.root.log.level:-INFO}">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="delegate"/>
    </root>

</configuration>
```

</div>

</div>

</div>

</div>

</div>

<div class="sect3">

#### Customizing Logging Configuration

<div class="paragraph">

It is now possible to customize the configuration of
{pivotal-gemfire-name} logging using properties defined in a
`spring-geode-logging.properties` file included on the Spring Boot
application classpath.

</div>

<div class="paragraph">

Any of the properties defined in
`org/springframework/geode/logging/slf4j/logback/properties-include.xml`
(shown above), such as `APACHE_GEODE_LOG_PATTERN`, or the
`spring.geode.logging.appender-ref` property, can be set.

</div>

<div class="paragraph">

For instance, and by default, {pivotal-gemfire-name} components log
output using the Spring Boot log pattern. However, if you prefer the
fine-grained details of Apache Geode’s logging behavior, you can change
the `Appender` used by the {pivotal-gemfire-name} `Logger’s` to use the
pre-defined "geode" `Appender` instead. Simply set the
`spring-geode.logging.appender-ref` property to "geode" in a
`spring-geode-logging.properties` file on your Spring Boot application
classpath, as follows:

</div>

<div class="exampleblock">

<div class="title">

Example 13. spring-geode-logging.properties

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# spring-geode-logging.properties
spring.geode.logging.appender-ref=geode
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Alternatively, if you want to configure the log output of your entire
Spring Boot application, including log output from all
{pivotal-gemfire-name} components, then you can set the
`SPRING_BOOT_LOG_PATTERN` property, or Spring Boot’s
`CONSOLE_LOG_PATTERN` property, in `spring-geode-logging.properties`, as
follows:

</div>

<div class="exampleblock">

<div class="title">

Example 14. spring-geode-logging.properties

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# spring-geode-logging.properties
CONSOLE_LOG_PATTERN=TEST - %msg%n
```

</div>

</div>

</div>

</div>

<div class="admonitionblock note">

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Note
</div></td>
<td class="content">The <code>spring-geode-logging.properties</code>
file is only recognized when the
<code>spring-geode-starter-logging</code> module is used.</td>
</tr>
</tbody>
</table>

</div>

</div>

</div>

<div class="sect2">

### SLF4J and Logback API Support

<div class="paragraph">

SBDG provides additional support when working with the SLF4J and Logback
APIs. This support is available when you declare the
`org.springframework.geode:spring-geode-starter-logging` dependency on
your Spring Boot application classpath.

</div>

<div class="paragraph">

One of the main supporting classes from the
`spring-geode-starter-logger` is the
`org.springframework.geode.logging.slf4j.logback.LogbackSupport` class.
This class provides methods to:

</div>

<div class="ulist">

- Resolve a reference to the Logback `LoggingContext`.

- Resolve the SLF4J ROOT `Logger` as a Logback `Logger`.

- Look up `Appenders` by name and required type.

- Add or remove `Appenders` to `Loggers`.

- Reset the state of the Logback logging system, which can prove to be
  most useful during testing.

</div>

<div class="paragraph">

`LogbackSupport` can even suppress the auto-configuration of Logback
performed by Spring Boot on startup, which is another useful utility
during automated testing.

</div>

<div class="paragraph">

In addition to the `LogbackSupport` class, SBDG also provides some
custom Logback `Appenders`.

</div>

<div class="sect3">

#### CompositeAppender

<div class="paragraph">

The `org.springframework.geode.logging.slf4j.logback.CompositeAppender`
class is an implementation of the Logback `Appender` interface and the
[Composite software design
pattern](https://en.wikipedia.org/wiki/Composite_pattern).

</div>

<div class="paragraph">

`CompositeAppender` lets developers compose multiple Appenders and use
them as if they were a single `Appender`.

</div>

<div class="paragraph">

For example, you could compose both the Logback `ConsoleAppender` and
`FileAppender` into one `Appender`:

</div>

<div class="exampleblock">

<div class="title">

Example 15. Composing multiple `Appenders`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
class LoggingConfiguration {

  Appender<ILoggingEvent> compositeAppender() {

    ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();

    FileAppender<ILoggingEvent> fileAppender = new FileApender<>();

    Appender<ILoggingEvent> compositeAppender = CompositeAppender.compose(consoleAppender, fileAppender);

    return compositeAppender;
  }
}

// do something with the compositeAppender
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You could then add the `CompositeAppender` to a named `Logger`:

</div>

<div class="exampleblock">

<div class="title">

Example 16. Register `CompositeAppender` on "named" `Logger`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
class LoggerConfiguration {

  void registerAppenderOnLogger() {

    Logger namedLogger = LoggerFactory.getLogger("loggerName");

    LogbackSupport.toLogbackLogger(namedLogger)
      .ifPresent(it -> LogbackSupport.addAppender(it, compositeAppender));
  }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

In this case, the named `Logger` logs events (or log messages) to both
the console and file Appenders.

</div>

<div class="paragraph">

You can compose an array or `Iterable` of `Appenders` by using either
the `CompositeAppender.compose(:Appender<T>[])` method or the
`CompositeAppender.compose(:Iterable<Appender<T>>)` method.

</div>

</div>

<div class="sect3">

#### DelegatingAppender

<div class="paragraph">

The `org.springframework.geode.logging.slf4j.logback.DelegatingAppender`
is a pass-through Logback `Appender` implementation that wraps another
Logback `Appender` or collection of `Appenders`, such as the
`ConsoleAppender`, a `FileAppender`, a `SocketAppender`, or others. By
default, the `DelegatingAppender` delegates to the `NOPAppender`,
thereby doing no actual work.

</div>

<div class="paragraph">

By default, SBDG registers the
`org.springframework.geode.logging.slfj4.logback.DelegatingAppender`
with the ROOT `Logger`, which can be useful for testing purposes.

</div>

<div class="paragraph">

With a reference to a `DelegatingAppender`, you can add any `Appender`
(even a `CompositeAppender`) as the delegate:

</div>

<div class="exampleblock">

<div class="title">

Example 17. Add `ConsoleAppender` as the "delegate" for the
`DelegatingAppender`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
class LoggerConfiguration {

  void setupDelegation() {

    ConsoleAppender consoleAppender = new ConsoleAppender();

    LogbackSupport.resolveLoggerContext().ifPresent(consoleAppender::setContext);

    consoleAppender.setImmediateFlush(true);
    consoleAppender.start();

    LogbackSupport.resolveRootLogger()
      .flatMap(LogbackSupport::toLogbackLogger)
      .flatMap(rootLogger -> LogbackSupport.resolveAppender(rootLogger,
        LogbackSupport.DELEGATE_APPENDER_NAME, DelegatingAppender.class))
      .ifPresent(delegateAppender -> delegateAppender.setAppender(consoleAppender));
  }
}
```

</div>

</div>

</div>

</div>

</div>

<div class="sect3">

#### StringAppender

<div class="paragraph">

The `org.springframework.geode.logging.slf4j.logback.StringAppender`
stores a log message in-memory, appended to a `String`.

</div>

<div class="paragraph">

The `StringAppender` is useful for testing purposes. For instance, you
can use the `StringAppender` to assert that a `Logger` used by certain
application components logged messages at the appropriately configured
log level while other log messages were not logged.

</div>

<div class="paragraph">

Consider the following example:

</div>

<div class="exampleblock">

<div class="title">

Example 18. `StringAppender` in Action

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
class ApplicationComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void someMethod() {
        logger.debug("Some debug message");
        // ...
    }

    public void someOtherMethod() {
        logger.info("Some info message");
    }
}

// Assuming the ApplicationComponent Logger was configured with log-level 'INFO', then...
class ApplicationComponentUnitTests {

    private final ApplicationComponent applicationComponent = new ApplicationComponent();

    private final Logger logger = LoggerFactory.getLogger(ApplicationComponent.class);

    private StringAppender stringAppender;

    @Before
    public void setup() {

        LogbackSupport.toLogbackLogger(logger)
            .map(Logger::getLevel)
            .ifPresent(level -> assertThat(level).isEqualTo(Level.INFO));

        stringAppender = new StringAppender.Builder()
            .applyTo(logger)
            .build();
    }

    @Test
    public void someMethodDoesNotLogDebugMessage() {

        applicationComponent.someMethod();

        assertThat(stringAppender.getLogOutput).doesNotContain("Some debug message");
    }

    @Test
    public void someOtherMethodLogsInfoMessage() {

        applicationComponent.someOtherMethod();

        assertThat(stringAppender.getLogOutput()).contains("Some info message");
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

There are many other uses for the `StringAppender` and you can use it
safely in a multi-Threaded context by calling
`StringAppender.Builder.useSynchronization()`.

</div>

<div class="paragraph">

When combined with other SBDG provided `Appenders` in conjunction with
the `LogbackSupport` class, you have a lot of power both in application
code as well as in your tests.

</div>

</div>

</div>

</div>

</div>

</div>

<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:14:14 -0700

</div>

</div>
