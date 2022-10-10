<div id="header">

</div>

<div id="content">

<div class="sect1">

## Auto-configuration vs. Annotation-based configuration

<div class="sectionbody">

<div class="paragraph">

The question most often asked is, “What Spring Data for
{apache-geode-name} (SDG) annotations can I use, or must I use, when
developing {apache-geode-name} applications with Spring Boot?”

</div>

<div class="paragraph">

This section answers this question and more.

</div>

<div class="paragraph">

See the complementary sample, [Spring Boot Auto-configuration for
{apache-geode-name}](guides/boot-configuration.html), which shows the
auto-configuration provided by Spring Boot for {apache-geode-name} in
action.

</div>

<div class="sect2">

### Background

<div class="paragraph">

To help answer this question, you must start by reviewing the complete
collection of available Spring Data for {apache-geode-name} (SDG)
annotations. These annotations are provided in the
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/package-summary.html\[`org.springframework.data.gemfire.config.annotation`\]
package. Most of the essential annotations begin with `@Enable…​`, except
for the base annotations: `@ClientCacheApplication`,
`@PeerCacheApplication` and `@CacheServerApplication`.

</div>

<div class="paragraph">

By extension, Spring Boot for {apache-geode-name} (SBDG) builds on SDG’s
annotation-based configuration model to implement auto-configuration and
apply Spring Boot’s core concepts, such as “convention over
configuration”, letting {apache-geode-name} applications be built with
Spring Boot reliably, quickly, and easily.

</div>

<div class="paragraph">

SDG provides this annotation-based configuration model to, first and
foremost, give application developers “choice” when building Spring
applications with {apache-geode-name}. SDG makes no assumptions about
what application developers are trying to create and fails fast anytime
the configuration is ambiguous, giving users immediate feedback.

</div>

<div class="paragraph">

Second, SDG’s annotations were meant to get application developers up
and running quickly and reliably with ease. SDG accomplishes this by
applying sensible defaults so that application developers need not know,
or even have to learn, all the intricate configuration details and
tooling provided by {apache-geode-name} to accomplish simple tasks, such
as building a prototype.

</div>

<div class="paragraph">

So, SDG is all about “choice” and SBDG is all about “convention”.
Together these frameworks provide application developers with
convenience and ease to move quickly and reliably.

</div>

<div class="paragraph">

To learn more about the motivation behind SDG’s annotation-based
configuration model, see the
{spring-data-gemfire-docs-html}/#bootstrap-annotation-config-introduction\[Reference
Documentation\].

</div>

</div>

<div class="sect2">

### Conventions

<div class="paragraph">

Currently, SBDG provides auto-configuration for the following features:

</div>

<div class="ulist">

- `ClientCache`

- Caching with Spring’s Cache Abstraction

- Continuous Query

- Function Execution and Implementation

- Logging

- PDX

- `GemfireTemplate`

- Spring Data Repositories

- Security (Client/server auth and SSL)

- Spring Session

</div>

<div class="paragraph">

This means the following SDG annotations are not required to use the
features above:

</div>

<div class="ulist">

- `@ClientCacheApplication`

- `@EnableGemfireCaching` (or by using Spring Framework’s
  `@EnableCaching` annotation)

- `@EnableContinuousQueries`

- `@EnableGemfireFunctionExecutions`

- `@EnableGemfireFunctions`

- `@EnableLogging`

- `@EnablePdx`

- `@EnableGemfireRepositories`

- `@EnableSecurity`

- `@EnableSsl`

- `@EnableGemFireHttpSession`

</div>

<div class="paragraph">

Since SBDG auto-configures these features for you, the above annotations
are not strictly required. Typically, you would only declare one of
these annotations when you want to “override” Spring Boot’s conventions,
as expressed in auto-configuration, and “customize” the behavior of the
feature.

</div>

</div>

<div class="sect2">

### Overriding

<div class="paragraph">

In this section, we cover a few examples to make the behavior when
overriding more apparent.

</div>

<div class="sect3">

#### Caches

<div class="paragraph">

By default, SBDG provides you with a `ClientCache` instance. SBDG
accomplishes this by annotating an auto-configuration class with
`@ClientCacheApplication` internally.

</div>

<div class="paragraph">

By convention, we assume most application developers' are developing
Spring Boot applications by using {apache-geode-name} as “client”
applications in {apache-geode-name}'s client/server topology. This is
especially true as users migrate their applications to a managed cloud
environment.

</div>

<div class="paragraph">

Still, you can “override” the default settings (convention) and declare
your Spring applications to be actual peer `Cache` members (nodes) of a
{apache-geode-name} cluster, instead:

</div>

<div class="exampleblock">

<div class="title">

Example 1. Spring Boot, {apache-geode-name} Peer `Cache` Application

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@CacheServerApplication
class SpringBootApacheGeodePeerCacheServerApplication {  }
```

</div>

</div>

</div>

</div>

<div class="paragraph">

By declaring the `@CacheServerApplication` annotation, you effectively
override the SBDG default. Therefore, SBDG does not provide you with a
`ClientCache` instance by default, because you have informed SBDG of
exactly what you want: a peer `Cache` instance hosting an embedded
`CacheServer` that allows client connections.

</div>

<div class="paragraph">

However, you then might ask, “Well, how do I customize the `ClientCache`
instance when developing client applications without explicitly
declaring the `@ClientCacheApplication` annotation?”

</div>

<div class="paragraph">

First, you can “customize” the `ClientCache` instance by explicitly
declaring the `@ClientCacheApplication` annotation in your Spring Boot
application configuration and setting specific attributes as needed.
However, you should be aware that, by explicitly declaring this
annotation, (or, by default, any of the other auto-configured
annotations), you assume all the responsibility that comes with it,
since you have effectively overridden the auto-configuration. One
example of this is security, which we touch on more later.

</div>

<div class="paragraph">

The most ideal way to “customize” the configuration of any feature is by
way of the well-known and documented
[properties](#geode-configuration-metadata), specified in Spring Boot
`application.properties` (the “convention”), or by using a
{spring-data-gemfire-docs-html}/#bootstrap-annotation-config-configurers\[`Configurer`\].

</div>

<div class="paragraph">

See the [Reference Guide](#geode-clientcache-applications) for more
detail.

</div>

</div>

<div class="sect3">

#### Security

<div class="paragraph">

As with the `@ClientCacheApplication` annotation, the `@EnableSecurity`
annotation is not strictly required, unless you want to override and
customize the defaults.

</div>

<div class="paragraph">

Outside a managed environment, the only security configuration required
is specifying a username and password. You do this by using the
well-known and documented SDG username and password properties in Spring
Boot `application.properties`:

</div>

<div class="exampleblock">

<div class="title">

Example 2. Required Security Properties in a Non-Manage Envionment

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
spring.data.gemfire.security.username=MyUser
spring.data.gemfire.security.password=Secret
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You need not explicitly declare the `@EnableSecurity` annotation just to
specify security configuration (such as username and password).

</div>

<div class="paragraph">

Inside a managed environment, such as the {pivotal-cloudfoundry-name}
when using {pivotal-gemfire-name}, SBDG is able to introspect the
environment and configure security (auth) completely without the need to
specify any configuration, usernames and passwords, or otherwise. This
is due, in part, because TAS supplies the security details in the VCAP
environment when the application is deployed to TAS and bound to
services (such as {pivotal-gemfire-name}).

</div>

<div class="paragraph">

So, in short, you need not explicitly declare the `@EnableSecurity`
annotation (or `@ClientCacheApplication`).

</div>

<div class="paragraph">

However, if you do explicitly declare the `@ClientCacheApplication` or
`@EnableSecurity` annotations, you are now responsible for this
configuration, and SBDG’s auto-configuration no longer applies.

</div>

<div class="paragraph">

While explicitly declaring `@EnableSecurity` makes more sense when
“overriding” the SBDG security auto-configuration, explicitly declaring
the `@ClientCacheApplication` annotation most likely makes less sense
with regard to its impact on security configuration.

</div>

<div class="paragraph">

This is entirely due to the internals of {apache-geode-name}, because,
in certain cases (such as security), not even Spring is able to
completely shield you from the nuances of {apache-geode-name}'s
configuration. No framework can.

</div>

<div class="paragraph">

You must configure both auth and SSL before the cache instance (whether
a `ClientCache` or a peer `Cache`) is created. This is because security
is enabled and configured during the “construction” of the cache. Also,,
the cache pulls the configuration from JVM System properties that must
be set before the cache is constructed.

</div>

<div class="paragraph">

Structuring the “exact” order of the auto-configuration classes provided
by SBDG when the classes are triggered, is no small feat. Therefore, it
should come as no surprise to learn that the security auto-configuration
classes in SBDG must be triggered before the `ClientCache`
auto-configuration class, which is why a `ClientCache` instance cannot
“auto” authenticate properly in PCC when the `@ClientCacheApplication`
is explicitly declared without some assistance. In other words you must
also explicitly declare the `@EnableSecurity` annotation in this case,
since you overrode the auto-configuration of the cache, and implicitly
security, as well.

</div>

<div class="paragraph">

Again, this is due to the way security (auth) and SSL metadata must be
supplied to {apache-geode-name} on startup.

</div>

<div class="paragraph">

See the [Reference Guide](#geode-security) for more details.

</div>

</div>

</div>

<div class="sect2">

### Extension

<div class="paragraph">

Most of the time, many of the other auto-configured annotations for CQ,
Functions, PDX, Repositories, and so on need not ever be declared
explicitly.

</div>

<div class="paragraph">

Many of these features are enabled automatically by having SBDG or other
libraries (such as Spring Session) on the application classpath or are
enabled based on other annotations applied to beans in the Spring
`ApplicationContext`.

</div>

<div class="paragraph">

We review a few examples in the following sections.

</div>

<div class="sect3">

#### Caching

<div class="paragraph">

It is rarely, if ever, necessary to explicitly declare either the Spring
Framework’s `@EnableCaching` or the SDG-specific `@EnableGemfireCaching`
annotation in Spring configuration when you use SBDG. SBDG automatically
enables caching and configures the SDG `GemfireCacheManager` for you.

</div>

<div class="paragraph">

You need only focus on which application service components are
appropriate for caching:

</div>

<div class="exampleblock">

<div class="title">

Example 3. Service Caching

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Service
class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Cacheable("CustomersByName")
    public Customer findBy(String name) {
        return customerRepository.findByName(name);
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You need to create {apache-geode-name} Regions that back the caches
declared in your application service components (`CustomersByName` in
the preceding example) by using Spring’s caching annotations (such as
`@Cacheable`), or alternatively, JSR-107 JCache annotations (such as
`@CacheResult`).

</div>

<div class="paragraph">

You can do that by defining each Region explicitly or, more
conveniently, you can use the following approach:

</div>

<div class="exampleblock">

<div class="title">

Example 4. Configuring Caches (Regions)

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@EnableCachingDefinedRegions
class Application {  }
```

</div>

</div>

</div>

</div>

<div class="paragraph">

`@EnableCachingDefinedRegions` is optional, provided for convenience,
and complementary to caching when used rather than being necessary.

</div>

<div class="paragraph">

See the [Reference Guide](#geode-caching-provider) for more detail.

</div>

</div>

<div class="sect3">

#### Continuous Query

<div class="paragraph">

It is rarely, if ever, necessary to explicitly declare the SDG
`@EnableContinuousQueries` annotation. Instead, you should focus on
defining your application queries and worry less about the plumbing.

</div>

<div class="paragraph">

Consider the following example:

</div>

<div class="exampleblock">

<div class="title">

Example 5. Defining Queries for CQ

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Component
public class TemperatureMonitor extends AbstractTemperatureEventPublisher {

    @ContinuousQuery(name = "BoilingTemperatureMonitor",
        query = "SELECT * FROM /TemperatureReadings WHERE temperature.measurement >= 212.0")
    public void boilingTemperatureReadings(CqEvent event) {
        publish(event, temperatureReading -> new BoilingTemperatureEvent(this, temperatureReading));
    }

    @ContinuousQuery(name = "FreezingTemperatureMonitor",
        query = "SELECT * FROM /TemperatureReadings WHERE temperature.measurement <= 32.0")
    public void freezingTemperatureReadings(CqEvent event) {
        publish(event, temperatureReading -> new FreezingTemperatureEvent(this, temperatureReading));
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

{apache-geode-name} CQ applies only to clients.

</div>

<div class="paragraph">

See the [Reference Guide](#geode-continuous-query) for more detail.

</div>

</div>

<div class="sect3">

#### Functions

<div class="paragraph">

You rarely, if ever, need to explicitly declare either the
`@EnableGemfireFunctionExecutions` or `@EnableGemfireFunctions`
annotations. SBDG provides auto-configuration for both Function
implementations and executions.

</div>

<div class="paragraph">

You need to define the implementation:

</div>

<div class="exampleblock">

<div class="title">

Example 6. Function Implementation

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Component
class GeodeFunctions {

    @GemfireFunction
    Object exampleFunction(Object arg) {
        // ...
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Then you need to define the execution:

</div>

<div class="exampleblock">

<div class="title">

Example 7. Function Execution

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@OnRegion(region = "Example")
interface GeodeFunctionExecutions {

    Object exampleFunction(Object arg);

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

SBDG automatically finds, configures, and registers Function
implementations (POJOs) in {apache-geode-name} as proper `Functions` and
creates execution proxies for the interfaces, which can then be injected
into application service components to invoke the registered `Functions`
without needing to explicitly declare the enabling annotations. The
application Function implementations (POJOs) and executions (interfaces)
should exist below the `@SpringBootApplication` annotated main class.

</div>

<div class="paragraph">

See the [Reference Guide](#geode-functions) for more detail.

</div>

</div>

<div class="sect3">

#### PDX

<div class="paragraph">

You rarely, if ever, need to explicitly declare the `@EnablePdx`
annotation, since SBDG auto-configures PDX by default. SBDG also
automatically configures the SDG `MappingPdxSerializer` as the default
`PdxSerializer`.

</div>

<div class="paragraph">

It is easy to customize the PDX configuration by setting the appropriate
[properties](#geode-configuration-metadata) (search for “PDX”) in Spring
Boot `application.properties`.

</div>

<div class="paragraph">

See the [Reference Guide](#geode-data-serialization) for more detail.

</div>

</div>

<div class="sect3">

#### Spring Data Repositories

<div class="paragraph">

You rarely, if ever, need to explicitly declare the
`@EnableGemfireRepositories` annotation, since SBDG auto-configures
Spring Data (SD) Repositories by default.

</div>

<div class="paragraph">

You need only define your Repositories:

</div>

<div class="exampleblock">

<div class="title">

Example 8. Customer’s Repository

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
interface CustomerRepository extends CrudRepository<Customer, Long> {

    Customer findByName(String name);

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

SBDG finds the Repository interfaces defined in your application,
proxies them, and registers them as beans in the Spring
`ApplicationContext`. The Repositories can be injected into other
application service components.

</div>

<div class="paragraph">

It is sometimes convenient to use the `@EnableEntityDefinedRegions`
along with Spring Data Repositories to identify the entities used by
your application and define the Regions used by the Spring Data
Repository infrastructure to persist the entity’s state. The
`@EnableEntityDefinedRegions` annotation is optional, provided for
convenience, and complementary to the `@EnableGemfireRepositories`
annotation.

</div>

<div class="paragraph">

See the [Reference Guide](#geode-repositories) for more detail.

</div>

</div>

</div>

<div class="sect2">

### Explicit Configuration

<div class="paragraph">

Most of the other annotations provided in SDG are focused on particular
application concerns or enable certain {apache-geode-name} features,
rather than being a necessity, including:

</div>

<div class="ulist">

- `@EnableAutoRegionLookup`

- `@EnableBeanFactoryLocator`

- `@EnableCacheServer(s)`

- `@EnableCachingDefinedRegions`

- `@EnableClusterConfiguration`

- `@EnableClusterDefinedRegions`

- `@EnableCompression`

- `@EnableDiskStore(s)`

- `@EnableEntityDefinedRegions`

- `@EnableEviction`

- `@EnableExpiration`

- `@EnableGatewayReceiver`

- `@EnableGatewaySender(s)`

- `@EnableGemFireAsLastResource`

- `@EnableHttpService`

- `@EnableIndexing`

- `@EnableOffHeap`

- `@EnableLocator`

- `@EnableManager`

- `@EnableMemcachedServer`

- `@EnablePool(s)`

- `@EnableRedisServer`

- `@EnableStatistics`

- `@UseGemFireProperties`

</div>

<div class="paragraph">

None of these annotations are necessary and none are auto-configured by
SBDG. They are at your disposal when and if you need them. This also
means that none of these annotations are in conflict with any SBDG
auto-configuration.

</div>

</div>

<div class="sect2">

### Summary

<div class="paragraph">

In conclusion, you need to understand where SDG ends and SBDG begins. It
all begins with the auto-configuration provided by SBDG.

</div>

<div class="paragraph">

If a feature or function is not covered by SBDG’s auto-configuration,
you are responsible for enabling and configuring the feature
appropriately, as needed by your application (for example,
`@EnableRedisServer`).

</div>

<div class="paragraph">

In other cases, you might also want to explicitly declare a
complimentary annotation (such as `@EnableEntityDefinedRegions`) for
convenience, since SBDG provides no convention or opinion.

</div>

<div class="paragraph">

In all remaining cases, it boils down to understanding how
{apache-geode-name} works under the hood. While we go to great lengths
to shield you from as many details as possible, it is not feasible or
practical to address all matters, such as cache creation and security.

</div>

</div>

</div>

</div>

</div>

<div id="footer">

<div id="footer-text">

Last updated 2022-10-06 11:36:31 -0700

</div>

</div>
