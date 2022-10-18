---
title: Appendix
---


<!-- 
 Copyright (c) VMware, Inc. 2022. All rights reserved.
 Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 agreements. See the NOTICE file distributed with this work for additional information regarding
 copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance with the License. You may obtain a
 copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software distributed under the License
 is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 or implied. See the License for the specific language governing permissions and limitations under
 the License.
-->

The following appendices provide additional help while developing Spring
Boot applications backed by VMware GemFire:

1.  [Auto-configuration vs. Annotation-based
    configuration](#geode-auto-configuration-annotations)

2.  [Configuration Metadata Reference](#geode-configuration-metadata)

3.  [Disabling Auto-configuration](#geode-auto-configuration-disable)

4.  [Switching from VMware GemFire to VMware GemFire or
    VMware GemFire for TAS](#geode-gemfire-switch)

5.  [Running a VMware GemFire cluster with Spring Boot from your
    IDE](#geode-cluster-configuration-bootstrapping)

6.  [Testing](#geode-testing-support)

7.  [Examples](#geode-examples)

8.  [References](#references)

### Auto-configuration vs. Annotation-based configuration

The question most often asked is, “What Spring Data for
VMware GemFire (SDG) annotations can I use, or must I use, when
developing VMware GemFire applications with Spring Boot?”

This section answers this question and more.

See the complementary sample, [Spring Boot Auto-configuration for
VMware GemFire](guides/boot-configuration.html), which shows the
auto-configuration provided by Spring Boot for VMware GemFire in
action.

#### Background

To help answer this question, you must start by reviewing the complete
collection of available Spring Data for VMware GemFire (SDG)
annotations. These annotations are provided in the
[`org.springframework.data.gemfire.config.annotation`](https://docs.spring.io/spring/docs/current/javadoc-api/)
package. Most of the essential annotations begin with `@Enable...`, except
for the base annotations: `@ClientCacheApplication`,
`@PeerCacheApplication` and `@CacheServerApplication`.

By extension, Spring Boot for VMware GemFire (SBDG) builds on SDG’s
annotation-based configuration model to implement auto-configuration and
apply Spring Boot’s core concepts, such as “convention over
configuration”, letting VMware GemFire applications be built with
Spring Boot reliably, quickly, and easily.

SDG provides this annotation-based configuration model to, first and
foremost, give application developers “choice” when building Spring
applications with VMware GemFire. SDG makes no assumptions about
what application developers are trying to create and fails fast anytime
the configuration is ambiguous, giving users immediate feedback.

Second, SDG’s annotations were meant to get application developers up
and running quickly and reliably with ease. SDG accomplishes this by
applying sensible defaults so that application developers need not know,
or even have to learn, all the intricate configuration details and
tooling provided by VMware GemFire to accomplish simple tasks, such
as building a prototype.

So, SDG is all about “choice” and SBDG is all about “convention”.
Together these frameworks provide application developers with
convenience and ease to move quickly and reliably.

To learn more about the motivation behind SDG’s annotation-based
configuration model, see the
[Reference Documentation](https://docs.spring.io/spring-data/gemfire/docs/current/reference/html/#bootstrap-annotation-config-introduction/#bootstrap-annotation-config-introduction).

#### Conventions

Currently, SBDG provides auto-configuration for the following features:

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

This means the following SDG annotations are not required to use the
features above:

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

Since SBDG auto-configures these features for you, the above annotations
are not strictly required. Typically, you would only declare one of
these annotations when you want to “override” Spring Boot’s conventions,
as expressed in auto-configuration, and “customize” the behavior of the
feature.

#### Overriding

In this section, we cover a few examples to make the behavior when
overriding more apparent.

##### Caches

By default, SBDG provides you with a `ClientCache` instance. SBDG
accomplishes this by annotating an auto-configuration class with
`@ClientCacheApplication` internally.

By convention, we assume most application developers' are developing
Spring Boot applications by using VMware GemFire as “client”
applications in VMware GemFire's client/server topology. This is
especially true as users migrate their applications to a managed cloud
environment.

Still, you can “override” the default settings (convention) and declare
your Spring applications to be actual peer `Cache` members (nodes) of a
VMware GemFire cluster, instead:

Example 1. Spring Boot, VMware GemFire Peer `Cache` Application


``` highlight
@SpringBootApplication
@CacheServerApplication
class SpringBootApacheGeodePeerCacheServerApplication {  }
```

By declaring the `@CacheServerApplication` annotation, you effectively
override the SBDG default. Therefore, SBDG does not provide you with a
`ClientCache` instance by default, because you have informed SBDG of
exactly what you want: a peer `Cache` instance hosting an embedded
`CacheServer` that allows client connections.

However, you then might ask, “Well, how do I customize the `ClientCache`
instance when developing client applications without explicitly
declaring the `@ClientCacheApplication` annotation?”

First, you can “customize” the `ClientCache` instance by explicitly
declaring the `@ClientCacheApplication` annotation in your Spring Boot
application configuration and setting specific attributes as needed.
However, you should be aware that, by explicitly declaring this
annotation, (or, by default, any of the other auto-configured
annotations), you assume all the responsibility that comes with it,
since you have effectively overridden the auto-configuration. One
example of this is security, which we touch on more later.

The most ideal way to “customize” the configuration of any feature is by
way of the well-known and documented
[properties](#geode-configuration-metadata), specified in Spring Boot
`application.properties` (the “convention”), or by using a
[`Configurer`](https://docs.spring.io/spring-data/gemfire/docs/current/reference/html/#bootstrap-annotation-config-introduction/#bootstrap-annotation-config-configurers).

See the [Reference Guide](#geode-clientcache-applications) for more
detail.

##### Security

As with the `@ClientCacheApplication` annotation, the `@EnableSecurity`
annotation is not strictly required, unless you want to override and
customize the defaults.

Outside a managed environment, the only security configuration required
is specifying a username and password. You do this by using the
well-known and documented SDG username and password properties in Spring
Boot `application.properties`:

Example 2. Required Security Properties in a Non-Manage Envionment


``` highlight
spring.data.gemfire.security.username=MyUser
spring.data.gemfire.security.password=Secret
```

You need not explicitly declare the `@EnableSecurity` annotation just to
specify security configuration (such as username and password).

Inside a managed environment, such as the Tanzu Application Service
when using VMware GemFire, SBDG is able to introspect the
environment and configure security (auth) completely without the need to
specify any configuration, usernames and passwords, or otherwise. This
is due, in part, because TAS supplies the security details in the VCAP
environment when the application is deployed to TAS and bound to
services (such as VMware GemFire).

So, in short, you need not explicitly declare the `@EnableSecurity`
annotation (or `@ClientCacheApplication`).

However, if you do explicitly declare the `@ClientCacheApplication` or
`@EnableSecurity` annotations, you are now responsible for this
configuration, and SBDG’s auto-configuration no longer applies.

While explicitly declaring `@EnableSecurity` makes more sense when
“overriding” the SBDG security auto-configuration, explicitly declaring
the `@ClientCacheApplication` annotation most likely makes less sense
with regard to its impact on security configuration.

This is entirely due to the internals of VMware GemFire, because,
in certain cases (such as security), not even Spring is able to
completely shield you from the nuances of VMware GemFire's
configuration. No framework can.

You must configure both auth and SSL before the cache instance (whether
a `ClientCache` or a peer `Cache`) is created. This is because security
is enabled and configured during the “construction” of the cache. Also,,
the cache pulls the configuration from JVM System properties that must
be set before the cache is constructed.

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

Again, this is due to the way security (auth) and SSL metadata must be
supplied to VMware GemFire on startup.

See the [Reference Guide](#geode-security) for more details.


#### Extension

Most of the time, many of the other auto-configured annotations for CQ,
Functions, PDX, Repositories, and so on need not ever be declared
explicitly.

Many of these features are enabled automatically by having SBDG or other
libraries (such as Spring Session) on the application classpath or are
enabled based on other annotations applied to beans in the Spring
`ApplicationContext`.

We review a few examples in the following sections.

##### Caching

It is rarely, if ever, necessary to explicitly declare either the Spring
Framework’s `@EnableCaching` or the SDG-specific `@EnableGemfireCaching`
annotation in Spring configuration when you use SBDG. SBDG automatically
enables caching and configures the SDG `GemfireCacheManager` for you.

You need only focus on which application service components are
appropriate for caching:

Example 3. Service Caching


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

You need to create VMware GemFire Regions that back the caches
declared in your application service components (`CustomersByName` in
the preceding example) by using Spring’s caching annotations (such as
`@Cacheable`), or alternatively, JSR-107 JCache annotations (such as
`@CacheResult`).

You can do that by defining each Region explicitly or, more
conveniently, you can use the following approach:

Example 4. Configuring Caches (Regions)


``` highlight
@SpringBootApplication
@EnableCachingDefinedRegions
class Application {  }
```

`@EnableCachingDefinedRegions` is optional, provided for convenience,
and complementary to caching when used rather than being necessary.

See the [Reference Guide](#geode-caching-provider) for more detail.

##### Continuous Query

It is rarely, if ever, necessary to explicitly declare the SDG
`@EnableContinuousQueries` annotation. Instead, you should focus on
defining your application queries and worry less about the plumbing.

Consider the following example:

Example 5. Defining Queries for CQ


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

VMware GemFire CQ applies only to clients.

See the [Reference Guide](#geode-continuous-query) for more detail.

##### Functions

You rarely, if ever, need to explicitly declare either the
`@EnableGemfireFunctionExecutions` or `@EnableGemfireFunctions`
annotations. SBDG provides auto-configuration for both Function
implementations and executions.

You need to define the implementation:

Example 6. Function Implementation


``` highlight
@Component
class GeodeFunctions {

    @GemfireFunction
    Object exampleFunction(Object arg) {
        // ...
    }
}
```

Then you need to define the execution:

Example 7. Function Execution


``` highlight
@OnRegion(region = "Example")
interface GeodeFunctionExecutions {

    Object exampleFunction(Object arg);

}
```

SBDG automatically finds, configures, and registers Function
implementations (POJOs) in VMware GemFire as proper `Functions` and
creates execution proxies for the interfaces, which can then be injected
into application service components to invoke the registered `Functions`
without needing to explicitly declare the enabling annotations. The
application Function implementations (POJOs) and executions (interfaces)
should exist below the `@SpringBootApplication` annotated main class.

See the [Reference Guide](#geode-functions) for more detail.

##### PDX

You rarely, if ever, need to explicitly declare the `@EnablePdx`
annotation, since SBDG auto-configures PDX by default. SBDG also
automatically configures the SDG `MappingPdxSerializer` as the default
`PdxSerializer`.

It is easy to customize the PDX configuration by setting the appropriate
[properties](#geode-configuration-metadata) (search for “PDX”) in Spring
Boot `application.properties`.

See the [Reference Guide](#geode-data-serialization) for more detail.

##### Spring Data Repositories

You rarely, if ever, need to explicitly declare the
`@EnableGemfireRepositories` annotation, since SBDG auto-configures
Spring Data (SD) Repositories by default.

You need only define your Repositories:

Example 8. Customer’s Repository


``` highlight
interface CustomerRepository extends CrudRepository<Customer, Long> {

    Customer findByName(String name);

}
```

SBDG finds the Repository interfaces defined in your application,
proxies them, and registers them as beans in the Spring
`ApplicationContext`. The Repositories can be injected into other
application service components.

It is sometimes convenient to use the `@EnableEntityDefinedRegions`
along with Spring Data Repositories to identify the entities used by
your application and define the Regions used by the Spring Data
Repository infrastructure to persist the entity’s state. The
`@EnableEntityDefinedRegions` annotation is optional, provided for
convenience, and complementary to the `@EnableGemfireRepositories`
annotation.

See the [Reference Guide](#geode-repositories) for more detail.


#### Explicit Configuration

Most of the other annotations provided in SDG are focused on particular
application concerns or enable certain VMware GemFire features,
rather than being a necessity, including:

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

None of these annotations are necessary and none are auto-configured by
SBDG. They are at your disposal when and if you need them. This also
means that none of these annotations are in conflict with any SBDG
auto-configuration.

#### Summary

In conclusion, you need to understand where SDG ends and SBDG begins. It
all begins with the auto-configuration provided by SBDG.

If a feature or function is not covered by SBDG’s auto-configuration,
you are responsible for enabling and configuring the feature
appropriately, as needed by your application (for example,
`@EnableRedisServer`).

In other cases, you might also want to explicitly declare a
complimentary annotation (such as `@EnableEntityDefinedRegions`) for
convenience, since SBDG provides no convention or opinion.

In all remaining cases, it boils down to understanding how
VMware GemFire works under the hood. While we go to great lengths
to shield you from as many details as possible, it is not feasible or
practical to address all matters, such as cache creation and security.


### Configuration Metadata Reference

The following reference sections cover documented and well-known
properties recognized and processed by Spring Data for
VMware GemFire (SDG) and Spring Session for VMware GemFire
(SSDG).

These properties may be used in Spring Boot `application.properties` or
as JVM System properties, to configure different aspects of or enable
individual features of VMware GemFire in a Spring application. When
combined with the power of Spring Boot, they give you the ability to
quickly create an application that uses VMware GemFire.

#### Spring Data Based Properties

The following properties all have a `spring.data.gemfire.*` prefix. For
example, to set the cache `copy-on-read` property, use
`spring.data.gemfire.cache.copy-on-read` in Spring Boot
`application.properties`.

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 1. <code>spring.data.gemfire.*</code>
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>name</code></p></td>
<td class="tableblock halign-left valign-top"><p>Name of the
VMware GemFire.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>SpringBasedCacheClientApplication</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#name--[<code>ClientCacheApplication.name</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>locators</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
Locator endpoints formatted as:
<code>locator1[port1],...,locatorN[portN]</code>.</p></td>
<td class="tableblock halign-left valign-top"><p>[]</p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#locators--[<code>PeerCacheApplication.locators</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>use-bean-factory-locator</code></p></td>
<td class="tableblock halign-left valign-top"><p>Enable the SDG
<code>BeanFactoryLocator</code> when mixing Spring config with
VMware GemFire native config (such as <code>cache.xml</code>) and
you wish to configure VMware GemFire objects declared in
<code>cache.xml</code> with Spring.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#useBeanFactoryLocator--[<code>ClientCacheApplication.useBeanFactoryLocator</code>]</p></td>
</tr>
</tbody>
</table>

Table 1. `spring.data.gemfire.*` properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 2. <code>spring.data.gemfire.*</code>
<em>GemFireCache</em> properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.copy-on-read</code></p></td>
<td class="tableblock halign-left valign-top"><p>Configure whether a
copy of an object returned from <code>Region.get(key)</code> is
made.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#copyOnRead--[<code>ClientCacheApplication.copyOnRead</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.critical-heap-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of heap at
or above which the cache is considered in danger of becoming
inoperable.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#criticalHeapPercentage--[<code>ClientCacheApplication.criticalHeapPercentage</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.critical-off-heap-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of off-heap
at or above which the cache is considered in danger of becoming
inoperable.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#criticalOffHeapPercentage--[<code>ClientCacheApplication.criticalOffHeapPercentage</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.enable-auto-region-lookup</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to lookup
Regions configured in VMware GemFire native configuration and
declare them as Spring beans.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAutoRegionLookup.html#enabled--[<code>EnableAutoRegionLookup.enable</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.eviction-heap-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of heap at
or above which the eviction should begin on Regions configured for
HeapLRU eviction.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#evictionHeapPercentage--[<code>ClientCacheApplication.evictionHeapPercentage</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.eviction-off-heap-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of off-heap
at or above which the eviction should begin on Regions configured for
HeapLRU eviction.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#evictionOffHeapPercentage--[<code>ClientCacheApplication.evictionOffHeapPercentage</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.log-level</code></p></td>
<td class="tableblock halign-left valign-top"><p>Configure the log-level
of a VMware GemFire cache.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>config</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#logLevel--[<code>ClientCacheApplication.logLevel</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.name</code></p></td>
<td class="tableblock halign-left valign-top"><p>Alias for
<code>spring.data.gemfire.name</code>.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>SpringBasedCacheClientApplication</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#name--[<code>ClientCacheApplication.name</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.compression.bean-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>Name of a Spring bean
that implements
<code>org.apache.geode.compression.Compressor</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableCompression.html#compressorBeanName--[<code>EnableCompression.compressorBeanName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.compression.region-names</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
Region names for which compression is configured.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>[]</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableCompression.html#RegionNames--[EnableCompression.RegionNames]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p>cache.off-heap.memory-size</p></td>
<td class="tableblock halign-left valign-top"><p>Determines the size of
off-heap memory used by VMware GemFire in megabytes (m) or
gigabytes (g) — for example, <code>120g</code></p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableOffHeap.html#memorySize--[<code>EnableOffHeap.memorySize</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.off-heap.region-names</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
Region names for which off-heap is configured.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>[]</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableOffHeap.html#RegionNames--[<code>EnableOffHeap.RegionNames</code>]</p></td>
</tr>
</tbody>
</table>

Table 2. `spring.data.gemfire.*` *GemFireCache* properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 3. <code>spring.data.gemfire.*</code>
<em>ClientCache</em> properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.client.durable-client-id</code></p></td>
<td class="tableblock halign-left valign-top"><p>Used only for clients
in a client/server installation. If set, this indicates that the client
is durable and identifies the client. The ID is used by servers to
reestablish any messaging that was interrupted by client
downtime.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#durableClientId--[<code>ClientCacheApplication.durableClientId</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.client.durable-client-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>Used only for clients
in a client/server installation. Number of seconds this client can
remain disconnected from its server and have the server continue to
accumulate durable events for it.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>300</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#durableClientTimeout--[<code>ClientCacheApplication.durableClientTimeout</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.client.keep-alive</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether the server
should keep the durable client’s queues alive for the timeout
period.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#keepAlive--[<code>ClientCacheApplication.keepAlive</code>]</p></td>
</tr>
</tbody>
</table>

Table 3. `spring.data.gemfire.*` *ClientCache* properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 4. <code>spring.data.gemfire.*</code> peer <em>Cache</em>
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.enable-auto-reconnect</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether a member (a
Locator or Server) try to reconnect and reinitialize the cache after it
has been forced out of the cluster by a network partition event or has
otherwise been shunned by other members.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#enableAutoReconnect--[<code>PeerCacheApplication.enableAutoReconnect</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.lock-lease</code></p></td>
<td class="tableblock halign-left valign-top"><p>The length, in seconds,
of distributed lock leases obtained by this cache.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>120</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#lockLease--[<code>PeerCacheApplication.lockLease</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.lock-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The number of seconds a
cache operation waits to obtain a distributed lock lease.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>60</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#lockTimeout--[<code>PeerCacheApplication.lockTimeout</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.message-sync-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>The frequency (in
seconds) at which a message is sent by the primary cache-server to all
the secondary cache-server nodes to remove the events that have already
been dispatched from the queue.</p></td>
<td class="tableblock halign-left valign-top"><p><code>1</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#messageSyncInterval--[<code>PeerCacheApplication.messageSyncInterval</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.search-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The number of seconds a
cache get operation can spend searching for a value.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>300</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#searchTimeout--[<code>PeerCacheApplication.searchTimeout</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.use-cluster-configuration</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether this cache
member node pulls its configuration metadata from the cluster-based
cluster configuration service.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#useClusterConfiguration--[<code>PeerCacheApplication.useClusterConfiguration</code>]</p></td>
</tr>
</tbody>
</table>

Table 4. `spring.data.gemfire.*` peer *Cache* properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 5. <code>spring.data.gemfire.*</code>
<em>CacheServer</em> properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.auto-startup</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether the
<code>CacheServer</code> should be started automatically at
runtime.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>true</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#autoStartup--[<code>CacheServerApplication.autoStartup</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.bind-address</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname on which this cache server listens.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#bindAddress--[<code>CacheServerApplication.bindAddress</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.hostname-for-clients</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname that server locators tell to clients to indicate the IP address
on which the cache server listens.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#hostnameForClients--[<code>CacheServerApplication.hostNameForClients</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.load-poll-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>The frequency in
milliseconds at which to poll the load probe on this cache
server.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>5000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#loadPollInterval--[<code>CacheServerApplication.loadPollInterval</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.max-connections</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum client
connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>800</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#maxConnections--[<code>CacheServerApplication.maxConnections</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.max-message-count</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum number of
messages that can be in a client queue.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>230000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#maxMessageCount--[<code>CacheServerApplication.maxMessageCount</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.max-threads</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum number of
threads allowed in this cache server to service client
requests.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#maxThreads--[<code>CacheServerApplication.maxThreads</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.max-time-between-pings</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum amount of
time between client pings.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>60000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#maxTimeBetweenPings--[<code>CacheServerApplication.maxTimeBetweenPings</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.message-time-to-live</code></p></td>
<td class="tableblock halign-left valign-top"><p>The time (in seconds)
after which a message in the client queue expires.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>180</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#messageTimeToLive--[<code>CacheServerApplication.messageTimeToLive</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port on which this
cache server listens for clients.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>40404</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#port--[<code>CacheServerApplication.port</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.socket-buffer-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The buffer size of the
socket connection to this <code>CacheServer</code>.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>32768</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#socketBufferSize--[<code>CacheServerApplication.socketBufferSize</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.subscription-capacity</code></p></td>
<td class="tableblock halign-left valign-top"><p>The capacity of the
client queue.</p></td>
<td class="tableblock halign-left valign-top"><p><code>1</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#subscriptionCapacity--[<code>CacheServerApplication.subscriptionCapacity</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.subscription-disk-store-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of the disk
store for client subscription queue overflow.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#subscriptionDiskStoreName--[<code>CacheServerApplication.subscriptionDiskStoreName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.subscription-eviction-policy</code></p></td>
<td class="tableblock halign-left valign-top"><p>The eviction policy
that is executed when the capacity of the client subscription queue is
reached.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>none</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#subscriptionEvictionPolicy--[<code>CacheServerApplication.subscriptionEvictionPolicy</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.tcp-no-delay</code></p></td>
<td class="tableblock halign-left valign-top"><p>The outgoing socket
connection tcp-no-delay setting.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>true</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#tcpNoDelay--[<code>CacheServerApplication.tcpNoDelay</code>]</p></td>
</tr>
</tbody>
</table>

Table 5. `spring.data.gemfire.*` *CacheServer* properties

`CacheServer` properties can be further targeted at specific
`CacheServer` instances by using an optional bean name of the
`CacheServer` bean defined in the Spring `ApplicationContext`. Consider
the following example:

``` highlight
spring.data.gemfire.cache.server.[<cacheServerBeanName>].bind-address=...
```


<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 6. <code>spring.data.gemfire.*</code> Cluster
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cluster.Region.type</code></p></td>
<td class="tableblock halign-left valign-top"><p>Specifies the data
management policy used when creating Regions on the servers in the
cluster.</p></td>
<td
class="tableblock halign-left valign-top"><p>https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/RegionShortcut.html#PARTITION[<code>RegionShortcut.PARTITION</code>]</p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html#serverRegionShortcut--[<code>EnableClusterConfiguration.serverRegionShortcut</code>]</p></td>
</tr>
</tbody>
</table>

Table 6. `spring.data.gemfire.*` Cluster properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 7. <code>spring.data.gemfire.*</code> <em>DiskStore</em>
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.allow-force-compaction</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to allow
<code>DiskStore.forceCompaction()</code> to be called on Regions that
use a disk store.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#allowForceCompaction--[<code>EnableDiskStore.allowForceCompaction</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.auto-compact</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to cause the
disk files to be automatically compacted.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>true</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#autoCompact--[<code>EnableDiskStore.autoCompact</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.compaction-threshold</code></p></td>
<td class="tableblock halign-left valign-top"><p>The threshold at which
an oplog becomes compactible.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>50</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#compactionThreshold--[<code>EnableDiskStore.compactionThreshold</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.directory.location</code></p></td>
<td class="tableblock halign-left valign-top"><p>The system directory
where the <code>DiskStore</code> (oplog) files are stored.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>[]</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#diskDirectories--[<code>EnableDiskStore.diskDirectories.location</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.directory.size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The amount of disk
space allowed to store disk store (oplog) files.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>21474883647</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#diskDirectories--[<code>EnableDiskStore.diskDirectories.size</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.disk-usage-critical-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>The critical threshold
for disk usage as a percentage of the total disk volume.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>99.0</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#diskUsageCriticalPercentage--[<code>EnableDiskStore.diskUsageCriticalPercentage</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.disk-usage-warning-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>The warning threshold
for disk usage as a percentage of the total disk volume.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>90.0</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#diskUsageWarningPercentage--[<code>EnableDiskStore.diskUsageWarningPercentage</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.max-oplog-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum size (in
megabytes) a single oplog (operation log) can be.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>1024</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#maxOplogSize--[<code>EnableDiskStore.maxOplogSize</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.queue-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum number of
operations that can be asynchronously queued.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#queueSize--[<code>EnableDiskStore.queueSize</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.time-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>The number of
milliseconds that can elapse before data written asynchronously is
flushed to disk.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>1000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#timeInterval--[<code>EnableDiskStore.timeInterval</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.write-buffer-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>Configures the write
buffer size in bytes.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>32768</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#writeBufferSize--[<code>EnableDiskStore.writeBufferSize</code>]</p></td>
</tr>
</tbody>
</table>

Table 7. `spring.data.gemfire.*` *DiskStore* properties

`DiskStore` properties can be further targeted at specific `DiskStore`
instances by setting the
[`DiskStore.name`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/DiskStore.html#getName)
property.

For example, you can specify directory location of the files for a
specific, named `DiskStore` by using:

``` highlight
spring.data.gemfire.disk.store.Example.directory.location=/path/to/geode/disk-stores/Example/
```

The directory location and size of the `DiskStore` files can be further
divided into multiple locations and size using array syntax:

``` highlight
spring.data.gemfire.disk.store.Example.directory[0].location=/path/to/geode/disk-stores/Example/one
spring.data.gemfire.disk.store.Example.directory[0].size=4096000
spring.data.gemfire.disk.store.Example.directory[1].location=/path/to/geode/disk-stores/Example/two
spring.data.gemfire.disk.store.Example.directory[1].size=8192000
```

Both the name and array index are optional, and you can use any
combination of name and array index. Without a name, the properties
apply to all `DiskStore` instances. Without array indexes, all named
`DiskStore` files are stored in the specified location and limited to
the defined size.

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 8. <code>spring.data.gemfire.*</code> Entity
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>entities.base-packages</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
package names indicating the start points for the entity scan.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableEntityDefinedRegions.html#basePackages--[<code>EnableEntityDefinedRegions.basePackages</code>]</p></td>
</tr>
</tbody>
</table>

Table 8. `spring.data.gemfire.*` Entity properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 9. <code>spring.data.gemfire.*</code> Locator
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>locator.host</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname of the system NIC to which the embedded Locator is bound to
listen for connections.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLocator.html#host--[<code>EnableLocator.host</code>]</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>locator.port</p></td>
<td class="tableblock halign-left valign-top"><p>The network port to
which the embedded Locator will listen for connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>10334</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLocator.html#port--[<code>EnableLocator.port</code>]</p></td>
</tr>
</tbody>
</table>

Table 9. `spring.data.gemfire.*` Locator properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 10. <code>spring.data.gemfire.*</code> Logging
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>logging.level</code></p></td>
<td class="tableblock halign-left valign-top"><p>The log level of an
VMware GemFire cache. Alias for
'spring.data.gemfire.cache.log-level'.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>config</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLogging.html#logLevel--[<code>EnableLogging.logLevel</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>logging.log-disk-space-limit</code></p></td>
<td class="tableblock halign-left valign-top"><p>The amount of disk
space allowed to store log files.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLogging.html#logDiskSpaceLimit--[<code>EnableLogging.logDiskSpaceLimit</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>logging.log-file</code></p></td>
<td class="tableblock halign-left valign-top"><p>The pathname of the log
file used to log messages.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLogging.html#logFile--[<code>EnableLogging.logFile</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>logging.log-file-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum size of a
log file before the log file is rolled.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLogging.html#logFileSizeLimit--[<code>EnableLogging.logFileSize</code>]</p></td>
</tr>
</tbody>
</table>

Table 10. `spring.data.gemfire.*` Logging properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 11. <code>spring.data.gemfire.*</code> Management
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>management.use-http</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to use the HTTP
protocol to communicate with a VMware GemFire Manager.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html#useHttp--[<code>EnableClusterConfiguration.useHttp</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>management.http.host</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname of the VMware GemFire Manager that runs the HTTP
service.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html#host--[<code>EnableClusterConfiguration.host</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>management.http.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port used by the
VMware GemFire Manager’s HTTP service to listen for
connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>7070</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html#port--[<code>EnableClusterConfiguration.port</code>]</p></td>
</tr>
</tbody>
</table>

Table 11. `spring.data.gemfire.*` Management properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 12. <code>spring.data.gemfire.*</code> Manager
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>manager.access-file</code></p></td>
<td class="tableblock halign-left valign-top"><p>The access control list
(ACL) file used by the Manager to restrict access to the JMX MBeans by
the clients.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#accessFile--[<code>EnableManager.accessFile</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p>manager.bind-address</p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname of the system NIC used by the Manager to bind and listen for
JMX client connections.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#bindAddress--[<code>EnableManager.bindAddress</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>manager.hostname-for-clients</code></p></td>
<td class="tableblock halign-left valign-top"><p>The hostname given to
JMX clients to ask the Locator for the location of the Manager.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#hostnameForClients--[<code>EnableManager.hostNameForClients</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>manager.password-file</code></p></td>
<td class="tableblock halign-left valign-top"><p>By default, the JMX
Manager lets clients without credentials connect. If this property is
set to the name of a file, only clients that connect with credentials
that match an entry in this file are allowed.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#passwordFile--[<code>EnableManager.passwordFile</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>manager.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port used by the
Manager to listen for JMX client connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>1099</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#port--[<code>EnableManager.port</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>manager.start</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to start the
Manager service at runtime.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#start--[<code>EnableManager.start</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>manager.update-rate</code></p></td>
<td class="tableblock halign-left valign-top"><p>The rate, in
milliseconds, at which this member pushes updates to any JMX
Managers.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>2000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#updateRate--[<code>EnableManager.updateRate</code>]</p></td>
</tr>
</tbody>
</table>

Table 12. `spring.data.gemfire.*` Manager properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 13. <code>spring.data.gemfire.*</code> PDX
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pdx.disk-store-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of the
<code>DiskStore</code> used to store PDX type metadata to disk when PDX
is persistent.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePdx.html#diskStoreName--[<code>EnablePdx.diskStoreName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pdx.ignore-unread-fields</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether PDX ignores
fields that were unread during deserialization.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePdx.html#ignoreUnreadFields--[<code>EnablePdx.ignoreUnreadFields</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pdx.persistent</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether PDX persists
type metadata to disk.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePdx.html#persistent--[<code>EnablePdx.persistent</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pdx.read-serialized</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether a Region entry
is returned as a <code>PdxInstance</code> or deserialized back into
object form on read.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePdx.html#readSerialized--[<code>EnablePdx.readSerialized</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pdx.serialize-bean-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of a custom
Spring bean that implements
<code>org.apache.geode.pdx.PdxSerializer</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePdx.html#serializerBeanName--[<code>EnablePdx.serializerBeanName</code>]</p></td>
</tr>
</tbody>
</table>

Table 13. `spring.data.gemfire.*` PDX properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 14. <code>spring.data.gemfire.*</code> Pool
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.free-connection-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The timeout used to
acquire a free connection from a Pool.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>10000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#freeConnectionTimeout--[<code>EnablePool.freeConnectionTimeout</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.idle-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The amount of time a
connection can be idle before expiring (and closing) the
connection.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>5000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#idleTimeout--[<code>EnablePool.idleTimeout</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.load-conditioning-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>The interval for how
frequently the Pool checks to see if a connection to a given server
should be moved to a different server to improve the load
balance.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>300000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#loadConditioningInterval--[<code>EnablePool.loadConditioningInterval</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.locators</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
locator endpoints in the format of
<code>locator1[port1],...,locatorN[portN]</code></p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#locators--[<code>EnablePool.locators</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.max-connections</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum number of
client to server connections that a Pool will create.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#maxConnections--[EnablePool.maxConnections]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.min-connections</code></p></td>
<td class="tableblock halign-left valign-top"><p>The minimum number of
client to server connections that a Pool maintains.</p></td>
<td class="tableblock halign-left valign-top"><p><code>1</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#minConnections--[<code>EnablePool.minConnections</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.multi-user-authentication</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether the created
Pool can be used by multiple authenticated users.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#multiUserAuthentication--[<code>EnablePool.multiUserAuthentication</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.ping-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>How often to ping
servers to verify that they are still alive.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>10000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#pingInterval--[<code>EnablePool.pingInterval</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.pr-single-hop-enabled</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to perform
single-hop data access operations between the client and servers. When
<code>true</code>, the client is aware of the location of partitions on
servers that host Regions with
<code>DataPolicy.PARTITION</code>.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>true</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#prSingleHopEnabled--[<code>EnablePool.prSingleHopEnabled</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.read-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The number of
milliseconds to wait for a response from a server before timing out the
operation and trying another server (if any are available).</p></td>
<td
class="tableblock halign-left valign-top"><p><code>10000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#readTimeout--[<code>EnablePool.readTimeout</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.ready-for-events</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to signal the
server that the client is prepared and ready to receive events.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#readyForEvents--[<code>ClientCacheApplication.readyForEvents</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.retry-attempts</code></p></td>
<td class="tableblock halign-left valign-top"><p>The number of times to
retry a request after timeout/exception.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#retryAttempts--[<code>EnablePool.retryAttempts</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.server-group</code></p></td>
<td class="tableblock halign-left valign-top"><p>The group that all
servers to which a Pool connects must belong.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#serverGroup--[<code>EnablePool.serverGroup</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.servers</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
<code>CacheServer</code> endpoints in the format of
<code>server1[port1],...,serverN[portN]</code></p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#servers--[<code>EnablePool.servers</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.socket-buffer-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The socket buffer size
for each connection made in all Pools.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>32768</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#socketBufferSize--[<code>EnablePool.socketBufferSize</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.statistic-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>How often to send
client statistics to the server.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#statisticInterval--[<code>EnablePool.statisticInterval</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p>pool.subscription-ack-interval</p></td>
<td class="tableblock halign-left valign-top"><p>The interval in
milliseconds to wait before sending acknowledgements to the
<code>CacheServer</code> for events received from the server
subscriptions.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>100</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#subscriptionAckInterval--[<code>EnablePool.subscriptionAckInterval</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.subscription-enabled</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether the created
Pool has server-to-client subscriptions enabled.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#subscriptionEnabled--[<code>EnablePool.subscriptionEnabled</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.subscription-message-tracking-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The
<code>messageTrackingTimeout</code> attribute, which is the time-to-live
period, in milliseconds, for subscription events the client has received
from the server.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>900000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#subscriptionMessageTrackingTimeout--[<code>EnablePool.subscriptionMessageTrackingTimeout</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.subscription-redundancy</code></p></td>
<td class="tableblock halign-left valign-top"><p>The redundancy level
for all Pools server-to-client subscriptions.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#subscriptionRedundancy--[<code>EnablePool.subsriptionRedundancy</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.thread-local-connections</code></p></td>
<td class="tableblock halign-left valign-top"><p>The thread local
connections policy for all Pools.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#threadLocalConnections--[<code>EnablePool.threadLocalConnections</code>]</p></td>
</tr>
</tbody>
</table>

Table 14. `spring.data.gemfire.*` Pool properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 15. <code>spring.data.gemfire.*</code> Security
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.username</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of the user
used to authenticate with the servers.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#securityUsername--[<code>EnableSecurity.securityUsername</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.password</code></p></td>
<td class="tableblock halign-left valign-top"><p>The user password used
to authenticate with the servers.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#securityPassword--[<code>EnableSecurity.securityPassword</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.properties-file</code></p></td>
<td class="tableblock halign-left valign-top"><p>The system pathname to
a properties file that contains security credentials.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#securityPropertiesFile--[<code>EnableAuth.propertiesFile</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.client.accessor</code></p></td>
<td class="tableblock halign-left valign-top"><p>X</p></td>
<td class="tableblock halign-left valign-top"><p>X</p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#clientAccessor--[<code>EnableAuth.clientAccessor</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.client.accessor-post-processor</code></p></td>
<td class="tableblock halign-left valign-top"><p>The callback that
should be invoked in the post-operation phase, which is when the
operation has completed on the server but before the result is sent to
the client.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#clientAccessorPostProcessor--[<code>EnableAuth.clientAccessorPostProcessor</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.client.authentication-initializer</code></p></td>
<td class="tableblock halign-left valign-top"><p>Static creation method
that returns an <code>AuthInitialize</code> object, which obtains
credentials for peers in a cluster.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#clientAuthenticationInitializer--[<code>EnableSecurity.clientAuthentiationInitializer</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.client.authenticator</code></p></td>
<td class="tableblock halign-left valign-top"><p>Static creation method
that returns an <code>Authenticator</code> object used by a cluster
member (Locator or Server) to verify the credentials of a connecting
client.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#clientAuthenticator--[<code>EnableAuth.clientAuthenticator</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.client.diffie-hellman-algorithm</code></p></td>
<td class="tableblock halign-left valign-top"><p>Used for
authentication. For secure transmission of sensitive credentials (such
as passwords), you can encrypt the credentials by using the
Diffie-Hellman key-exchange algorithm. You can do so by setting the
<code>security-client-dhalgo</code> system property on the clients to
the name of a valid, symmetric key cipher supported by the JDK.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#clientDiffieHellmanAlgorithm--[<code>EnableAuth.clientDiffieHellmanAlgorithm</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.log.file</code></p></td>
<td class="tableblock halign-left valign-top"><p>The pathname to a log
file used for security log messages.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#securityLogFile--[<code>EnableAuth.securityLogFile</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.log.level</code></p></td>
<td class="tableblock halign-left valign-top"><p>The log level for
security log messages.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#securityLogLevel--[<code>EnableAuth.securityLogLevel</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.manager.class-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of a class
that implements
<code>org.apache.geode.security.SecurityManager</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#securityManagerClassName--[<code>EnableSecurity.securityManagerClassName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.peer.authentication-initializer</code></p></td>
<td class="tableblock halign-left valign-top"><p>Static creation method
that returns an <code>AuthInitialize</code> object, which obtains
credentials for peers in a cluster.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#peerAuthenticationInitializer--[<code>EnableSecurity.peerAuthenticationInitializer</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.peer.authenticator</code></p></td>
<td class="tableblock halign-left valign-top"><p>Static creation method
that returns an <code>Authenticator</code> object, which is used by a
peer to verify the credentials of a connecting node.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#peerAuthenticator--[<code>EnableAuth.peerAuthenticator</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p>security.peer.verify-member-timeout</p></td>
<td class="tableblock halign-left valign-top"><p>The timeout in
milliseconds used by a peer to verify membership of an unknown
authenticated peer requesting a secure connection.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#peerVerifyMemberTimeout--[<code>EnableAuth.peerVerifyMemberTimeout</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.post-processor.class-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of a class
that implements the <code>org.apache.geode.security.PostProcessor</code>
interface that can be used to change the returned results of Region get
operations.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#securityPostProcessorClassName--[<code>EnableSecurity.securityPostProcessorClassName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.shiro.ini-resource-path</code></p></td>
<td class="tableblock halign-left valign-top"><p>The VMware GemFire
System property that refers to the location of an Apache Shiro INI file
that configures the Apache Shiro Security Framework in order to secure
VMware GemFire.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#shiroIniResourcePath--[<code>EnableSecurity.shiroIniResourcePath</code>]</p></td>
</tr>
</tbody>
</table>

Table 15. `spring.data.gemfire.*` Security properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 16. <code>spring.data.gemfire.*</code> SSL
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.cluster</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by the cluster to secure communications.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.default-alias</code></p></td>
<td class="tableblock halign-left valign-top"><p>The default alias to
the stored SSL certificate used to secure communications across the
entire VMware GemFire system.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#defaultCertificateAlias--[<code>EnableSsl.defaultCertificateAlias</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.gateway</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by the WAN Gateway Senders/Receivers to secure
communications.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.jmx</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by the Manager’s JMX-based JVM MBeanServer and JMX
clients to secure communications.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.locator</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by the Locator to secure communications.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.server</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by clients and servers to secure
communications.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.web</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by the embedded HTTP server to secure
communications (HTTPS).</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.ciphers</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-separated list of
SSL ciphers or <code>any</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#ciphers--[<code>EnableSsl.ciphers</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.components</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
VMware GemFire components (for example, WAN) to be configured for
SSL communication.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#components--[<code>EnableSsl.components</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.keystore</code></p></td>
<td class="tableblock halign-left valign-top"><p>The system pathname to
the Java KeyStore file storing certificates for SSL.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#keystore--[<code>EnableSsl.keystore</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.keystore.password</code></p></td>
<td class="tableblock halign-left valign-top"><p>The password used to
access the Java KeyStore file.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#keystorePassword--[<code>EnableSsl.keystorePassword</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.keystore.type</code></p></td>
<td class="tableblock halign-left valign-top"><p>The password used to
access the Java KeyStore file (for example, JKS).</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#keystoreType--[<code>EnableSsl.keystoreType</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.protocols</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-separated list of
SSL protocols or <code>any</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#protocols--[<code>EnableSsl.protocols</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.require-authentication</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether two-way
authentication is required.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#requireAuthentication--[<code>EnableSsl.requireAuthentication</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.truststore</code></p></td>
<td class="tableblock halign-left valign-top"><p>The system pathname to
the trust store (Java KeyStore file) that stores certificates for
SSL.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#truststore--[<code>EnableSsl.truststore</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.truststore.password</code></p></td>
<td class="tableblock halign-left valign-top"><p>The password used to
access the trust store (Java KeyStore file).</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#truststorePassword--[<code>EnableSsl.truststorePassword</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.truststore.type</code></p></td>
<td class="tableblock halign-left valign-top"><p>The password used to
access the trust store (Java KeyStore file — for example, JKS).</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#truststoreType--[<code>EnableSsl.truststoreType</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.web-require-authentication</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether two-way HTTP
authentication is required.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#webRequireAuthentication--[<code>EnableSsl.webRequireAuthentication</code>]</p></td>
</tr>
</tbody>
</table>

Table 16. `spring.data.gemfire.*` SSL properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 17. <code>spring.data.gemfire.*</code> Service
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>service.http.bind-address</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname of the system NIC used by the embedded HTTP server to bind and
listen for HTTP(S) connections.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableHttpService.html#bindAddress--[<code>EnableHttpService.bindAddress</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>service.http.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port used by the
embedded HTTP server to listen for HTTP(S) connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>7070</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableHttpService.html#port--[<code>EnableHttpService.port</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>service.http.ssl-require-authentication</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether two-way HTTP
authentication is required.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableHttpService.html#sslRequireAuthentication--[<code>EnableHttpService.sslRequireAuthentication</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>service.http.dev-rest-api-start</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to start the
Developer REST API web service. A full installation of
VMware GemFire is required, and you must set the
<code>$GEODE</code> environment variable.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableHttpService.html#startDeveloperRestApi--[<code>EnableHttpService.startDeveloperRestApi</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>service.memcached.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port of the
embedded Memcached server (service).</p></td>
<td
class="tableblock halign-left valign-top"><p><code>11211</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableMemcachedServer.html#port--[<code>EnableMemcachedServer.port</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>service.memcached.protocol</code></p></td>
<td class="tableblock halign-left valign-top"><p>The protocol used by
the embedded Memcached server (service).</p></td>
<td
class="tableblock halign-left valign-top"><p><code>ASCII</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableMemcachedServer.html#protocol--[<code>EnableMemcachedServer.protocol</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>service.redis.bind-address</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname of the system NIC used by the embedded Redis server to bind and
listen for connections.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableRedisServer.html#bindAddress--[<code>EnableRedis.bindAddress</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>service.redis.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port used by the
embedded Redis server to listen for connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>6479</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableRedisServer.html#port--[<code>EnableRedisServer.port</code>]</p></td>
</tr>
</tbody>
</table>

Table 17. `spring.data.gemfire.*` Service properties

#### Spring Session Based Properties

The following properties all have a `spring.session.data.gemfire.*`
prefix. For example, to set the session Region name, set
`spring.session.data.gemfire.session.region.name` in Spring Boot
`application.properties`.

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 18. <code>spring.session.data.gemfire.*</code>
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.client.pool.name</code></p></td>
<td class="tableblock halign-left valign-top"><p>Name of the pool used
to send data access operations between the client and servers.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>gemfirePool</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#poolName--[<code>EnableGemFireHttpSession.poolName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.client.Region.shortcut</code></p></td>
<td class="tableblock halign-left valign-top"><p>The
<code>DataPolicy</code> used by the client Region to manage (HTTP)
session state.</p></td>
<td
class="tableblock halign-left valign-top"><p>https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/client/ClientRegionShortcut.html#PROXY[<code>ClientRegionShortcut.PROXY</code>]</p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#clientRegionShortcut--[<code>EnableGemFireHttpSession.clientRegionShortcut</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.Region.shortcut</code></p></td>
<td class="tableblock halign-left valign-top"><p>The
<code>DataPolicy</code> used by the server Region to manage (HTTP)
session state.</p></td>
<td
class="tableblock halign-left valign-top"><p>https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/RegionShortcut.html#PARTITION[<code>RegionShortcut.PARTITION</code>]</p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#serverRegionShortcut--[<code>EnableGemFireHttpSession.serverRegionShortcut</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>session.attributes.indexable</code></p></td>
<td class="tableblock halign-left valign-top"><p>The names of session
attributes for which an Index is created.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>[]</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#indexableSessionAttributes--[<code>EnableGemFireHttpSession.indexableSessionAttributes</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>session.expiration.max-inactive-interval-seconds</code></p></td>
<td class="tableblock halign-left valign-top"><p>Configures the number
of seconds in which a session can remain inactive before it
expires.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>1800</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#maxInactiveIntervalSeconds--[<code>EnableGemFireHttpSession.maxInactiveIntervalSeconds</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>session.Region.name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of the
(client/server) Region used to manage (HTTP) session state.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>ClusteredSpringSessions</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#RegionName--[<code>EnableGemFireHttpSession.RegionName</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>session.serializer.bean-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of a Spring
bean that implements
<code>org.springframework.session.data.gemfire.serialization.SessionSerializer</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#sessionSerializerBeanName--[<code>EnableGemFireHttpSession.sessionSerializerBeanName</code>]</p></td>
</tr>
</tbody>
</table>

Table 18. `spring.session.data.gemfire.*` properties

#### VMware GemFire Properties

While we do not recommend using VMware GemFire properties directly
in your Spring applications, SBDG does not prevent you from doing so.
See the
[complete reference to the VMware GemFire specific properties](https://geode.apache.org/docs/guide/115/reference/topics/gemfire_properties.html).

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Warning
</td>
<td class="content">VMware GemFire is very strict about the
properties that may be specified in a <code>gemfire.properties</code>
file. You cannot mix Spring properties with <code>gemfire.*</code>
properties in a VMware GemFire <code>gemfire.properties</code>
file.</td>
</tr>
</tbody>
</table>


### Disabling Auto-configuration

If you would like to disable the auto-configuration of any feature
provided by Spring Boot for VMware GemFire, you can specify the
auto-configuration class in the `exclude` attribute of the
`@SpringBootApplication` annotation:

Example 9. Disable Auto-configuration of PDX


``` highlight
@SpringBootApplication(exclude = PdxSerializationAutoConfiguration.class)
public class MySpringBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(MySpringBootApplication.class, args);
  }
}
```

You can disable more than one auto-configuration class at a time by
specifying each class in the `exclude` attribute using array syntax:

Example 10. Disable Auto-configuration of PDX & SSL


``` highlight
@SpringBootApplication(exclude = { PdxSerializationAutoConfiguration.class, SslAutoConfiguration.class })
public class MySpringBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(MySpringBootApplication.class, args);
  }
}
```

#### Complete Set of Auto-configuration Classes

The current set of auto-configuration classes in Spring Boot for
VMware GemFire includes:

- `CacheNameAutoConfiguration`

- `CachingProviderAutoConfiguration`

- `ClientCacheAutoConfiguration`

- `ClientSecurityAutoConfiguration`

- `ContinuousQueryAutoConfiguration`

- `FunctionExecutionAutoConfiguration`

- `GemFirePropertiesAutoConfiguration`

- `LoggingAutoConfiguration`

- `PdxSerializationAutoConfiguration`

- `PeerSecurityAutoConfiguration`

- `RegionTemplateAutoConfiguration`

- `RepositoriesAutoConfiguration`

- `SpringSessionAutoConfiguration`

- `SpringSessionPropertiesAutoConfiguration`

- `SslAutoConfiguration`


### Switching from VMware GemFire to VMware GemFire or VMware GemFire for TAS

Spring Boot for VMware GemFire (SBDG) stopped providing support for
VMware GemFire after SBDG 1.3. SBDG 1.3 was the last version to
support both VMware GemFire and VMware GemFire. If you need
support for VMware GemFire in Spring Boot, then you will need to
downgrade to SBDG 1.3.

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Warning
</td>
<td class="content">This section is now deprecated. Spring Boot for
VMware GemFire (SBDG) no longer provides the
<code>spring-gemfire-starter</code> or related starter modules. As of
SBDG 1.4, SBDG is based on VMware GemFire 1.13. Standalone GemFire
bits based on VMware GemFire are no longer being released by
VMware, Inc. after GemFire 9.10. GemFire 9.10 was based on
VMware GemFire 1.12, and SBDG can no longer properly support
standalone GemFire bits (version ⇐ 9.10).</td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Note
</td>
<td class="content">What was Pivotal GemFire has now been rebranded as
https://pivotal.io/pivotal-gemfire[VMware GemFire] and what was Pivotal
Cloud Cache (PCC) running on Pivotal CloudFoundry (PCF) has been
rebranded as [VMware GemFire for TAS](https://pivotal.io/pivotal-cloud-cache) and
[Tanzu Application Service (TAS)](https://pivotal.io/platform),
respectively.</td>
</tr>
</tbody>
</table>

### Running a VMware GemFire cluster with Spring Boot from your IDE

As described in
[geode-clientcache-applications](#geode-clientcache-applications),
you can configure and run a small VMware GemFire cluster from
inside your IDE using Spring Boot. This is extremely helpful during
development because it enables you to manually run, test, and debug your
applications quickly and easily.

Spring Boot for VMware GemFire includes such a class:

Example 11. Spring Boot application class used to configure and
bootstrap a VMware GemFire server


``` highlight
@SpringBootApplication
@CacheServerApplication(name = "SpringBootApacheGeodeCacheServerApplication")
@SuppressWarnings("unused")
public class SpringBootApacheGeodeCacheServerApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(SpringBootApacheGeodeCacheServerApplication.class)
            .web(WebApplicationType.NONE)
            .build()
            .run(args);
    }

    @Configuration
    @UseLocators
    @Profile("clustered")
    static class ClusteredConfiguration { }

    @Configuration
    @EnableLocator
    @EnableManager(start = true)
    @Profile("!clustered")
    static class LonerConfiguration { }

}
```

This class is a proper Spring Boot application that you can use to
configure and bootstrap multiple VMware GemFire servers and join
them together to form a small cluster. You only need to modify the
runtime configuration of this class to startup multiple servers.

Initially, you will need to start a single (primary) server with an
embedded Locator and Manager.

The Locator enables members in the cluster to locate one another and
lets new members join the cluster as a peer. The Locator also lets
clients connect to the servers in the cluster. When the cache client’s
connection pool is configured to use Locators, the pool of connections
can intelligently route data requests directly to the server hosting the
data (a.k.a. single-hop access), especially when the data is
partitioned/sharded across multiple servers in the cluster.
Locator-based connection pools include support for load balancing
connections and handling automatic fail-over in the event of failed
connections, among other things.

The Manager lets you connect to this server using Gfsh
(VMware GemFire's
[command-line shell tool](https://geode.apache.org/docs/guide/115/tools_modules/gfsh/chapter_overview.html)).

To start your primary server, create a run configuration in your IDE for
the `SpringBootApacheGeodeCacheServerApplication` class using the
following, recommended JRE command-line options:

Example 12. Server 1 run profile configuration


``` highlight
-server -ea -Dspring.profiles.active=
```

Run the class. You should see output similar to the following:

Example 13. Server 1 output on startup


``` highlight
/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/bin/java -server -ea -Dspring.profiles.active= "-javaagent:/Applications/IntelliJ IDEA 17 CE.app/Contents/lib/idea_rt.jar=62866:
...
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See https://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.0.3.RELEASE)

[info 2018/06/24 21:42:28.183 PDT <main> tid=0x1] Starting SpringBootApacheGeodeCacheServerApplication on jblum-mbpro-2.local with PID 41795 (/Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build/classes/main started by jblum in /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build)

[info 2018/06/24 21:42:28.186 PDT <main> tid=0x1] No active profile set, falling back to default profiles: default

[info 2018/06/24 21:42:28.278 PDT <main> tid=0x1] Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@6fa51cd4: startup date [Sun Jun 24 21:42:28 PDT 2018]; root of context hierarchy

[warn 2018/06/24 21:42:28.962 PDT <main> tid=0x1] @Bean method PdxConfiguration.pdxDiskStoreAwareBeanFactoryPostProcessor is non-static and returns an object assignable to Spring's BeanFactoryPostProcessor interface. This will result in a failure to process annotations such as @Autowired, @Resource and @PostConstruct within the method's declaring @Configuration class. Add the 'static' modifier to this method to avoid these container lifecycle issues; see @Bean javadoc for complete details.

[info 2018/06/24 21:42:30.036 PDT <main> tid=0x1]
---------------------------------------------------------------------------

  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with this
  work for additional information regarding copyright ownership.

  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with the
  License.  You may obtain a copy of the License at

  https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
  License for the specific language governing permissions and limitations
  under the License.

---------------------------------------------------------------------------
Build-Date: 2017-09-16 07:20:46 -0700
Build-Id: abaker 0
Build-Java-Version: 1.8.0_121
Build-Platform: Mac OS X 10.12.3 x86_64
Product-Name: Apache Geode
Product-Version: 1.2.1
Source-Date: 2017-09-08 11:57:38 -0700
Source-Repository: release/1.2.1
Source-Revision: 0b881b515eb1dcea974f0f5c1b40da03d42af9cf
Native version: native code unavailable
Running on: /10.0.0.121, 8 cpu(s), x86_64 Mac OS X 10.10.5
Communications version: 65
Process ID: 41795
User: jblum
Current dir: /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Home dir: /Users/jblum
Command Line Parameters:
  -ea
  -Dspring.profiles.active=
  -javaagent:/Applications/IntelliJ IDEA 17 CE.app/Contents/lib/idea_rt.jar=62866:/Applications/IntelliJ IDEA 17 CE.app/Contents/bin
  -Dfile.encoding=UTF-8
Class Path:
  /Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/jre/lib/charsets.jar
  ...
Library Path:
  /Users/jblum/Library/Java/Extensions
  /Library/Java/Extensions
  /Network/Library/Java/Extensions
  /System/Library/Java/Extensions
  /usr/lib/java
  .
System Properties:
    PID = 41795
  ...
[info 2018/06/24 21:42:30.045 PDT <main> tid=0x1] Startup Configuration:
 ### GemFire Properties defined with api ###
disable-auto-reconnect=true
jmx-manager=true
jmx-manager-port=1099
jmx-manager-start=true
jmx-manager-update-rate=2000
log-level=config
mcast-port=0
name=SpringBootApacheGeodeCacheServerApplication
start-locator=localhost[10334]
use-cluster-configuration=false
### GemFire Properties using default values ###
ack-severe-alert-threshold=0
...

[info 2018/06/24 21:42:30.090 PDT <main> tid=0x1] Starting peer location for Distribution Locator on localhost/127.0.0.1

[info 2018/06/24 21:42:30.093 PDT <main> tid=0x1] Starting Distribution Locator on localhost/127.0.0.1

[info 2018/06/24 21:42:30.094 PDT <main> tid=0x1] Locator was created at Sun Jun 24 21:42:30 PDT 2018

[info 2018/06/24 21:42:30.094 PDT <main> tid=0x1] Listening on port 10334 bound on address localhost/127.0.0.1

...

[info 2018/06/24 21:42:30.685 PDT <main> tid=0x1] Initializing region _monitoringRegion_10.0.0.121<v0>1024

[info 2018/06/24 21:42:30.688 PDT <main> tid=0x1] Initialization of region _monitoringRegion_10.0.0.121<v0>1024 completed

...

[info 2018/06/24 21:42:31.570 PDT <main> tid=0x1] CacheServer Configuration:   port=40404 max-connections=800 max-threads=0 notify-by-subscription=true socket-buffer-size=32768 maximum-time-between-pings=60000 maximum-message-count=230000 message-time-to-live=180 eviction-policy=none capacity=1 overflow directory=. groups=[] loadProbe=ConnectionCountProbe loadPollInterval=5000 tcpNoDelay=true

[info 2018/06/24 21:42:31.588 PDT <main> tid=0x1] Started SpringBootApacheGeodeCacheServerApplication in 3.77 seconds (JVM running for 5.429)
```

You can now connect to this server by using Gfsh:

Example 14. Connect with Gfsh


``` highlight
$ echo $GEMFIRE
/Users/jblum/pivdev/apache-geode-1.2.1
jblum-mbpro-2:lab jblum$
jblum-mbpro-2:lab jblum$ gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    1.2.1

Monitor and Manage Apache Geode

gfsh>connect
Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=10.0.0.121, port=1099] ..
Successfully connected to: [host=10.0.0.121, port=1099]


gfsh>list members
                   Name                     | Id
------------------------------------------- | --------------------------------------------------------------------------
SpringBootApacheGeodeCacheServerApplication | 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:41795)<ec><v0>:1024


gfsh>describe member --name=SpringBootApacheGeodeCacheServerApplication
Name        : SpringBootApacheGeodeCacheServerApplication
Id          : 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:41795)<ec><v0>:1024
Host        : 10.0.0.121
Regions     :
PID         : 41795
Groups      :
Used Heap   : 184M
Max Heap    : 3641M
Working Dir : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Log file    : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Locators    : localhost[10334]

Cache Server Information
Server Bind              :
Server Port              : 40404
Running                  : true
Client Connections       : 0
```

Now you can run additional servers to scale-out your cluster.

To do so, you must vary the name of the members you add to your cluster
as peers. VMware GemFire requires members in a cluster to be named
and for the names of each member in the cluster to be unique.

Additionally, since we are running multiple instances of our
`SpringBootApacheGeodeCacheServerApplication` class, which also embeds a
`CacheServer` component enabling cache clients to connect. Therefore,
you must vary the ports used by the embedded services.

Fortunately, you do not need to run another embedded Locator or Manager
(you need only one of each in this case). Therefore, you can switch
profiles from non-clustered to using the Spring "clustered" profile,
which includes different configuration (the `ClusterConfiguration`
class) to connect another server as a peer member in the cluster, which
currently has only one member, as shown in Gfsh with the `list members`
command (shown earlier).

To add another server, set the member name and `CacheServer` port to
different values with the following run configuration:

Example 15. Run profile configuration for server 2


``` highlight
-server -ea -Dspring.profiles.active=clustered -Dspring.data.gemfire.name=ServerTwo -Dspring.data.gemfire.cache.server.port=41414
```

Notice that we explicitly activated the "clustered" Spring profile,
which enables the configuration provided in the nested
`ClusteredConfiguration` class while disabling the configuration
provided in the `LonerConfiguration` class.

The `ClusteredConfiguration` class is also annotated with
`@UseLocators`, which sets the VMware GemFire `locators` property
to "localhost[10334]". By default, it assumes that the Locator runs on
localhost, listening on the default Locator port of 10334. You can
adjust your `locators` connection endpoint if your Locators run
elsewhere in your network by using the `locators` attribute of the
`@UseLocators` annotation.

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Tip
</td>
<td class="content">In production environments, it is common to run
multiple Locators in separate processes. Running multiple Locators
provides redundancy in case a Locator fails. If all Locators in your
cluster fail, then your cluster will continue to run, but no other
members will be able to join the cluster, which is important when
scaling out the cluster. Clients also will not be able to connect.
Restart the Locators if this happens.</td>
</tr>
</tbody>
</table>

Also, we set the `spring.data.gemfire.name` property to `ServerTwo`,
adjusting the name of our member when it joins the cluster as a peer.

Finally, we set the `spring.data.gemfire.cache.server.port` property to
`41414` to vary the `CacheServer` port used by `ServerTwo`. The default
`CacheServer` port is `40404`. If we had not set this property before
starting `ServerTwo`, we would have encounter a
`java.net.BindException`.

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Tip
</td>
<td class="content">Both <code>spring.data.gemfire.name</code> and
<code>spring.data.gemfire.cache.server.port</code> are well-known
properties used by SDG to dynamically configure VMware GemFire with
a Spring Boot <code>application.properties</code> file or by using Java
System properties. You can find these properties in the annotation
Javadoc in SDG’s annotation-based configuration model. For example, see
the Javadoc for the
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#port--[<code>spring.data.gemfire.cache.server.port</code>
property]. Most SDG annotations include corresponding properties that
can be defined in Spring Boot <code>application.properties</code>, which
is explained in detail in the
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-properties/#bootstrap-annotation-config-properties[documentation].</td>
</tr>
</tbody>
</table>

After starting our second server, `ServerTwo`, we should see output
similar to the following at the command-line and in Gfsh when we again
`list members` and `describe member`:

Example 16. Gfsh output after starting server 2


``` highlight
gfsh>list members
                   Name                     | Id
------------------------------------------- | --------------------------------------------------------------------------
SpringBootApacheGeodeCacheServerApplication | 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:41795)<ec><v0>:1024
ServerTwo                                   | 10.0.0.121(ServerTwo:41933)<v1>:1025


gfsh>describe member --name=ServerTwo
Name        : ServerTwo
Id          : 10.0.0.121(ServerTwo:41933)<v1>:1025
Host        : 10.0.0.121
Regions     :
PID         : 41933
Groups      :
Used Heap   : 165M
Max Heap    : 3641M
Working Dir : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Log file    : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Locators    : localhost[10334]

Cache Server Information
Server Bind              :
Server Port              : 41414
Running                  : true
Client Connections       : 0
```

When we list the members of the cluster, we see `ServerTwo`, and when we
`describe` `ServerTwo`, we see that its `CacheServer` port is
appropriately set to `41414`.

We can add one more server, `ServerThree`, by using the following run
configuration:

Example 17. Add server three to our cluster


``` highlight
-server -ea -Dspring.profiles.active=clustered -Dspring.data.gemfire.name=ServerThree -Dspring.data.gemfire.cache.server.port=42424
```

We again see similar output at the command-line and in Gfsh:

Example 18. Gfsh output after starting server 3


``` highlight
gfsh>list members
                   Name                     | Id
------------------------------------------- | --------------------------------------------------------------------------
SpringBootApacheGeodeCacheServerApplication | 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:41795)<ec><v0>:1024
ServerTwo                                   | 10.0.0.121(ServerTwo:41933)<v1>:1025
ServerThree                                 | 10.0.0.121(ServerThree:41965)<v2>:1026


gfsh>describe member --name=ServerThree
Name        : ServerThree
Id          : 10.0.0.121(ServerThree:41965)<v2>:1026
Host        : 10.0.0.121
Regions     :
PID         : 41965
Groups      :
Used Heap   : 180M
Max Heap    : 3641M
Working Dir : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Log file    : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Locators    : localhost[10334]

Cache Server Information
Server Bind              :
Server Port              : 42424
Running                  : true
Client Connections       : 0
```

Congratulations. You have just started a small VMware GemFire
cluster with 3 members by using Spring Boot from inside your IDE.

Now you can build and run a Spring Boot, VMware GemFire
`ClientCache` application that connects to this cluster. To do so,
include and use Spring Boot for VMware GemFire.

### Testing

[Spring Test for
VMware GemFire](https://github.com/spring-projects/spring-test-data-geode)
(STDG) is a relatively new project to help you write both unit and
integration tests when you use VMware GemFire in a Spring context.
In fact, the entire
[test suite](https://github.com/spring-projects/spring-boot-data-geode/tree/1.7.4/tree/master/spring-geode-autoconfigure/src/test/java/org/springframework/geode/boot/autoconfigure)
in Spring Boot for VMware GemFire is based on this project.

All Spring projects that integrate with VMware GemFire will use
this new test framework for all their testing needs, making this new
test framework for VMware GemFire a proven and reliable solution
for all your VMware GemFire application testing needs when using
Spring as well.

In future versions, this reference guide will include an entire chapter
on testing along with samples. In the meantime, look to the STDG
[README](https://github.com/spring-projects/spring-test-data-geode#stdg-in-a-nutshell).

### Examples

The definitive source of truth on how to best use Spring Boot for
VMware GemFire is to refer to the [samples](#geode-samples).

See also the [Temperature
Service](https://github.com/jxblum/temperature-service), Spring Boot
application that implements a temperature sensor and monitoring,
Internet of Things (IOT) example. The example uses SBDG to showcase
VMware GemFire CQ, function implementations and executions, and
positions VMware GemFire as a caching provider in Spring’s Cache
Abstraction. It is a working, sophisticated, and complete example, and
we highly recommend it as a good starting point for real-world use
cases.

See the [Boot
example](https://github.com/jxblum/contacts-application/tree/master/boot-example)
from the contact application reference implementation (RI) for Spring
Data for VMware GemFire (SDG) as yet another example.

### References

1.  Spring Framework [Reference Guide](https://docs.spring.io/spring/docs/current/spring-framework-reference) |
    [Javadoc](https://docs.spring.io/spring/docs/current/javadoc-api\)

2.  Spring Boot [Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/html) |
    [Javadoc](https://docs.spring.io/spring-boot/docs/current/api)

3.  Spring Data Commons [Reference Guide](https://docs.spring.io/spring-data/commons/docs/current/reference/html) | [Javadoc](https://docs.spring.io/spring-data/commons/docs/current/api)

4.  Spring Data for VMware GemFire
    [Reference Guide](https://docs.spring.io/spring-data/geode/docs/current/reference/html) |
    [Javadoc](https://docs.spring.io/spring/docs/current/javadoc-api)

5.  Spring Session for VMware GemFire
    [Reference Guide](https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/reference/html5) |
    [Javadoc](https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api)

6.  Spring Test for VMware GemFire
    [README](https://github.com/spring-projects/spring-test-data-geode#spring-test-framework-for-apache-geode%E2%80%94%E2%80%8Bvmware-tanzu-gemfire)

7.  VMware GemFire [User Guide](https://geode.apache.org/docs/guide/115) |
    [Javadoc](https://geode.apache.org/releases/latest/javadoc)

