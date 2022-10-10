<div id="header">

</div>

<div id="content">

<div class="sect1">

## Auto-configuration

<div class="sectionbody">

<div class="paragraph">

The following Spring Framework, Spring Data for {pivotal-gemfire-name}
(SDG) and Spring Session for {pivotal-gemfire-name} (SSDG) annotations
are implicitly declared by Spring Boot for {pivotal-gemfire-name}'s
(SBDG) auto-configuration.

</div>

<div class="ulist">

- `@ClientCacheApplication`

- `@EnableGemfireCaching` (alternatively, Spring Framework’s
  `@EnableCaching`)

- `@EnableContinuousQueries`

- `@EnableGemfireFunctions`

- `@EnableGemfireFunctionExecutions`

- `@EnableGemfireRepositories`

- `@EnableLogging`

- `@EnablePdx`

- `@EnableSecurity`

- `@EnableSsl`

- `@EnableGemFireHttpSession`

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
<td class="content">This means that you need not explicitly declare any
of these annotations on your <code>@SpringBootApplication</code> class,
since they are provided by SBDG already. The only reason you would
explicitly declare any of these annotations is to override Spring
Boot’s, and in particular, SBDG’s auto-configuration. Otherwise, doing
so is unnecessary.</td>
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
<td class="content">You should read the chapter in Spring Boot’s
reference documentation on
{spring-boot-docs-html}/#using-boot-auto-configuration[auto-configuration].</td>
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
<td class="content">You should review the chapter in Spring Data for
{pivotal-gemfire-name}'s (SDG) reference documentation on
{spring-data-geode-docs-html}/#bootstrap-annotation-config[annotation-based
configuration]. For a quick reference and overview of annotation-based
configuration, see the
{spring-data-geode-docs-html}/#bootstap-annotations-quickstart[annotations
quickstart].</td>
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
href="guides/boot-configuration.html">guide</a> and
{github-samples-url}/boot/configuration[code] to see Spring Boot
auto-configuration for {pivotal-gemfire-name} in action.</td>
</tr>
</tbody>
</table>

</div>

<div class="sect2">

### Customizing Auto-configuration

<div class="paragraph">

You might ask, “How do I customize the auto-configuration provided by
SBDG if I do not explicitly declare the annotation?”

</div>

<div class="paragraph">

For example, you may want to customize the member’s name. You know that
the
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html\[`@ClientCacheApplication`\]
annotation provides the
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/EnableGemFireProperties.html#name--\[`name`\]
attribute so that you can set the client member’s name. However, SBDG
has already implicitly declared the `@ClientCacheApplication` annotation
through auto-configuration on your behalf. What do you do?

</div>

<div class="paragraph">

In this case, SBDG supplies a few additional annotations.

</div>

<div class="paragraph">

For example, to set the (client or peer) member’s name, you can use the
`@UseMemberName` annotation:

</div>

<div class="exampleblock">

<div class="title">

Example 1. Setting the member’s name using `@UseMemberName`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@UseMemberName("MyMemberName")
class SpringBootApacheGeodeClientCacheApplication {
    //...
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Alternatively, you could set the `spring.application.name` or the
`spring.data.gemfire.name` property in Spring Boot
`application.properties`:

</div>

<div class="exampleblock">

<div class="title">

Example 2. Setting the member’s name using the `spring.application.name`
property

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Spring Boot application.properties

spring.application.name = MyMemberName
```

</div>

</div>

</div>

</div>

<div class="exampleblock">

<div class="title">

Example 3. Setting the member’s name using the
`spring.data.gemfire.cache.name` property

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Spring Boot application.properties

spring.data.gemfire.cache.name = MyMemberName
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
<td class="content">The <code>spring.data.gemfire.cache.name</code>
property is an alias for the <code>spring.data.gemfire.name</code>
property. Both properties do the same thing (set the name of the client
or peer member node).</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

In general, there are three ways to customize configuration, even in the
context of SBDG’s auto-configuration:

</div>

<div class="ulist">

- Using
  {spring-boot-data-geode-javadoc}/org/springframework/geode/config/annotation/package-summary.html\[annotations\]
  provided by SBDG for common and popular concerns (such as naming
  client or peer members with the `@UseMemberName` annotation or
  enabling durable clients with the `@EnableDurableClient` annotation).

- Using well-known and documented
  {spring-data-geode-docs-html}/#bootstrap-annotation-config-properties\[properties\]
  (such as `spring.application.name`, or `spring.data.gemfire.name`, or
  `spring.data.gemfire.cache.name`).

- Using
  {spring-data-geode-docs-html}/#bootstrap-annotation-config-configurers\[configurers\]
  (such as
  {spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/ClientCacheConfigurer.html\[`ClientCacheConfigurer`\]).

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
<td class="content">For the complete list of documented properties, see
<a
href="#geode-configuration-metadata">[geode-configuration-metadata]</a>.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect2">

### Disabling Auto-configuration

<div class="paragraph">

Spring Boot’s reference documentation explains how to
{spring-boot-docs-html}/#using-boot-disabling-specific-auto-configuration\[disable
Spring Boot auto-configuration\].

</div>

<div class="paragraph">

[\[geode-auto-configuration-disable\]](#geode-auto-configuration-disable)
also explains how to disable SBDG auto-configuration.

</div>

<div class="paragraph">

In a nutshell, if you want to disable any auto-configuration provided by
either Spring Boot or SBDG, declare your intent in the
`@SpringBootApplication` annotation:

</div>

<div class="exampleblock">

<div class="title">

Example 4. Disabling Specific Auto-configuration Classes

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication(
  exclude = { DataSourceAutoConfiguration.class, PdxAutoConfiguration.class }
)
class SpringBootApacheGeodeClientCacheApplication {
    // ...
}
```

</div>

</div>

</div>

</div>

<div class="admonitionblock caution">

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Caution
</div></td>
<td class="content">Make sure you understand what you are doing when you
disable auto-configuration.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect2">

### Overriding Auto-configuration

<div class="paragraph">

[\[geode-autoconfiguration-annotations-overriding\]](#geode-autoconfiguration-annotations-overriding)
explains how to override SBDG auto-configuration.

</div>

<div class="paragraph">

In a nutshell, if you want to override the default auto-configuration
provided by SBDG, you must annotate your `@SpringBootApplication` class
with your intent.

</div>

<div class="paragraph">

For example, suppose you want to configure and bootstrap an
{pivotal-gemfire-name} `CacheServer` application (a peer, not a client):

</div>

<div class="exampleblock">

<div class="title">

Example 5. Overriding the default `ClientCache` *Auto-Configuration* by
configuring & bootstrapping a `CacheServer` application

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@CacheServerApplication
class SpringBootApacheGeodeCacheServerApplication {
    // ...
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You can also explicitly declare the `@ClientCacheApplication` annotation
on your `@SpringBootApplication` class:

</div>

<div class="exampleblock">

<div class="title">

Example 6. Overriding by explicitly declaring `@ClientCacheApplication`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@ClientCacheApplication
class SpringBootApacheGeodeClientCacheApplication {
    // ...
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You are overriding SBDG’s auto-configuration of the `ClientCache`
instance. As a result, you have now also implicitly consented to being
responsible for other aspects of the configuration (such as security).

</div>

<div class="paragraph">

Why does that happen?

</div>

<div class="paragraph">

It happens because, in certain cases, such as security, certain aspects
of security configuration (such as SSL) must be configured before the
cache instance is created. Also, Spring Boot always applies user
configuration before auto-configuration partially to determine what
needs to be auto-configured in the first place.

</div>

<div class="admonitionblock caution">

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Caution
</div></td>
<td class="content">Make sure you understand what you are doing when you
override auto-configuration.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect2">

### Replacing Auto-configuration

<div class="paragraph">

See the Spring Boot reference documentation on
{spring-boot-docs-html}/#using-boot-replacing-auto-configuration\[replacing
auto-configuration\].

</div>

</div>

<div class="sect2">

### Understanding Auto-configuration

<div class="paragraph">

This section covers the SBDG provided auto-configuration classes that
correspond to the SDG annotations in more detail.

</div>

<div class="paragraph">

To review the complete list of SBDG auto-confiugration classes, see
[\[geode-auto-configuration-disable-classes\]](#geode-auto-configuration-disable-classes).

</div>

<div class="sect3">

#### `@ClientCacheApplication`

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
<td class="content">The SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/ClientCacheAutoConfiguration.html[<code>ClientCacheAutoConfiguration</code>]
class corresponds to the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html[<code>@ClientCacheApplication</code>]
annotation.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

As explained in [\[getting-started\]](#getting-started) SBDG starts with
the opinion that application developers primarily build
{pivotal-gemfire-name} [client
applications](#geode-clientcache-applications) by using Spring Boot.

</div>

<div class="paragraph">

Technically, this means building Spring Boot applications with an
{pivotal-gemfire-name} `ClientCache` instance connected to a dedicated
cluster of {pivotal-gemfire-name} servers that manage the data as part
of a
{apache-geode-docs}/topologies_and_comm/cs_configuration/chapter_overview.html\[client/server\]
topology.

</div>

<div class="paragraph">

By way of example, this means that you need not explicitly declare and
annotate your `@SpringBootApplication` class with SDG’s
`@ClientCacheApplication` annotation, as the following example shows:

</div>

<div class="exampleblock">

<div class="title">

Example 7. Do Not Do This

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@ClientCacheApplication
class SpringBootApacheGeodeClientCacheApplication {
    // ...
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

SBDG’s provided auto-configuration class is already meta-annotated with
SDG’s `@ClientCacheApplication` annotation. Therefore, you need only do:

</div>

<div class="exampleblock">

<div class="title">

Example 8. Do This

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
class SpringBootApacheGeodeClientCacheApplication {
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
<td class="content">See SDG’s reference documentation for more details
on {pivotal-gemfire-name}
{spring-data-geode-docs-html}/#bootstrap-annotation-config-geode-applications[cache
applications] and
{spring-data-geode-docs-html}/#bootstrap-annotation-config-client-server-applications[client/server
applications] in particular.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### `@EnableGemfireCaching`

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
<td class="content">The SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/CachingProviderAutoConfiguration.html[<code>CachingProviderAutoConfiguration</code>]
class corresponds to the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/cache/config/EnableGemfireCaching.html[<code>@EnableGemfireCaching</code>]
annotation.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

If you used the core Spring Framework to configure
{pivotal-gemfire-name} as a caching provider in
{spring-framework-docs}/integration.html#cache\[Spring’s Cache
Abstraction\], you need to:

</div>

<div class="exampleblock">

<div class="title">

Example 9. Configuring caching using the Spring Framework

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@EnableCaching
class CachingUsingApacheGeodeConfiguration {

    @Bean
    GemfireCacheManager cacheManager(GemFireCache cache) {

        GemfireCacheManager cacheManager = new GemfireCacheManager();

        cacheManager.setCache(cache);

        return cacheManager;
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

If you use Spring Data for {pivotal-gemfire-name}'s
`@EnableGemfireCaching` annotation, you can simplify the preceding
configuration:

</div>

<div class="exampleblock">

<div class="title">

Example 10. Configuring caching using Spring Data for
{pivotal-gemfire-name}

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@EnableGemfireCaching
class CachingUsingApacheGeodeConfiguration {

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Also, if you use SBDG, you need only do:

</div>

<div class="exampleblock">

<div class="title">

Example 11. Configuring caching using Spring Boot for
{pivotal-gemfire-name}

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
class CachingUsingApacheGeodeConfiguration {

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

This lets you focus on the areas in your application that would benefit
from caching without having to enable the plumbing. You can then
demarcate the service methods in your application that are good
candidates for caching:

</div>

<div class="exampleblock">

<div class="title">

Example 12. Using caching in your application

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Service
class CustomerService {

    @Caching("CustomersByName")
    Customer findBy(String name) {
        // ...
    }
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
<td class="content">See <a href="#geode-caching-provider">documentation
on caching</a> for more details.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### `@EnableContinuousQueries`

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
<td class="content">The SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/ContinuousQueryAutoConfiguration.html[<code>ContinuousQueryAutoConfiguration</code>]
class corresponds to the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/EnableContinuousQueries.html[<code>@EnableContinuousQueries</code>]
annotation.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Without having to enable anything, you can annotate your application
(POJO) component method(s) with the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/listener/annotation/ContinuousQuery.html\[`@ContinuousQuery`\]
annotation to register a CQ and start receiving events. The method acts
as a `CqEvent` handler or, in {pivotal-gemfire-name}'s terminology, the
method is an implementation of the
{apache-geode-javadoc}/org/apache/geode/cache/query/CqListener.html\[`CqListener`\]
interface.

</div>

<div class="exampleblock">

<div class="title">

Example 13. Declare application CQs

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Component
class MyCustomerApplicationContinuousQueries {

    @ContinuousQuery("SELECT customer.* "
        + " FROM /Customers customers"
        + " WHERE customer.getSentiment().name().equalsIgnoreCase('UNHAPPY')")
    public void handleUnhappyCustomers(CqEvent event) {
        // ...
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

As the preceding example shows, you can define the events you are
interested in receiving by using an OQL query with a finely tuned query
predicate that describes the events of interests and implements the
handler method to process the events (such as applying a credit to the
customer’s account and following up in email).

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
<td class="content">See <a
href="#geode-continuous-query">[geode-continuous-query]</a> for more
details.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### `@EnableGemfireFunctionExecutions` & `@EnableGemfireFunctions`

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
<td class="content">The SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/FunctionExecutionAutoConfiguration.html[<code>FunctionExecutionAutoConfiguration</code>]
class corresponds to both the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/function/config/EnableGemfireFunctionExecutions.html[<code>@EnableGemfireFunctionExecutions</code>]
and SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/function/config/EnableGemfireFunctions.html[<code>@EnableGemfireFunctions</code>]
annotations.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Whether you need to
{spring-data-geode-docs-html}/#function-execution\[execute\] or
{spring-data-geode-docs-html}/#function-implementation\[implement\] a
`Function`, SBDG detects the Function definition and auto-configures it
appropriately for use in your Spring Boot application. You need only
define the Function execution or implementation in a package below the
main `@SpringBootApplication` class:

</div>

<div class="exampleblock">

<div class="title">

Example 14. Declare a Function Execution

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
package example.app.functions;

@OnRegion("Accounts")
interface MyCustomerApplicationFunctions {

    void applyCredit(Customer customer);

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Then you can inject the Function execution into any application
component and use it:

</div>

<div class="exampleblock">

<div class="title">

Example 15. Use the Function

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
package example.app.service;

@Service
class CustomerService {

    @Autowired
    private MyCustomerApplicationFunctions customerFunctions;

    void analyzeCustomerSentiment(Customer customer) {

        // ...

        this.customerFunctions.applyCredit(customer);

        // ...
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The same pattern basically applies to Function implementations, except
in the implementation case, SBDG registers the Function implementation
for use (that is, to be called by a Function execution).

</div>

<div class="paragraph">

Doing so lets you focus on defining the logic required by your
application and not worry about how Functions are registered, called,
and so on. SBDG handles this concern for you.

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
<td class="content">Function implementations are typically defined and
registered on the server-side.</td>
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
<td class="content">See <a href="#geode-functions">[geode-functions]</a>
for more details.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### `@EnableGemfireRepositories`

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
<td class="content">The SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/GemFireRepositoriesAutoConfigurationRegistrar.html[<code>GemFireRepositoriesAutoConfigurationRegistrar</code>]
class corresponds to the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/repository/config/EnableGemfireRepositories.html[<code>@EnableGemfireRepositories</code>]
annotation.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

As with Functions, you need concern yourself only with the data access
operations (such as basic CRUD and simple queries) required by your
application to carry out its operation, not with how to create and
perform them (for example, `Region.get(key)` and `Region.put(key, obj)`)
or execute them (for example, `Query.execute(arguments)`).

</div>

<div class="paragraph">

Start by defining your Spring Data Repository:

</div>

<div class="exampleblock">

<div class="title">

Example 16. Define an application-specific Repository

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
package example.app.repo;

interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findBySentimentEqualTo(Sentiment sentiment);

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Then you can inject the Repository into an application component and use
it:

</div>

<div class="exampleblock">

<div class="title">

Example 17. Using the application-specific Repository

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
package example.app.sevice;

@Service
class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public void processCustomersWithSentiment(Sentiment sentiment) {

        this.repository.findBySentimentEqualTo(sentiment)
            .forEach(customer -> { /* ... */ });

        // ...
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Your application-specific Repository simply needs to be declared in a
package below the main `@SpringBootApplication` class. Again, you are
focusing only on the data access operations and queries required to
carry out the operatinons of your application, nothing more.

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
<td class="content">See <a
href="#geode-repositories">[geode-repositories]</a> for more
details.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### `@EnableLogging`

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
<td class="content">The SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/LoggingAutoConfiguration.html[<code>LoggingAutoConfiguration</code>]
class corresponds to the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/EnableLogging.html[<code>@EnableLogging</code>]
annotation.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Logging is an essential application concern to understand what is
happening in the system along with when and where the events occurred.
By default, SBDG auto-configures logging for {pivotal-gemfire-name} with
the default log-level, “config”.

</div>

<div class="paragraph">

You can change any aspect of logging, such as the log-level, in Spring
Boot `application.properties`:

</div>

<div class="exampleblock">

<div class="title">

Example 18. Change the log-level for {pivotal-gemfire-name}

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Spring Boot application.properites.

spring.data.gemfire.cache.log-level=debug
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
<td class="content">The 'spring.data.gemfire.logging.level' property is
an alias for <code>spring.data.gemfire.cache.log-level</code>.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

You can also configure other aspects, such as the log file size and disk
space limits for the filesystem location used to store the
{pivotal-gemfire-name} log files at runtime.

</div>

<div class="paragraph">

Under the hood, {pivotal-gemfire-name}'s logging is based on Log4j.
Therefore, you can configure {pivotal-gemfire-name} logging to use any
logging provider (such as Logback) and configuration metadata
appropriate for that logging provider so long as you supply the
necessary adapter between Log4j and whatever logging system you use. For
instance, if you include
`org.springframework.boot:spring-boot-starter-logging`, you are using
Logback and you will need the `org.apache.logging.log4j:log4j-to-slf4j`
adapter.

</div>

</div>

<div class="sect3">

#### `@EnablePdx`

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
<td class="content">The SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/PdxSerializationAutoConfiguration.html[<code>PdxSerializationAutoConfiguration</code>]
class corresponds to the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/EnablePdx.html[<code>@EnablePdx</code>]
annotation.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Any time you need to send an object over the network or overflow or
persist an object to disk, your application domain model object must be
serializable. It would be painful to have to implement
`java.io.Serializable` in every one of your application domain model
objects (such as `Customer`) that would potentially need to be
serialized.

</div>

<div class="paragraph">

Furthermore, using Java Serialization may not be ideal (it may not be
the most portable or efficient solution) in all cases or even possible
in other cases (such as when you use a third party library over which
you have no control).

</div>

<div class="paragraph">

In these situations, you need to be able to send your object anywhere,
anytime without unduly requiring the class type to be serializable and
exist on the classpath in every place it is sent. Indeed, the final
destination may not even be a Java application. This is where
{pivotal-gemfire-name}
{apache-geode-docs}/developing/data_serialization/gemfire_pdx_serialization.html\[PDX
Serialization\] steps in to help.

</div>

<div class="paragraph">

However, you need not figure out how to configure PDX to identify the
application class types that needs to be serialized. Instead, you can
define your class type as follows:

</div>

<div class="exampleblock">

<div class="title">

Example 19. Customer class

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Region("Customers")
class Customer {

    @Id
    private Long id;

    @Indexed
    private String name;

    // ...
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

SBDG’s auto-configuration handles the rest.

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
<td class="content">See <a
href="#geode-data-serialization">[geode-data-serialization]</a> for more
details.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### `@EnableSecurity`

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
<td class="content">The SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/ClientSecurityAutoConfiguration.html[<code>ClientSecurityAutoConfiguration</code>]
class and
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/PeerSecurityAutoConfiguration.html[<code>PeerSecurityAutoConfiguration</code>]
class correspond to the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/EnableSecurity.html[<code>@EnableSecurity</code>]
annotation, but they apply security (specifically, authentication and
authorization (auth) configuration) for both clients and servers.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Configuring your Spring Boot, {pivotal-gemfire-name} `ClientCache`
application to properly authenticate with a cluster of secure
{pivotal-gemfire-name} servers is as simple as setting a username and a
password in Spring Boot `application.properties`:

</div>

<div class="exampleblock">

<div class="title">

Example 20. Supplying Authentication Credentials

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Spring Boot application.properties

spring.data.gemfire.security.username=Batman
spring.data.gemfire.security.password=r0b!n5ucks
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
<td class="content">Authentication is even easier to configure in a
managed environment, such as PCF when using PCC. You need not do
anything.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Authorization is configured on the server-side and is made simple with
SBDG and the help of [Apache Shiro](https://shiro.apache.org/). Of
course, this assumes you use SBDG to configure and bootstrap your
{pivotal-gemfire-name} cluster in the first place, which is even easier
with SBDG. See
[\[geode-cluster-configuration-bootstrapping\]](#geode-cluster-configuration-bootstrapping).

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
<td class="content">See <a href="#geode-security">[geode-security]</a>
for more details.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### `@EnableSsl`

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
<td class="content">The SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/SslAutoConfiguration.html[<code>SslAutoConfiguration</code>]
class corresponds to the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/EnableSsl.html[<code>@EnableSsl</code>]
annotation.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Configuring SSL for secure transport (TLS) between your Spring Boot,
{pivotal-gemfire-name} `ClientCache` application and an
{pivotal-gemfire-name} cluster can be a real problem, especially to get
right from the start. So, it is something that SBDG makes as simple as
possible.

</div>

<div class="paragraph">

You can supply a `trusted.keystore` file containing the certificates in
a well-known location (such as the root of your application classpath),
and SBDG’s auto-configuration steps in to handle the rest.

</div>

<div class="paragraph">

This is useful during development, but we highly recommend using a more
secure procedure (such as integrating with a secure credential store
like LDAP, CredHub or Vault) when deploying your Spring Boot application
to production.

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
<td class="content">See <a
href="#geode-security-ssl">[geode-security-ssl]</a> for more
details.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### `@EnableGemFireHttpSession`

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
<td class="content">The SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/SpringSessionAutoConfiguration.html[<code>SpringSessionAutoConfiguration</code>]
class corresponds to the SSDG
{spring-session-data-gemfire-javadoc}/org/springframework/session/data/gemfire/config/annotation/EnableGemFireHttpSession.html[<code>@EnableGemFireHttpSession</code>]
annotation.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Configuring {pivotal-gemfire-name} to serve as the (HTTP) session state
caching provider by using Spring Session requires that you only include
the correct starter, that is `spring-geode-starter-session`:

</div>

<div class="exampleblock">

<div class="title">

Example 21. Using Spring Session

</div>

<div class="content">

<div class="listingblock">

<div class="content">

    <dependency>
        <groupId>org.springframework.geode</groupId>
        <artifactId>spring-geode-starter-session</artifactId>
        <version>{revnumber}</version>
    </dependency>

</div>

</div>

</div>

</div>

<div class="paragraph">

With Spring Session — and specifically Spring Session for
{pivotal-gemfire-name} (SSDG) — on the classpath of your Spring Boot,
{pivotal-gemfire-name} `ClientCache` Web application, you can manage
your (HTTP) session state with {pivotal-gemfire-name}. No further
configuration is needed. SBDG auto-configuration detects Spring Session
on the application classpath and does the rest.

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
<td class="content">See <a href="#geode-session">[geode-session]</a> for
more details.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### RegionTemplateAutoConfiguration

<div class="paragraph">

The SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/RegionTemplateAutoConfiguration.html\[`RegionTemplateAutoConfiguration`\]
class has no corresponding SDG annotation. However, the
auto-configuration of a `GemfireTemplate` for every
{pivotal-gemfire-name} `Region` defined and declared in your Spring Boot
application is still supplied by SBDG.

</div>

<div class="paragraph">

For example, you can define a Region by using:

</div>

<div class="exampleblock">

<div class="title">

Example 22. Region definition using JavaConfig

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Configuration
class GeodeConfiguration {

    @Bean("Customers")
    ClientRegionFactoryBean<Long, Customer> customersRegion(GemFireCache cache) {

        ClientRegionFactoryBean<Long, Customer> customersRegion =
            new ClientRegionFactoryBean<>();

        customersRegion.setCache(cache);
        customersRegion.setShortcut(ClientRegionShortcut.PROXY);

        return customersRegion;
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Alternatively, you can define the `Customers` Region by using
`@EnableEntityDefinedRegions`:

</div>

<div class="exampleblock">

<div class="title">

Example 23. Region definition using `@EnableEntityDefinedRegions`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Configuration
@EnableEntityDefinedRegion(basePackageClasses = Customer.class)
class GeodeConfiguration {

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Then SBDG supplies a `GemfireTemplate` instance that you can use to
perform low-level data-access operations (indirectly) on the `Customers`
Region:

</div>

<div class="exampleblock">

<div class="title">

Example 24. Use the `GemfireTemplate` to access the "Customers" Region

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Repository
class CustomersDao {

    @Autowired
    @Qualifier("customersTemplate")
    private GemfireTemplate customersTemplate;

    Customer findById(Long id) {
        return this.customerTemplate.get(id);
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You need not explicitly configure `GemfireTemplates` for each Region to
which you need low-level data access (such as when you are not using the
Spring Data Repository abstraction).

</div>

<div class="paragraph">

Be careful to qualify the `GemfireTemplate` for the Region to which you
need data access, especially given that you probably have more than one
Region defined in your Spring Boot application.

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
<td class="content">See <a
href="#geode-data-access-region-templates">[geode-data-access-region-templates]</a>
for more details.</td>
</tr>
</tbody>
</table>

</div>

</div>

</div>

</div>

</div>

</div>

<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:11:44 -0700

</div>

</div>
