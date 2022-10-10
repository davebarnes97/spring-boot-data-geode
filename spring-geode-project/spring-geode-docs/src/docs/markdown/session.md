<div id="header">

</div>

<div id="content">

<div class="sect1">

## Spring Session

<div class="sectionbody">

<div class="paragraph">

This chapter covers auto-configuration of Spring Session for
{pivotal-gemfire-name} to manage (HTTP) session state in a reliable
(consistent), highly available (replicated), and clustered manner.

</div>

<div class="paragraph">

{spring-session-website}\[Spring Session\] provides an API and several
implementations for managing a user’s session information. It has the
ability to replace the `javax.servlet.http.HttpSession` in an
application container-neutral way and provide session IDs in HTTP
headers to work with RESTful APIs.

</div>

<div class="paragraph">

Furthermore, Spring Session provides the ability to keep the
`HttpSession` alive even when working with `WebSockets` and reactive
Spring WebFlux `WebSessions`.

</div>

<div class="paragraph">

A complete discussion of Spring Session is beyond the scope of this
document. You can learn more by reading the
{spring-session-docs}\[docs\] and reviewing the
{spring-session-docs}/#samples\[samples\].

</div>

<div class="paragraph">

Spring Boot for {pivotal-gemfire-name} provides auto-configuration
support to configure {pivotal-gemfire-name} as the session management
provider and store when {spring-session-data-gemfire-website}\[Spring
Session for {pivotal-gemfire-name}\] is on your Spring Boot
application’s classpath.

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
<td class="content">You can learn more about Spring Session for
{pivotal-gemfire-name} in the
{spring-session-data-gemfire-docs}[docs].</td>
</tr>
</tbody>
</table>

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
<td class="content">See the corresponding sample <a
href="guides/caching-http-session.html.html">guide</a> and
{github-samples-url}/caching/http-session[code] to see Spring Session
for {pivotal-gemfire-name} in action.</td>
</tr>
</tbody>
</table>

</div>

<div class="sect2">

### Configuration

<div class="paragraph">

You need do nothing special to use {pivotal-gemfire-name} as a Spring
Session provider implementation, managing the (HTTP) session state of
your Spring Boot application.

</div>

<div class="paragraph">

To do so, include the appropriate Spring Session dependency on your
Spring Boot application’s classpath:

</div>

<div class="exampleblock">

<div class="title">

Example 1. Maven dependency declaration

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
  <dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-geode</artifactId>
    <version>{spring-session-data-geode-version}</version>
  </dependency>
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Alternatively, you may declare the provided
`spring-geode-starter-session` dependency in your Spring Boot
application Maven POM (shown here) or Gradle build file:

</div>

<div class="exampleblock">

<div class="title">

Example 2. Maven dependency declaration

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
  <dependency>
    <groupId>org.springframework.geode</groupId>
    <artifactId>spring-geode-starter-session</artifactId>
    <version>1.27</version>
  </dependency>
```

</div>

</div>

</div>

</div>

<div class="paragraph">

After declaring the required Spring Session dependency, you can begin
your Spring Boot application as you normally would:

</div>

<div class="exampleblock">

<div class="title">

Example 3. Spring Boot Application

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
public class MySpringBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(MySpringBootApplication.class, args);
  }

  // ...
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You can then create application-specific Spring Web MVC `Controllers` to
interact with the `HttpSession` as needed by your application:

</div>

<div class="exampleblock">

<div class="title">

Example 4. Spring Boot Application `Controller` using `HttpSession`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Controller
class MyApplicationController {

  @GetRequest("...")
  public String processGet(HttpSession session) {
    // interact with HttpSession
  }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The `HttpSession` is replaced by a Spring managed `Session` that is
stored in {pivotal-gemfire-name}.

</div>

</div>

<div class="sect2">

### Custom Configuration

<div class="paragraph">

By default, Spring Boot for {pivotal-gemfire-name} (SBDG) applies
reasonable and sensible defaults when configuring {pivotal-gemfire-name}
as the provider in Spring Session.

</div>

<div class="paragraph">

For instance, by default, SBDG sets the session expiration timeout to 30
minutes. It also uses a `ClientRegionShortcut.PROXY` as the data
management policy for the {pivotal-gemfire-name} client Region that
managing the (HTTP) session state when the Spring Boot application is
using a `ClientCache`, which it does by
[default](#geode-clientcache-applications).

</div>

<div class="paragraph">

However, what if the defaults are not sufficient for your application
requirements?

</div>

<div class="paragraph">

In that case, see the next section.

</div>

<div class="sect3">

#### Custom Configuration using Properties

<div class="paragraph">

Spring Session for {pivotal-gemfire-name} publishes
{spring-session-data-gemfire-docs}/#httpsession-gemfire-configuration-properties\[well-known
configuration properties\] for each of the various Spring Session
configuration options when you use {pivotal-gemfire-name} as the (HTTP)
session state management provider.

</div>

<div class="paragraph">

You can specify any of these properties in Spring Boot
`application.properties` to adjust Spring Session’s configuration when
using {pivotal-gemfire-name}.

</div>

<div class="paragraph">

In addition to the properties provided in and by Spring Session for
{pivotal-gemfire-name}, Spring Boot for {pivotal-gemfire-name} also
recognizes and respects the `spring.session.timeout` property and the
`server.servlet.session.timeout` property, as discussed
{spring-boot-docs-html}/boot-features-session.html\[the Spring Boot
documentation\].

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
<td
class="content"><code>spring.session.data.gemfire.session.expiration.max-inactive-interval-seconds</code>
takes precedence over <code>spring.session.timeout</code>, which takes
precedence over <code>server.servlet.session.timeout</code> when any
combination of these properties have been simultaneously configured in
the Spring <code>Environment</code> of your application.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### Custom Configuration using a Configurer

<div class="paragraph">

Spring Session for {pivotal-gemfire-name} also provides the
{spring-session-data-gemfire-javadoc}/org/springframework/session/data/gemfire/config/annotation/web/http/support/SpringSessionGemFireConfigurer.html\[`SpringSessionGemFireConfigurer`\]
callback interface, which you can declare in your Spring
`ApplicationContext` to programmatically control the configuration of
Spring Session when you use {pivotal-gemfire-name}.

</div>

<div class="paragraph">

The `SpringSessionGemFireConfigurer`, when declared in the Spring
`ApplicationContext`, takes precedence over any of the Spring Session
(for {pivotal-gemfire-name}) configuration properties and effectively
overrides them when both are present.

</div>

<div class="paragraph">

More information on using the `SpringSessionGemFireConfigurer` can be
found in the
{spring-session-data-gemfire-docs}/#httpsession-gemfire-configuration-configurer\[docs\].

</div>

</div>

</div>

<div class="sect2">

### Disabling Session State Caching

<div class="paragraph">

There may be cases where you do not want your Spring Boot application to
manage (HTTP) session state by using {pivotal-gemfire-name}.

</div>

<div class="paragraph">

In certain cases, you may be using another Spring Session provider
implementation, such as Redis, to cache and manage your Spring Boot
application’s (HTTP) session state. In other cases, you do not want to
use Spring Session to manage your (HTTP) session state at all. Rather,
you prefer to use your Web Server’s (such as Tomcat’s) built-in
`HttpSession` state management capabilities.

</div>

<div class="paragraph">

Either way, you can specifically call out your Spring Session provider
implementation by using the `spring.session.store-type` property in
Spring Boot `application.properties`:

</div>

<div class="exampleblock">

<div class="title">

Example 5. Use Redis as the Spring Session Provider Implementation

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
#application.properties

spring.session.store-type=redis
...
```

</div>

</div>

</div>

</div>

<div class="paragraph">

If you prefer not to use Spring Session to manage your Spring Boot
application’s (HTTP) session state at all, you can do the following:

</div>

<div class="exampleblock">

<div class="title">

Example 6. Use Web Server Session State Management

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
#application.properties

spring.session.store-type=none
...
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Again, see the Spring Boot
{spring-boot-docs-html}/boot-features-session.html\[documentation\] for
more detail.

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
<td class="content">You can include multiple provider implementations on
the classpath of your Spring Boot application. For instance, you might
use Redis to cache your application’s (HTTP) session state while using
{pivotal-gemfire-name} as your application’s transactional persistent
store (System of Record).</td>
</tr>
</tbody>
</table>

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
<td class="content">Spring Boot does not properly recognize
<code>spring.session.store-type=[gemfire|geode]</code> even though
Spring Boot for {pivotal-gemfire-name} is set up to handle either of
these property values (that is, either <code>gemfire</code> or
<code>geode</code>).</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect2">

### Using Spring Session with {pivotal-cloudcache-name} (PCC)

<div class="paragraph">

Whether you use Spring Session in a Spring Boot, {pivotal-gemfire-name}
`ClientCache` application to connect to an standalone, externally
managed cluster of {pivotal-gemfire-name} servers or to connect to a
cluster of servers in a {pivotal-cloudcache-name} service instance
managed by a {pivotal-cloudfoundry-name} environment, the setup is the
same.

</div>

<div class="paragraph">

Spring Session for {pivotal-gemfire-name} expects there to be a cache
Region in the cluster that can store and manage (HTTP) session state
when your Spring Boot application is a `ClientCache` application in the
client/server topology.

</div>

<div class="paragraph">

By default, the cache Region used to store and manage (HTTP) session
state is called `ClusteredSpringSessions`.

</div>

<div class="paragraph">

We recommend that you configure the cache Region name by using the
well-known and documented property in Spring Boot
`application.properties`:

</div>

<div class="exampleblock">

<div class="title">

Example 7. Using properties

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
spring.session.data.gemfire.session.region.name=MySessions
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Alternatively, you can set the name of the cache Region used to store
and manage (HTTP) session state by explicitly declaring the
`@EnableGemFireHttpSession` annotation on your main
`@SpringBootApplication` class:

</div>

<div class="exampleblock">

<div class="title">

Example 8. Using \`@EnableGemfireHttpSession

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@EnableGemFireHttpSession(regionName = "MySessions")
class MySpringBootSpringSessionApplication {
    // ...
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Once you decide on the cache Region name used to store and manage (HTTP)
sessions, you must create the cache Region in the cluster somehow.

</div>

<div class="paragraph">

On the client, doing so is simple, since SBDG’s auto-configuration
automatically creates the client `PROXY` Region that is used to send and
receive (HTTP) session state between the client and server for you when
either Spring Session is on the application classpath (for example,
`spring-geode-starter-session`) or you explicitly declare the
`@EnableGemFireHttpSession` annotation on your main
`@SpringBootApplication` class.

</div>

<div class="paragraph">

However, on the server side, you currently have a couple of options.

</div>

<div class="paragraph">

First, you can manually create the cache Region by using Gfsh:

</div>

<div class="exampleblock">

<div class="title">

Example 9. Create the Sessions Region using Gfsh

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
gfsh> create region --name=MySessions --type=PARTITION --entry-idle-time-expiration=1800
        --entry-idle-time-expiration-action=INVALIDATE
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You must create the cache Region with the appropriate name and an
expiration policy.

</div>

<div class="paragraph">

In this case, we created an idle expiration policy with a timeout of
`1800` seconds (30 minutes), after which the entry (session object) is
`invalidated`.

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
<td class="content">Session expiration is managed by the Expiration
Policy set on the cache Region that is used to store session state. The
Servlet container’s (HTTP) session expiration configuration is not used,
since Spring Session replaces the Servlet container’s session management
capabilities with its own, and Spring Session delegates this behavior to
the individual providers, such as {pivotal-gemfire-name}.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Alternatively, you could send the definition for the cache Region from
your Spring Boot `ClientCache` application to the cluster by using the
SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/config/annotation/EnableClusterAware.html\[`@EnableClusterAware`\]
annotation, which is meta-annotated with SDG’s
`@EnableClusterConfiguration` annotation:

</div>

<div class="exampleblock">

<div class="title">

Example 10. Using `@EnableClusterAware`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@EnableClusterAware
class MySpringBootSpringSessionApacheGeodeApplication {
    // ...
}
```

</div>

</div>

</div>

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
<td class="content">See the
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html[Javadoc]
on the <code>@EnableClusterConfiguration</code> annotation and the
{spring-data-geode-docs-html}/#bootstrap-annotation-config-cluster[documentation]
for more detail.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

However, you cannot currently send expiration policy configuration
metadata to the cluster. Therefore, you must manually alter the cache
Region to set the expiration policy:

</div>

<div class="exampleblock">

<div class="title">

Example 11. Using Gfsh to Alter Region

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
gfsh> alter region --name=MySessions --entry-idle-time-expiration=1800
        --entry-idle-time-expiration-action=INVALIDATE
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Now your Spring Boot `ClientCache` application that uses Spring Session
in a client/server topology is configured to store and manage user
(HTTP) session state in the cluster. This works for either standalone,
externally managed {pivotal-gemfire-name} clusters or when you use PCC
running in a {pivotal-cloudfoundry-name} environment.

</div>

</div>

</div>

</div>

</div>

<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:15:09 -0700

</div>

</div>
