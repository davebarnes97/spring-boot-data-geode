<div id="header">

</div>

<div id="content">

<div class="sect1">

## Data Access with GemfireTemplate

<div class="sectionbody">

<div class="paragraph">

There are several ways to access data stored in {pivotal-gemfire-name}.

</div>

<div class="paragraph">

For instance, you can use the
{apache-geode-javadoc}/org/apache/geode/cache/Region.html\[Region API\]
directly. If you are driven by the application’s domain context, you can
use the power of {spring-data-commons-docs-html}/#repositories\[Spring
Data Repositories\] instead.

</div>

<div class="paragraph">

While the Region API offers flexibility, it couples your application to
{pivotal-gemfire-name}, which is usually undesirable and unnecessary.
While using Spring Data Repositories provides a very powerful and
convenient abstraction, you give up the flexibility provided by a
lower-level Region API.

</div>

<div class="paragraph">

A good compromise is to use the [Template software design
pattern](https://en.wikipedia.org/wiki/Template_method_pattern). This
pattern is consistently and widely used throughout the entire Spring
portfolio.

</div>

<div class="paragraph">

For example, the Spring Framework provides
{spring-framework-javadoc}/org/springframework/jdbc/core/JdbcTemplate.html\[`JdbcTemplate`\]
and
{spring-framework-javadoc}/org/springframework/jms/core/JmsTemplate.html\[`JmsTemplate`\].

</div>

<div class="paragraph">

Other Spring Data modules, such as Spring Data Redis, offer the
[`RedisTemplate`](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/RedisTemplate.html),
and Spring Data for {pivotal-gemfire-name} (SDG) itself offers the
{spring-data-gemfire-javadoc}/org/springframework/data/gemfire/GemfireTemplate.html\[`GemfireTemplate`\].

</div>

<div class="paragraph">

The `GemfireTemplate` provides a highly consistent and familiar API to
perform data access operations on {pivotal-gemfire-name} cache
`Regions`.

</div>

<div class="paragraph">

`GemfireTemplate` offers:

</div>

<div class="ulist">

- A simple and convenient data access API to perform basic CRUD and
  simple query operations on cache Regions.

- Use of Spring Framework’s consistent data access
  {spring-framework-docs}/data-access.html#dao-exceptions\[Exception
  hierarchy\].

- Automatic enlistment in the presence of local cache transactions.

- Consistency and protection from
  {apache-geode-javadoc}/org/apache/geode/cache/Region.html\[Region
  API\] breaking changes.

</div>

<div class="paragraph">

Given these advantages, Spring Boot for {pivotal-gemfire-name} (SBDG)
auto-configures `GemfireTemplate` beans for each Region present in the
{pivotal-gemfire-name} cache.

</div>

<div class="paragraph">

Additionally, SBDG is careful not to create a `GemfireTemplate` if you
have already declared a `GemfireTemplate` bean in the Spring
`ApplicationContext` for a given Region.

</div>

<div class="sect2">

### Explicitly Declared Regions

<div class="paragraph">

Consider an explicitly declared Region bean definition:

</div>

<div class="olist arabic">

1.  Explicitly Declared Region Bean Definition

</div>

<div class="exampleblock">

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Configuration
class GeodeConfiguration {

    @Bean("Example")
    ClientRegionFactoryBean<?, ?> exampleRegion(GemFireCache gemfireCache) {
        // ...
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

SBDG automatically creates a `GemfireTemplate` bean for the `Example`
Region by using the bean name `exampleTemplate`. SBDG names the
`GemfireTemplate` bean after the Region by converting the first letter
in the Region’s name to lower case and appending `Template` to the bean
name.

</div>

<div class="paragraph">

In a managed Data Access Object (DAO), you can inject the Template:

</div>

<div class="exampleblock">

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Repository
class ExampleDataAccessObject {

    @Autowired
    @Qualifier("exampleTemplate")
    private GemfireTemplate exampleTemplate;

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You should use the `@Qualifier` annotation to qualify which
`GemfireTemplate` bean you are specifically referring, especially if you
have more than one Region bean definition.

</div>

</div>

<div class="sect2">

### Entity-defined Regions

<div class="paragraph">

SBDG auto-configures `GemfireTemplate` beans for entity-defined Regions.

</div>

<div class="paragraph">

Consider the following entity class:

</div>

<div class="exampleblock">

<div class="title">

Example 1. Customer class

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Region("Customers")
class Customer {
    // ...
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Further consider the following configuration:

</div>

<div class="exampleblock">

<div class="title">

Example 2. Apache Geode Configuration

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Configuration
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
class GeodeConfiguration {
    // ...
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

SBDG auto-configures a `GemfireTemplate` bean for the `Customers` Region
named `customersTemplate`, which you can then inject into an application
component:

</div>

<div class="exampleblock">

<div class="title">

Example 3. CustomerService application component

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Service
class CustomerService {

    @Bean
    @Qualifier("customersTemplate")
    private GemfireTemplate customersTemplate;

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Again, be careful to qualify the `GemfireTemplate` bean injection if you
have multiple Regions, whether declared explicitly or implicitly, such
as when you use the `@EnableEntityDefineRegions` annotation.

</div>

</div>

<div class="sect2">

### Caching-defined Regions

<div class="paragraph">

SBDG auto-configures `GemfireTemplate` beans for caching-defined
Regions.

</div>

<div class="paragraph">

When you use Spring Framework’s
{spring-framework-docs}/integration.html#cache\[Cache Abstraction\]
backed by {pivotal-gemfire-name}, one requirement is to configure
Regions for each of the caches specified in the
{spring-framework-docs}integration.html#cache-annotations\[caching
annotations\] of your application service components.

</div>

<div class="paragraph">

Fortunately, SBDG makes enabling and configuring caching easy and
[automatic](#geode-caching-provider).

</div>

<div class="paragraph">

Consider the following cacheable application service component:

</div>

<div class="exampleblock">

<div class="title">

Example 4. Cacheable `CustomerService` class

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Service
class CacheableCustomerService {

    @Bean
    @Qualifier("customersByNameTemplate")
    private GemfireTemplate customersByNameTemplate;

    @Cacheable("CustomersByName")
    public Customer findBy(String name) {
        return toCustomer(customersByNameTemplate.query("name = " + name));
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Further consider the following configuration:

</div>

<div class="exampleblock">

<div class="title">

Example 5. Apache Geode Configuration

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Configuration
@EnableCachingDefinedRegions
class GeodeConfiguration {

    @Bean
    public CustomerService customerService() {
        return new CustomerService();
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

SBDG auto-configures a `GemfireTemplate` bean named
`customersByNameTemplate` to perform data access operations on the
`CustomersByName` (`@Cacheable`) Region. You can then inject the bean
into any managed application component, as shown in the preceding
application service component example.

</div>

<div class="paragraph">

Again, be careful to qualify the `GemfireTemplate` bean injection if you
have multiple Regions, whether declared explicitly or implicitly, such
as when you use the `@EnableCachingDefineRegions` annotation.

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
<td class="content">Autowiring (that is, injecting)
<code>GemfireTemplate</code> beans auto-configured by SBDG for
caching-defined Regions into your application components does not always
work. This has to do with the Spring container bean creation process. In
those cases, you may need to lazily lookup the
<code>GemfireTemplate</code> by using
<code>applicationContext.getBean("customersByNameTemplate", GemfireTemplate.class)</code>.
This is not ideal, but it works when autowiring does not.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect2">

### Native-defined Regions

<div class="paragraph">

SBDG even auto-configures `GemfireTemplate` beans for Regions that have
been defined with {pivotal-gemfire-name} native configuration metadata,
such as `cache.xml`.

</div>

<div class="paragraph">

Consider the following {pivotal-gemfire-name} native `cache.xml`:

</div>

<div class="exampleblock">

<div class="title">

Example 6. Client `cache.xml`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
<?xml version="1.0" encoding="UTF-8"?>
<client-cache xmlns="http://geode.apache.org/schema/cache"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://geode.apache.org/schema/cache http://geode.apache.org/schema/cache/cache-1.0.xsd"
              version="1.0">

    <region name="Example" refid="LOCAL"/>

</client-cache>
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Further consider the following Spring configuration:

</div>

<div class="exampleblock">

<div class="title">

Example 7. Apache Geode Configuration

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Configuration
@EnableGemFireProperties(cacheXmlFile = "cache.xml")
class GeodeConfiguration {
    // ...
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

SBDG auto-configures a `GemfireTemplate` bean named `exampleTemplate`
after the `Example` Region defined in `cache.xml`. You can inject this
template as you would any other Spring-managed bean:

</div>

<div class="exampleblock">

<div class="title">

Example 8. Injecting the `GemfireTemplate`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Service
class ExampleService {

    @Autowired
    @Qualifier("exampleTemplate")
    private GemfireTemplate exampleTemplate;

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The rules described earlier apply when multiple Regions are present.

</div>

</div>

<div class="sect2">

### Template Creation Rules

<div class="paragraph">

Fortunately, SBDG is careful not to create a `GemfireTemplate` bean for
a Region if a template by the same name already exists.

</div>

<div class="paragraph">

For example, consider the following configuration:

</div>

<div class="exampleblock">

<div class="title">

Example 9. Apache Geode Configuration

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Configuration
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
class GeodeConfiguration {

    @Bean
    public GemfireTemplate customersTemplate(GemFireCache cache) {
        return new GemfireTemplate(cache.getRegion("/Customers"));
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Further consider the following example:

</div>

<div class="exampleblock">

<div class="title">

Example 10. Customer class

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Region("Customers")
class Customer {
    // ...
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Because you explicitly defined and declared the `customersTemplate`
bean, SBDG does not automatically create a template for the `Customers`
Region. This applies regardless of how the Region was created, whether
by using `@EnableEntityDefinedRegions`, `@EnableCachingDefinedRegions`,
explicitly declaring Regions, or natively defining Regions.

</div>

<div class="paragraph">

Even if you name the template differently from the Region for which the
template was configured, SBDG conserves resources and does not create
the template.

</div>

<div class="paragraph">

For example, suppose you named the `GemfireTemplate` bean
`vipCustomersTemplate`, even though the Region name is `Customers`,
based on the `@Region` annotated `Customer` class, which specified the
`Customers` Region.

</div>

<div class="paragraph">

With the following configuration, SBDG is still careful not to create
the template:

</div>

<div class="exampleblock">

<div class="title">

Example 11. Apache Geode Configuration

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Configuration
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
class GeodeConfiguration {

    @Bean
    public GemfireTemplate vipCustomersTemplate(GemFireCache cache) {
        return new GemfireTemplate(cache.getRegion("/Customers"));
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

SBDG identifies that your `vipCustomersTemplate` is the template used
with the `Customers` Region, and SBDG does not create the
`customersTemplate` bean, which would result in two `GemfireTemplate`
beans for the same Region.

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
<td class="content">The name of your Spring bean defined in Java
configuration is the name of the method if the Spring bean is not
explicitly named by using the <code>name</code> attribute or the
<code>value</code> attribute of the <code>@Bean</code> annotation.</td>
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

Last updated 2022-10-10 12:13:06 -0700

</div>

</div>
