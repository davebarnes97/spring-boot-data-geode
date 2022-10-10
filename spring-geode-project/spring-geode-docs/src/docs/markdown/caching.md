<div id="header">

</div>

<div id="content">

<div class="sect1">

## Caching with Apache Geode

<div class="sectionbody">

<div class="paragraph">

One of the easiest, quickest and least invasive ways to start using
{pivotal-gemfire-name} in your Spring Boot applications is to use
{pivotal-gemfire-name} as a
{spring-framework-docs}/integration.html#cache-store-configuration\[caching
provider\] in {spring-framework-docs}/integration.html#cache\[Spring’s
Cache Abstraction\]. SDG
{spring-framework-docs}/integration.html#cache-store-configuration-gemfire\[enables\]
{pivotal-gemfire-name} to function as a caching provider in Spring’s
Cache Abstraction.

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
<td class="content">See the <em>Spring Data for {pivotal-gemfire-name}
Reference Guide</em> for more details on the
{spring-data-geode-docs-html}/#apis:spring-cache-abstraction[support]
and
{spring-data-geode-docs-html}/#bootstrap-annotation-config-caching[configuration]
of {pivotal-gemfire-name} as a caching provider in Spring’s Cache
Abstraction.</td>
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
<td class="content">Make sure you thoroughly understand the
{spring-framework-docs}/integration.html#cache-strategies[concepts]
behind Spring’s Cache Abstraction before you continue.</td>
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
<td class="content">See also the relevant section on
{spring-boot-docs-html}/#boot-features-caching[caching] in Spring Boot’s
reference documentation. Spring Boot even provides auto-configuration
support for a few of the simple
{spring-boot-docs-html}/#_supported_cache_providers[caching
providers].</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Indeed, caching can be an effective software design pattern to avoid the
cost of invoking a potentially expensive operation when, given the same
input, the operation yields the same output, every time.

</div>

<div class="paragraph">

Some classic examples of caching include, but are not limited to,
looking up a customer by name or account number, looking up a book by
ISBN, geocoding a physical address, and caching the calculation of a
person’s credit score when the person applies for a financial loan.

</div>

<div class="paragraph">

If you need the proven power of an enterprise-class caching solution,
with strong consistency, high availability, low latency, and multi-site
(WAN) capabilities, then you should consider
[{pivotal-gemfire-name}](https://geode.apache.org/). Alternatively,
VMWare, Inc. offers a commercial solution, built on
{pivotal-gemfire-name}, called {pivotal-gemfire-name}.

</div>

<div class="paragraph">

Spring’s
{spring-framework-docs}/integration.html#cache-annotations\[declarative,
annotation-based caching\] makes it simple to get started with caching,
which is as easy as annotating your application components with the
appropriate Spring cache annotations.

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
<td class="content">Spring’s declarative, annotation-based caching also
{spring-framework-docs}/integration.html#cache-jsr-107[supports] JSR-107
JCache annotations.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

For example, suppose you want to cache the results of determining a
person’s eligibility when applying for a loan. A person’s financial
status is unlikely to change in the time that the computer runs the
algorithms to compute a person’s eligibility after all the financial
information for the person has been collected, submitted for review and
processed.

</div>

<div class="paragraph">

Our application might consist of a financial loan service to process a
person’s eligibility over a given period of time:

</div>

<div class="exampleblock">

<div class="title">

Example 1. Spring application service component applicable to caching

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Service
class FinancialLoanApplicationService {

    @Cacheable("EligibilityDecisions")
    EligibilityDecision processEligibility(Person person, Timespan timespan) {
        // ...
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Notice the `@Cacheable` annotation declared on the
`processEligibility(:Person, :Timespan)` method of our service class.

</div>

<div class="paragraph">

When the `FinancialLoanApplicationService.processEligibility(..)` method
is called, Spring’s caching infrastructure first consults the
"EligibilityDecisions" cache to determine if a decision has already been
computed for the given person within the given span of time. If the
person’s eligibility in the given time frame has already been
determined, the existing decision is returned from the cache. Otherwise,
the `processEligibility(..)` method is invoked and the result of the
method is cached when the method returns, before returning the decision
to the caller.

</div>

<div class="paragraph">

Spring Boot for {pivotal-gemfire-name} auto-configures
{pivotal-gemfire-name} as the caching provider when
{pivotal-gemfire-name} is declared on the application classpath and when
no other caching provider (such as Redis) has been configured.

</div>

<div class="paragraph">

If Spring Boot for {pivotal-gemfire-name} detects that another cache
provider has already been configured, then {pivotal-gemfire-name} will
not function as the caching provider for the application. This lets you
configure another store, such as Redis, as the caching provider and
perhaps use {pivotal-gemfire-name} as your application’s persistent
store.

</div>

<div class="paragraph">

The only other requirement to enable caching in a Spring Boot
application is for the declared caches (as specified in Spring’s or
JSR-107’s caching annotations) to have been created and already exist,
especially before the operation on which caching was applied is invoked.
This means the backend data store must provide the data structure that
serves as the cache. For {pivotal-gemfire-name}, this means a cache
`Region`.

</div>

<div class="paragraph">

To configure the necessary Regions that back the caches declared in
Spring’s cache annotations, use Spring Data for {pivotal-gemfire-name}'s
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/EnableCachingDefinedRegions.html\[`@EnableCachingDefinedRegions`\]
annotation.

</div>

<div class="paragraph">

The following listing shows a complete Spring Boot application:

</div>

<div class="exampleblock">

<div class="title">

Example 2. Spring Boot cache enabled application using
{pivotal-gemfire-name}

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
package example.app;

@SpringBootApplication
@EnableCachingDefinedRegions
class FinancialLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancialLoanApplication.class, args);
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
<td class="content">The <code>FinancialLoanApplicationService</code> is
picked up by Spring’s classpath component scan, since this class is
annotated with Spring’s <code>@Service</code> stereotype
annotation.</td>
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
<td class="content">You can set the <code>DataPolicy</code> of the
Region created through the <code>@EnableCachingDefinedRegions</code>
annotation by setting the <code>clientRegionShortcut</code> attribute to
a valid enumerated value.</td>
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
<td class="content">Spring Boot for {pivotal-gemfire-name} does not
recognize nor apply the <code>spring.cache.cache-names</code> property.
Instead, you should use SDG’s <code>@EnableCachingDefinedRegions</code>
on an appropriate Spring Boot application <code>@Configuration</code>
class.</td>
</tr>
</tbody>
</table>

</div>

<div class="sect2">

### Look-Aside Caching, Near Caching, Inline Caching, and Multi-Site Caching

<div class="paragraph">

Four different types of caching patterns can be applied with Spring when
using {pivotal-gemfire-name} for your application caching needs:

</div>

<div class="ulist">

- Look-aside caching

- Near caching

- \[Async\] Inline caching

- Multi-site caching

</div>

<div class="paragraph">

Typically, when most users think of caching, they think of Look-aside
caching. This is the default caching pattern applied by Spring’s Cache
Abstraction.

</div>

<div class="paragraph">

In a nutshell, Near caching keeps the data closer to where the data is
used, thereby improving on performance due to lower latencies when data
is needed (no extra network hops). This also improves application
throughput — that is, the amount of work completed in a given period of
time.

</div>

<div class="paragraph">

Within Inline caching\_, developers have a choice between synchronous
(read/write-through) and asynchronous (write-behind) configurations
depending on the application use case and requirements. Synchronous,
read/write-through Inline caching is necessary if consistency is a
concern. Asynchronous, write-behind Inline caching is applicable if
throughput and low-latency are a priority.

</div>

<div class="paragraph">

Within Multi-site caching, there are active-active and active-passive
arrangements. More details on Multi-site caching will be presented in a
later release.

</div>

<div class="sect3">

#### Look-Aside Caching

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
href="guides/caching-look-aside.html">guide</a> and
{github-samples-url}/caching/look-aside[code] to see Look-aside caching
with {pivotal-gemfire-name} in action.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

The caching pattern demonstrated in the preceding example is a form of
[Look-aside
caching](https://content.pivotal.io/blog/an-introduction-to-look-aside-vs-inline-caching-patterns)
(or "*Cache Aside*").

</div>

<div class="paragraph">

Essentially, the data of interest is searched for in the cache first,
before calling a potentially expensive operation, such as an operation
that makes an IO- or network-bound request that results in either a
blocking or a latency-sensitive computation.

</div>

<div class="paragraph">

If the data can be found in the cache (stored in-memory to reduce
latency), the data is returned without ever invoking the expensive
operation. If the data cannot be found in the cache, the operation must
be invoked. However, before returning, the result of the operation is
cached for subsequent requests when the same input is requested again by
another caller, resulting in much improved response times.

</div>

<div class="paragraph">

The typical Look-aside caching pattern applied in your Spring
application code looks similar to the following:

</div>

<div class="exampleblock">

<div class="title">

Example 3. Look-Aside Caching Pattern Applied

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Service
class CustomerService {

  private final CustomerRepository customerRepository;

  @Cacheable("Customers")
  Customer findByAcccount(Account account) {

    // pre-processing logic here

    Customer customer = customerRepository.findByAccoundNumber(account.getNumber());

    // post-processing logic here

    return customer;
  }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

In this design, the `CustomerRepository` is perhaps a JDBC- or
JPA/Hibernate-backed implementation that accesses the external data
source (for example, an RDBMS) directly. The `@Cacheable` annotation
wraps, or "decorates", the `findByAccount(:Account):Customer` operation
(method) to provide caching behavior.

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
<td class="content">This operation may be expensive because it may
validate the customer’s account before looking up the customer, pull
multiple bits of information to retrieve the customer record, and so
on — hence the need for caching.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### Near Caching

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
href="guides/caching-near.html">guide</a> and
{github-samples-url}/caching/near[code] to see Near caching with
{pivotal-gemfire-name} in action.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Near caching is another pattern of caching where the cache is collocated
with the application. This is useful when the caching technology is
configured in a client/server arrangement.

</div>

<div class="paragraph">

We already mentioned that Spring Boot for {pivotal-gemfire-name}
[provides](clientcache-applications.html#geode-clientcache-applications)
an auto-configured `ClientCache` instance by default. A `ClientCache`
instance is most effective when the data access operations, including
cache access, are distributed to the servers in a cluster that is
accessible to the client and, in most cases, multiple clients. This lets
other cache client applications access the same data. However, this also
means the application incurs a network hop penalty to evaluate the
presence of the data in the cache.

</div>

<div class="paragraph">

To help avoid the cost of this network hop in a client/server topology,
a local cache can be established to maintain a subset of the data in the
corresponding server-side cache (that is, a Region). Therefore, the
client cache contains only the data of interest to the application. This
"local" cache (that is, a client-side Region) is consulted before
forwarding the lookup request to the server.

</div>

<div class="paragraph">

To enable Near caching when using {pivotal-gemfire-name}, change the
Region’s (that is the `Cache` in Spring’s Cache Abstraction) data
management policy from `PROXY` (the default) to `CACHING_PROXY`:

</div>

<div class="exampleblock">

<div class="title">

Example 4. Enable Near Caching with {pivotal-gemfire-name}

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.CACHING_PROXY)
class FinancialLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancialLoanApplication.class, args);
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
<td class="content">The default client Region data management policy is
{apache-geode-javadoc}/org/apache/geode/cache/client/ClientRegionShortcut.html#PROXY[<code>ClientRegionShortcut.PROXY</code>].
As a result, all data access operations are immediately forwarded to the
server.</td>
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
<td class="content">See also the {pivotal-gemfire-name} documentation
concerning
{apache-geode-docs}/developing/events/how_client_server_distribution_works.html[client/server
event distribution] and, specifically, “Client Interest Registration on
the Server,” which applies when you use client
<code>CACHING_PROXY</code> Regions to manage state in addition to the
corresponding server-side Region. This is necessary to receive updates
on entries in the Region that might have been changed by other clients
that have access to the same data.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### Inline Caching

<div class="paragraph">

The next pattern of caching covered in this chapter is Inline caching.

</div>

<div class="paragraph">

You can apply two different configurations of Inline caching to your
Spring Boot applications when you use the Inline caching pattern:
synchronous (read/write-through) and asynchronous (write-behind).

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
<td class="content">Asynchronous (currently) offers only write
capabilities, from the cache to the external data source. There is no
option to asynchronously and automatically load the cache when the value
becomes available in the external data source.</td>
</tr>
</tbody>
</table>

</div>

<div class="sect4">

##### Synchronous Inline Caching

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
href="guides/caching-inline.html">guide</a> and
{github-samples-url}/caching/inline[code] to see Inline caching with
{pivotal-gemfire-name} in action.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

When employing Inline caching and a cache miss occurs, the application
service method might not be invoked still, since a cache can be
configured to invoke a loader to load the missing entry from an external
data source.

</div>

<div class="paragraph">

With {pivotal-gemfire-name}, you can configure the cache (or, to use
{pivotal-gemfire-name} terminology, the Region) with a
{apache-geode-javadoc}/org/apache/geode/cache/CacheLoader.html\[`CacheLoader`\].
A `CacheLoader` is implemented to retrieve missing values from an
external data source when a cache miss occurs. The external data source
could be an RDBMS or any other type of data store (for example, another
NoSQL data store, such as Apache Cassandra, MongoDB, or Neo4j).

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
<td class="content">See {pivotal-gemfire-name}'s User Guide on
{apache-geode-docs}/developing/outside_data_sources/how_data_loaders_work.html[data
loaders] for more details.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Likewise, you can also configure an {pivotal-gemfire-name} Region with a
{apache-geode-javadoc}/org/apache/geode/cache/CacheWriter.html\[`CacheWriter`\].
A `CacheWriter` is responsible for writing an entry that has been put
into the Region to the backend data store, such as an RDBMS. This is
referred to as a write-through operation, because it is synchronous. If
the backend data store fails to be updated, the entry is not stored in
the Region. This helps to ensure consistency between the backend data
store and the {pivotal-gemfire-name} Region.

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
<td class="content">You can also implement Inline caching using
asynchronous write-behind operations by registering an
{apache-geode-javadoc}/org/apache/geode/cache/asyncqueue/AsyncEventListener.html[<code>AsyncEventListener</code>]
on an
{apache-geode-javadoc}/org/apache/geode/cache/asyncqueue/AsyncEventQueue.html[<code>AsyncEventQueue</code>]
attached to a server-side Region. See {pivotal-gemfire-name}'s User
Guide for more
{apache-geode-docs}/developing/events/implementing_write_behind_event_handler.html[details].
We cover asynchronous write-behind Inline caching in the next
section.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

The typical pattern of Inline caching when applied to application code
looks similar to the following:

</div>

<div class="exampleblock">

<div class="title">

Example 5. Inline Caching Pattern Applied

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Service
class CustomerService {

  private CustomerRepository customerRepository;

  Customer findByAccount(Account account) {

      // pre-processing logic here

      Customer customer = customerRepository.findByAccountNumber(account.getNumber());

      // post-processing logic here.

      return customer;
  }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The main difference is that no Spring or JSR-107 caching annotations are
applied to the application’s service methods, and the
`CustomerRepository` accesses {pivotal-gemfire-name} directly and the
RDBMS indirectly.

</div>

<div class="sect5">

###### Implementing CacheLoaders and CacheWriters for Inline Caching

<div class="paragraph">

You can use Spring to configure a `CacheLoader` or `CacheWriter` as a
bean in the Spring `ApplicationContext` and then wire the loader or
writer to a Region. Given that the `CacheLoader` or `CacheWriter` is a
Spring bean like any other bean in the Spring `ApplicationContext`, you
can inject any `DataSource` you like into the loader or writer.

</div>

<div class="paragraph">

While you can configure client Regions with `CacheLoaders` and
`CacheWriters`, it is more common to configure the corresponding
server-side Region:

</div>

<div class="exampleblock">

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@CacheServerApplication
class FinancialLoanApplicationServer {

    public static void main(String[] args) {
        SpringApplication.run(FinancialLoanApplicationServer.class, args);
    }

    @Bean("EligibilityDecisions")
    PartitionedRegionFactoryBean<Object, Object> eligibilityDecisionsRegion(
            GemFireCache gemfireCache, CacheLoader eligibilityDecisionLoader,
            CacheWriter eligibilityDecisionWriter) {

        PartitionedRegionFactoryBean<?, EligibilityDecision> eligibilityDecisionsRegion =
            new PartitionedRegionFactoryBean<>();

        eligibilityDecisionsRegion.setCache(gemfireCache);
        eligibilityDecisionsRegion.setCacheLoader(eligibilityDecisionLoader);
        eligibilityDecisionsRegion.setCacheWriter(eligibilityDecisionWriter);
        eligibilityDecisionsRegion.setPersistent(false);

        return eligibilityDecisionsRegion;
    }

    @Bean
    CacheLoader<?, EligibilityDecision> eligibilityDecisionLoader(
            DataSource dataSource) {

        return new EligibilityDecisionLoader(dataSource);
    }

    @Bean
    CacheWriter<?, EligibilityDecision> eligibilityDecisionWriter(
            DataSource dataSource) {

        return new EligibilityDecisionWriter(dataSource);
    }

    @Bean
    DataSource dataSource() {
      // ...
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Then you could implement the
{apache-geode-javadoc}/org/apache/geode/cache/CacheLoader.html\[`CacheLoader`\]
and
{apache-geode-javadoc}/org/apache/geode/cache/CacheWriter.html\[`CacheWriter`\]
interfaces, as appropriate:

</div>

<div class="exampleblock">

<div class="title">

Example 6. EligibilityDecisionLoader

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
class EligibilityDecisionLoader implements CacheLoader<?, EligibilityDecision> {

  private final DataSource dataSource;

  EligibilityDecisionLoader(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public EligibilityDecision load(LoadHelper<?, EligibilityDecision> helper) {

    Object key = helper.getKey();

    // Use the configured DataSource to load the EligibilityDecision identified by the key
    // from a backend, external data store.
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
<td class="content">SBDG provides the
<code>org.springframework.geode.cache.support.CacheLoaderSupport</code>
<code>@FunctionalInterface</code> to conveniently implement application
<code>CacheLoaders</code>.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

If the configured `CacheLoader` still cannot resolve the value, the
cache lookup operation results in a cache miss and the application
service method is then invoked to compute the value:

</div>

<div class="exampleblock">

<div class="title">

Example 7. EligibilityDecisionWriter

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
class EligibilityDecisionWriter implements CacheWriter<?, EligibilityDecision> {

  private final DataSource dataSource;

  EligibilityDecisionWriter(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void beforeCreate(EntryEvent<?, EligiblityDecision> entryEvent) {
    // Use configured DataSource to save (e.g. INSERT) the entry value into the backend data store
  }

  public void beforeUpdate(EntryEvent<?, EligiblityDecision> entryEvent) {
    // Use the configured DataSource to save (e.g. UPDATE or UPSERT) the entry value into the backend data store
  }

  public void beforeDestroy(EntryEvent<?, EligiblityDecision> entryEvent) {
    // Use the configured DataSource to delete (i.e. DELETE) the entry value from the backend data store
  }

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
<td class="content">SBDG provides the
<code>org.springframework.geode.cache.support.CacheWriterSupport</code>
interface to conveniently implement application
<code>CacheWriters</code>.</td>
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
<td class="content">Your <code>CacheWriter</code> implementation can use
any data access technology to interface with your backend data store
(for example JDBC, Spring’s <code>JdbcTemplate</code>, JPA with
Hibernate, and others). It is not limited to using only a
<code>javax.sql.DataSource</code>. In fact, we present another, more
useful and convenient approach to implementing Inline caching in the
next section.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect5">

###### Inline Caching with Spring Data Repositories

<div class="paragraph">

Spring Boot for {pivotal-gemfire-name} offers dedicated support to
configure Inline caching with Spring Data Repositories.

</div>

<div class="paragraph">

This is powerful, because it lets you:

</div>

<div class="ulist">

- Access any backend data store supported by Spring Data (such as Redis
  for key-value or other distributed data structures, MongoDB for
  documents, Neo4j for graphs, Elasticsearch for search, and so on).

- Use complex mapping strategies (such as ORM provided by JPA with
  Hibernate).

</div>

<div class="paragraph">

We believe that users should store data where it is most easily
accessible. If you access and process documents, then MongoDB,
Couchbase, or another document store is probably going to be the most
logical choice to manage your application’s documents.

</div>

<div class="paragraph">

However, this does not mean that you have to give up
{pivotal-gemfire-name} in your application/system architecture. You can
use each data store for what it is good at. While MongoDB is excellent
at handling documents, {pivotal-gemfire-name} is a valuable choice for
consistency, high-availability/low-latency, high-throughput, multi-site,
scale-out application use cases.

</div>

<div class="paragraph">

As such, using {pivotal-gemfire-name}'s `CacheLoader` and `CacheWriter`
provides a nice integration point between itself and other data stores
to best serve your application’s use case and requirements.

</div>

<div class="paragraph">

Suppose you use JPA and Hibernate to access data managed in an Oracle
database. Then, you can configure {pivotal-gemfire-name} to
read/write-through to the backend Oracle database when performing cache
(Region) operations by delegating to a Spring Data JPA Repository.

</div>

<div class="paragraph">

The configuration might look something like:

</div>

<div class="exampleblock">

<div class="title">

Example 8. Inline caching configuration using SBDG

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@EntityScan(basePackageClasses = Customer.class)
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@EnableJpaRepositories(basePackageClasses = CustomerRepository.class)
class SpringBootOracleDatabaseApacheGeodeApplication {

  @Bean
  InlineCachingRegionConfigurer<Customer, Long> inlineCachingForCustomersRegionConfigurer(
      CustomerRepository customerRepository) {

    return new InlineCachingRegionConfigurer<>(customerRepository, Predicate.isEqual("Customers"));
  }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

SBDG provides the `InlineCachingRegionConfigurer<ENTITY, ID>` interface.

</div>

<div class="paragraph">

Given a `Predicate` to express the criteria used to match the target
Region by name and a Spring Data `CrudRepository`, the
`InlineCachingRegionConfigurer` configures and adapts the Spring Data
`CrudRepository` as a `CacheLoader` and `CacheWriter` registered on the
Region (for example, "Customers") to enable Inline caching
functionality.

</div>

<div class="paragraph">

You need only declare `InlineCachingRegionConfigurer` as a bean in the
Spring `ApplicationContext` and make the association between the Region
(by name) and the appropriate Spring Data `CrudRepository`.

</div>

<div class="paragraph">

In this example, we used JPA and Spring Data JPA to store and retrieve
data stored in the cache (Region) to and from a backend database.
However, you can inject any Spring Data Repository for any data store
(Redis, MongoDB, and others) that supports the Spring Data Repository
abstraction.

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
<td class="content">If you want only to support one-way data access
operations when you use Inline caching, you can use either the
<code>RepositoryCacheLoaderRegionConfigurer</code> for reads or the
<code>RepositoryCacheWriterRegionConfigurer</code> for writes, instead
of the <code>InlineCachingRegionConfigurer</code>, which supports both
reads and writes.</td>
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
<td class="content">To see a similar implementation of Inline caching
with a database (an in-memory HSQLDB database) in action, see the <a
href="https://github.com/spring-projects/spring-boot-data-geode/blob/master/spring-geode/src/test/java/org/springframework/geode/cache/inline/database/InlineCachingWithDatabaseIntegrationTests.java"><code>InlineCachingWithDatabaseIntegrationTests</code></a>
test class from the SBDG test suite. A dedicated sample will be provided
in a future release.</td>
</tr>
</tbody>
</table>

</div>

</div>

</div>

<div class="sect4">

##### Asynchronous Inline Caching

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
href="guides/caching-inline-async.html">guide</a> and
{github-samples-url}/caching/inline-async[code] to see asynchronous
Inline caching with {pivotal-gemfire-name} in action.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

If consistency between the cache and your external data source is not a
concern, and you need only write from the cache to the backend data
store periodically, you can employ asynchronous (write-behind) Inline
caching.

</div>

<div class="paragraph">

As the term, "write-behind", implies, a write to the backend data store
is asynchronous and not strictly tied to the cache operation. As a
result, the backend data store is in an "eventually consistent" state,
since the cache is primarily used by the application at runtime to
access and manage data. In this case, the backend data store is used to
persist the state of the cache (and that of the application) at periodic
intervals.

</div>

<div class="paragraph">

If multiple applications are updating the backend data store
concurrently, you could combine a `CacheLoader` to synchronously read
through to the backend data store and keep the cache up-to-date as well
as asynchronously write behind from the cache to the backend data store
when the cache is updated to eventually inform other interested
applications of data changes. In this capacity, the backend data store
is still the primary System of Record (SoR).

</div>

<div class="paragraph">

If data processing is not time sensitive, you can gain a performance
advantage from quantity-based or time-based batch updates.

</div>

<div class="sect5">

###### Implementing an AsyncEventListener for Inline Caching

<div class="paragraph">

If you were to configure asynchronous, write-behind Inline caching by
hand, you would need to do the following yourself:

</div>

<div class="olist arabic">

1.  Implement an `AsyncEventListener` to write to an external data
    source on cache events.

2.  Configure and register the listener with an `AsyncEventQueue` (AEQ).

3.  Create a Region to serve as the source of cache events and attach
    the AEQ to the Region.

</div>

<div class="paragraph">

The advantage of this approach is that you have access to and control
over low-level configuration details. The disadvantage is that with more
moving parts, it is easier to make errors.

</div>

<div class="paragraph">

Following on from our synchronous, read/write-through, Inline caching
examples from the prior sections, our `AsyncEventListener`
implementation might appear as follows:

</div>

<div class="exampleblock">

<div class="title">

Example 9. Example `AsyncEventListener` for Asynchronous, Write-Behind
Inline Caching

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Component
class ExampleAsyncEventListener implements AsyncEventListener {

    private final DataSource dataSource;

    ExampleAsyncEventListener(DataSoruce dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean processEvents(List<AsyncEvent> events) {

        // Iterate over the ordered AsyncEvents and use the configured DataSource
        // to write to the external, backend DataSource

    }
}
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
<td class="content">Instead of directly injecting a
<code>DataSource</code> into your <code>AsyncEventListener</code>, you
could use JDBC, Spring’s <code>JdbcTemplate</code>, JPA and Hibernate,
or another data access API or framework. Later in this chapter, we show
how SBDG simplifies the <code>AsyncEventListener</code> implementation
by using Spring Data Repositories.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Then we need to register this listener with an `AsyncEventQueue` (step 2
from the procedure shown earlier) and attach it to the target Region
that will be the source of the cache events we want to persist
asynchronously (step 3):

</div>

<div class="exampleblock">

<div class="title">

Example 10. Create and Configure an `AsyncEventQueue`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Configuration
@PeerCacheApplication
class GeodeConfiguration {

    @Bean
    DataSource exampleDataSource() {
        // Construct and configure a data store specific DataSource
    }

    @Bean
    ExampleAsyncEventListener exampleAsyncEventListener(DataSource dataSource) {
        return new ExampleAsyncEventListener(dataSource);
    }

    @Bean
    AsyncEventQueueFactoryBean exampleAsyncEventQueue(Cache peerCache, ExampleAsyncEventListener listener) {

        AsyncEventQueueFactoryBean asyncEventQueue = new AsyncEventQueueFactoryBean(peerCache, listener);

        asyncEventQueue.setBatchConflationEnabled(true);
        asyncEventQueue.setBatchSize(50);
        asyncEventQueue.setBatchTimeInterval(15000); // 15 seconds
        asyncEventQueue.setMaximumQueueMemory(64); // 64 MB
        // ...

        return asyncEventQueue;
    }

    @Bean("Example")
    PartitionedRegionFactoryBean<?, ?> exampleRegion(Cache peerCache, AsyncEventQueue queue) {

        PartitionedRegionFactoryBean<?, ?> exampleRegion = new PartitionedRegionFactoryBean<>();

        exampleRegion.setAsyncEventQueues(ArrayUtils.asArray(queue));
        exampleRegion.setCache(peerCache);
        // ...

        return exampleRegion;
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

While this approach affords you a lot of control over the low-level
configuration, in addition to your `AsyncEventListener` implementation,
this is a lot of boilerplate code.

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
<td class="content">See the Javadoc for SDG’s
{spring-data-geode-javadoc}/org/springframework/data/gemfire/wan/AsyncEventQueueFactoryBean.html[<code>AsyncEventQueueFactoryBean</code>]
for more detail on the configuration of the AEQ.</td>
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
<td class="content">See {pivotal-gemfire-name}'s
{apache-geode-docs}/developing/events/implementing_write_behind_event_handler.html[User
Guide] for more details on AEQs and listeners.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Fortunately, with SBDG, there is a better way.

</div>

</div>

<div class="sect5">

###### Asynchronous Inline Caching with Spring Data Repositories

<div class="paragraph">

The implementation and configuration of the `AsyncEventListener` as well
as the AEQ shown in the [preceding
section](#geode-caching-provider-inline-caching-asynchronous-asynceventlistener)
can be simplified as follows:

</div>

<div class="exampleblock">

<div class="title">

Example 11. Using SBDG to configure Asynchronous, Write-Behind Inline
Caching

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@EntityScan(basePackageClasses = ExampleEntity.class)
@EnableJpaRepositories(basePackageClasses = ExampleRepository.class)
@EnableEntityDefinedRegions(basePackageClasses = ExampleEnity.class)
class ExampleSpringBootApacheGeodeAsyncInlineCachingApplication {

    @Bean
    AsyncInlineCachingRegionConfigurer asyncInlineCachingRegionConfigurer(
            CrudRepository<ExampleEntity, Long> repository) {

        return AsyncInlineCachingRegionConfigurer.create(repository, "Example")
            .withQueueBatchConflationEnabled()
            .withQueueBatchSize(50)
            .withQueueBatchTimeInterval(Duration.ofSeconds(15))
            .withQueueMaxMemory(64);
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The `AsyncInlineCachingRegionConfigurer.create(..)` method is overloaded
to accept a `Predicate` in place of the `String` to programmatically
express more powerful matching logic and identify the target Region (by
name) on which to configure asynchronous Inline caching functionality.

</div>

<div class="paragraph">

The `AsyncInlineCachingRegionConfigurer` uses the [Builder software
design pattern](https://en.wikipedia.org/wiki/Builder_pattern) and
`withQueue*(..)` builder methods to configure the underlying
`AsyncEventQueue` (AEQ) when the queue’s configuration deviates from the
defaults, as specified by {pivotal-gemfire-name}.

</div>

<div class="paragraph">

Under the hood, the `AsyncInlineCachingRegionConfigurer` constructs a
new instance of the `RepositoryAsyncEventListener` class initialized
with the given Spring Data `CrudRepository`. The `RegionConfigurer` then
registers the listener with the AEQ and attaches it to the target
`Region`.

</div>

<div class="paragraph">

With the power of Spring Boot auto-configuration and SBDG, the
configuration is much more concise and intuitive.

</div>

</div>

<div class="sect5">

###### About `RepositoryAsyncEventListener`

<div class="paragraph">

The SBDG `RepositoryAsyncEventListener` class is the magic ingredient
behind the integration of the cache with an external data source.

</div>

<div class="paragraph">

The listener is a specialized
[adapter](https://en.wikipedia.org/wiki/Adapter_pattern) that processes
`AsyncEvents` by invoking an appropriate `CrudRepository` method based
on the cache operation. The listener requires an instance of
`CrudRepository`. The listener supports any external data source
supported by Spring Data’s Repository abstraction.

</div>

<div class="paragraph">

Backend data store, data access operations (such as INSERT, UPDATE,
DELETE, and so on) triggered by cache events are performed
asynchronously from the cache operation. This means the state of the
cache and backend data store will be "eventually consistent".

</div>

<div class="paragraph">

Given the complex nature of "eventually consistent" systems and
asynchronous concurrent processing, the `RepositoryAsyncEventListener`
lets you register a custom `AsyncEventErrorHandler` to handle the errors
that occur during processing of `AsyncEvents`, perhaps due to a faulty
backend data store data access operation (such as
`OptimisticLockingFailureException`), in an application-relevant way.

</div>

<div class="paragraph">

The `AsyncEventErrorHandler` interface is a
`java.util.function.Function` implementation and `@FunctionalInterface`
defined as:

</div>

<div class="exampleblock">

<div class="title">

Example 12. AsyncEventErrorHandler interface definition

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@FunctionalInterface
interface AsyncEventErrorHandler implements Function<AsyncEventError, Boolean> { }
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The `AsyncEventError` class encapsulates `AsyncEvent` along with the
`Throwable` that was thrown while processing the `AsyncEvent`.

</div>

<div class="paragraph">

Since the `AsyncEventErrorHandler` interface implements `Function`, you
should override the `apply(:AsyncEventError)` method to handle the error
with application-specific actions. The handler returns a `Boolean` to
indicate whether it was able to handle the error or not:

</div>

<div class="exampleblock">

<div class="title">

Example 13. Custom `AsyncEventErrorHandler` implementation

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
class CustomAsyncEventErrorHandler implements AsyncEventErrorHandler {

    @Override
    public Boolean apply(AsyncEventError error) {

        if (error.getCause() instanceof OptimisticLockingFailureException) {
            // handle optimistic locking failure if you can
            return true; // if error was successfully handled
        }
        else if (error.getCause() instanceof IncorrectResultSizeDataAccessException) {
            // handle no row or too many row update if you can
            return true; // if error was successfully handled
        }
        else {
            // ...
        }

        return false;
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You can configure the `RepositoryAsyncEventListener` with your custom
`AsyncEventErrorHandler` by using the
`AsyncInlineCachingRegionConfigurer`:

</div>

<div class="exampleblock">

<div class="title">

Example 14. Configuring a custom `AsyncEventErrorHandler`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Configuration
class GeodeConfiguration {

    @Bean
    CustomAsyncEventErrorHandler customAsyncEventErrorHandler() {
        return new CustomAsyncEventErrorHandler();
    }

    @Bean
    AsyncInlineCachingRegionConfigurer asyncInlineCachingRegionConfigurer(
            CrudRepository<?, ?> repository,
            CustomAsyncEventErrorHandler errorHandler) {

        return AsyncInlineCachingRegionConfigurer.create(repository, "Example")
            .withAsyncEventErrorHandler(errorHandler);
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Also, since `AsyncEventErrorHandler` implements `Function`, you can
[compose](https://en.wikipedia.org/wiki/Composite_pattern) multiple
error handlers by using
{jdk-javadoc}/java/util/function/Function.html#andThen-java.util.function.Function-\[`Function.andThen(:Function)`\].

</div>

<div class="paragraph">

By default, the `RepositoryAsyncEventListener` handles `CREATE`,
`UPDATE`, and `REMOVE` cache event, entry operations.

</div>

<div class="paragraph">

`CREATE` and `UPDATE` translate to `CrudRepository.save(entity)`. The
`entity` is derived from `AsyncEvent.getDeserializedValue()`.

</div>

<div class="paragraph">

`REMOVE` translates to `CrudRepository.delete(entity)`. The `entity` is
derived from `AsyncEvent.getDeserializedValue()`.

</div>

<div class="paragraph">

The cache
{apache-geode-javadoc}/org/apache/geode/cache/Operation.html\[`Operation`\]
to `CrudRepository` method is supported by the
`AsyncEventOperationRepositoryFunction` interface, which implements
`java.util.function.Function` and is a `@FunctionalInterface`.

</div>

<div class="paragraph">

This interface becomes useful if and when you want to implement
`CrudRepository` method invocations for other `AsyncEvent` `Operations`
not handled by SBDG’s `RepositoryAsyncEventListener`.

</div>

<div class="paragraph">

The `AsyncEventOperationRepositoryFunction` interface is defined as
follows:

</div>

<div class="exampleblock">

<div class="title">

Example 15. AsyncEventOperationRepositoryFunction interface definition

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@FunctionalInterface
interface AsyncEventOperationRepositoryFunction<T, ID> implements Function<AsyncEvent<ID, T>,  Boolean> {

    default boolean canProcess(AsyncEvent<ID, T> event) {
        return false;
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

`T` is the class type of the entity and `ID` is the class type of the
entity’s identifier (ID), possibly declared with Spring Data’s
{spring-data-commons-javadoc}/org/springframework/data/annotation/Id.html\[`org.springframework.data.annotation.Id`\]
annotation.

</div>

<div class="paragraph">

For convenience, SBDG provides the
`AbstractAsyncEventOperationRepositoryFunction` class for extension,
where you can provide implementations for the
`cacheProcess(:AsyncEvent)` and `doRepositoryOp(entity)` methods.

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
<td class="content">The
<code>AsyncEventOperationRepositoryFunction.apply(:AsyncEvent)</code>
method is already implemented in terms of
<code>canProcess(:AsyncEvent)</code>,
<code>resolveEntity(:AsyncEvent)</code>,
<code>doRepositoryOp(entity)</code>, and catching and handling any
<code>Throwable</code> (errors) by calling the configured
<code>AsyncEventErrorHandler</code>.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

For example, you may want to handle
{apache-geode-javadoc}/org/apache/geode/cache/Operation.html#INVALIDATE\[`Operation.INVALIDATE`\]
cache events as well, deleting the entity from the backend data store by
invoking the `CrudRepository.delete(entity)` method:

</div>

<div class="exampleblock">

<div class="title">

Example 16. Handling `AsyncEvent`, `Operation.INVALIDATE`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Component
class InvalidateAsyncEventRepositoryFunction
        extends RepositoryAsyncEventListener.AbstractAsyncEventOperationRepositoryFunction<?, ?> {

    InvalidateAsyncEventRepositoryFunction(RepositoryAsyncEventListener<?, ?> listener) {
        super(listener);
    }

    @Override
    public boolean canProcess(AsyncEvent<?, ?> event) {
        return event != null && Operation.INVALIDATE.equals(event.getOperation());
    }


    @Override
    protected Object doRepositoryOperation(Object entity) {
        getRepository().delete(entity);
        return null;
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You can then register your user-defined,
`AsyncEventOperationRepositoryFunction` (that is,
`InvalidateAsyncEventRepositoryFunction`) with the
`RepositoryAsyncEventListener` by using the
`AsyncInlineCachingRegionConfigurer`:

</div>

<div class="exampleblock">

<div class="title">

Example 17. Configuring a user-defined
`AsyncEventOperationRepositoryFunction`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
import org.springframework.geode.cache.RepositoryAsyncEventListener;

@Configuration
class GeodeConfiguration {

    @Bean
    AsyncInlineCachingRegionConfigurer asyncInlineCachingRegionConfigurer(
            CrudRepository<?, ?> repository,
            CustomAsyncEventErrorHandler errorHandler ) {

        return AsyncInlineCachingRegionConfigurer.create(repository, "ExampleRegion")
            .applyToListener(listener -> {

                if (listener instanceof RepositoryAsyncEventListener) {

                    RepositoryAsyncEventListener<?, ?> repositoryListener =
                        (RepositoryAsyncEventListener<?, ?>) listener;

                    repositoryListener.register(new InvalidAsyncEventRepositoryFunction(repositoryListener));
                }

                return listener;
            });
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

This same technique can be applied to `CREATE`, `UPDATE`, and `REMOVE`
cache operations as well, effectively overriding the default behavior
for these cache operations handled by SBDG.

</div>

</div>

<div class="sect5">

###### About `AsyncInlineCachingRegionConfigurer`

<div class="paragraph">

As we saw in the previous section, you can intercept and post-process
the essential components that are constructed and configured by the
`AsyncInlineCachingRegionConfigurer` class during initialization.

</div>

<div class="paragraph">

SBDG’s lets you intercept and post-process the `AsyncEventListener`
(such as `RepositoryAsyncEventListener`), the `AsyncEventQueueFactory`
and even the `AsyncEventQueue` created by the
`AsyncInlineCachingRegionConfigurer` (a SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/RegionConfigurer.html\[`RegionConfigurer`\])
during Spring `ApplicationContext` bean initialization.

</div>

<div class="paragraph">

The `AsyncInlineCachingRegionConfigurer` class provides the following
builder methods to intercept and post-process any of the following
{pivotal-gemfire-name} objects:

</div>

<div class="ulist">

- `applyToListener(:Function<AsyncEventListener, AsyncEventListener>)`

- `applyToQueue(:Function<AsyncEventQueue, AsyncEventQueue>)`

- `applyToQueueFactory(:Function<AsyncEventQueueFactory, AsyncEventQueueFactory>)`

</div>

<div class="paragraph">

All of these `apply*` methods accept a `java.util.function.Function`
that applies the logic of the `Function` to the {pivotal-gemfire-name}
object (such as `AsyncEventListener`), returning the object as a result.

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
<td class="content">The {pivotal-gemfire-name} object returned by the
<code>Function</code> may be the same object, a proxy, or a completely
new object. Essentially, the returned object can be anything you want.
This is the fundamental premise behind Aspect-Oriented Programming (AOP)
and the <a
href="https://en.wikipedia.org/wiki/Decorator_pattern">Decorator
software design pattern</a>.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

The `apply*` methods and the supplied `Function` let you decorate,
enhance, post-process, or otherwise modify the {pivotal-gemfire-name}
objects created by the configurer.

</div>

<div class="paragraph">

The `AsyncInlineCachingRegionConfigurer` strictly adheres to the
[open/close
principle](https://en.wikipedia.org/wiki/Open%E2%80%93closed_principle)
and is, therefore, flexibly extensible.

</div>

</div>

</div>

</div>

<div class="sect3">

#### Multi-Site Caching

<div class="paragraph">

The final pattern of caching presented in this chapter is Multi-site
caching.

</div>

<div class="paragraph">

As described earlier, there are two configuration arrangements,
depending on your application usage patterns, requirements and user
demographic: active-active and active-passive.

</div>

<div class="paragraph">

Multi-site caching, along with active-active and active-passive
configuration arrangements, are described in more detail in the sample
[guide](guides/caching-multi-site.html). Also, be sure to review the
sample {github-samples-url}/caching/multi-site\[code\].

</div>

</div>

</div>

<div class="sect2">

### Advanced Caching Configuration

<div class="paragraph">

{pivotal-gemfire-name} supports additional caching capabilities to
manage the entries stored in the cache.

</div>

<div class="paragraph">

As you can imagine, given that cache entries are stored in-memory, it
becomes important to manage and monitor the available memory used by the
cache. After all, by default, {pivotal-gemfire-name} stores data in the
JVM Heap.

</div>

<div class="paragraph">

You can employ several techniques to more effectively manage memory,
such as using
{apache-geode-docs}/developing/eviction/chapter_overview.html\[eviction\],
possibly
{apache-geode-docs}/developing/storing_data_on_disk/chapter_overview.html\[overflowing
data to disk\], configuring both entry Idle-Timeout\_ (TTI) and
Time-to-Live\_ (TTL)
{apache-geode-docs}/developing/expiration/chapter_overview.html\[expiration
policies\], configuring
{apache-geode-docs}/managing/region_compression.html\[compression\], and
using
{apache-geode-docs}/managing/heap_use/off_heap_management.html\[off-heap\]
or main memory.

</div>

<div class="paragraph">

You can use several other strategies as well, as described in
{apache-geode-docs}/managing/heap_use/heap_management.html\[Managing
Heap and Off-heap Memory\].

</div>

<div class="paragraph">

While this is beyond the scope of this document, know that Spring Data
for {pivotal-gemfire-name} makes all of these
{spring-data-geode-docs-html}/#bootstrap-annotation-config-regions\[configuration
options\] available to you.

</div>

</div>

<div class="sect2">

### Disable Caching

<div class="paragraph">

There may be cases where you do not want your Spring Boot application to
cache application state with
{spring-framework-docs}/integration.html#cache\[Spring’s Cache
Abstraction\] using {pivotal-gemfire-name}. In certain cases, you may
use another Spring supported caching provider, such as Redis, to cache
and manage your application state. In other cases, you may not want to
use Spring’s Cache Abstraction at all.

</div>

<div class="paragraph">

Either way, you can specifically call out your Spring Cache Abstraction
provider by using the `spring.cache.type` property in
`application.properties`:

</div>

<div class="exampleblock">

<div class="title">

Example 18. Use Redis as the Spring Cache Abstraction Provider

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
#application.properties

spring.cache.type=redis
...
```

</div>

</div>

</div>

</div>

<div class="paragraph">

If you prefer not to use Spring’s Cache Abstraction to manage your
Spring Boot application’s state at all, then set the `spring.cache.type`
property to "none":

</div>

<div class="exampleblock">

<div class="title">

Example 19. Disable Spring’s Cache Abstraction

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
#application.properties

spring.cache.type=none
...
```

</div>

</div>

</div>

</div>

<div class="paragraph">

See the Spring Boot
{spring-boot-docs-html}/boot-features-caching.html#boot-features-caching-provider-none\[documentation\]
for more detail.

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
<td class="content">You can include multiple caching providers on the
classpath of your Spring Boot application. For instance, you might use
Redis to cache your application’s state while using
{pivotal-gemfire-name} as your application’s persistent data store (that
is, the System of Record (SOR)).</td>
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
<code>spring.cache.type=[gemfire|geode]</code>, even though Spring Boot
for {pivotal-gemfire-name} is set up to handle either of these property
values (that is, either <code>gemfire</code> or
<code>geode</code>).</td>
</tr>
</tbody>
</table>

</div>

</div>

</div>

</div>

</div>

<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:12:53 -0700

</div>

</div>
