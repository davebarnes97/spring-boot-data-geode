<div id="header">

</div>

<div id="content">

<div class="sect1">

## Spring Data Repositories

<div class="sectionbody">

<div class="paragraph">

Using Spring Data Repositories with {pivotal-gemfire-name} makes short
work of data access operations when you use {pivotal-gemfire-name} as
your System of Record (SoR) to persist your application’s state.

</div>

<div class="paragraph">

{spring-data-commons-docs-html}/#repositories\[Spring Data
Repositories\] provide a convenient and powerful way to define basic
CRUD and simple query data access operations by specifying the contract
of those data access operations in a Java interface.

</div>

<div class="paragraph">

Spring Boot for {pivotal-gemfire-name} auto-configures the Spring Data
for {pivotal-gemfire-name}
{spring-data-geode-docs-html}/#gemfire-repositories\[Repository
extension\] when either is declared on your application’s classpath. You
need not do anything special to enable it. You can start coding your
application-specific Repository interfaces.

</div>

<div class="paragraph">

The following example defines a `Customer` class to model customers and
map it to the {pivotal-gemfire-name} `Customers` Region by using the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/mapping/annotation/Region.html\[`@Region`\]
mapping annotation:

</div>

<div class="exampleblock">

<div class="title">

Example 1. `Customer` entity class

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
package example.app.crm.model;

@Region("Customers")
class Customer {

    @Id
    private Long id;

    private String name;

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The following example shows how to declare your Repository (a.k.a.
{wikipedia-docs}/Data_access_object\[Data Access Object (DAO)\]) for
`Customers`:

</div>

<div class="exampleblock">

<div class="title">

Example 2. `CustomerRepository` for peristing and accessing `Customers`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
package example.app.crm.repo;

interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findByLastNameLikeOrderByLastNameDescFirstNameAsc(String customerLastNameWildcard);

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Then you can use the `CustomerRepository` in an application service
class:

</div>

<div class="exampleblock">

<div class="title">

Example 3. Inject and use the `CustomerRepository`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
package example.app;

@SpringBootApplication
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
class SpringBootApacheGeodeClientCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApacheGeodeClientCacheApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(CustomerRepository customerRepository) {

        // Matches Williams, Wilson, etc.
        List<Customer> customers =
            customerRepository.findByLastNameLikeOrderByLastNameDescFirstNameAsc("Wil%");

        // process the list of matching customers...
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

See Spring Data Commons'
{spring-data-commons-docs-html}/#repositories\[Repositories
abstraction\] and Spring Data for {pivotal-gemfire-name}'s
{spring-data-geode-docs-html}/#gemfire-repositories\[Repositories
extension\] for more detail.

</div>

</div>

</div>

</div>

<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:13:15 -0700

</div>

</div>
