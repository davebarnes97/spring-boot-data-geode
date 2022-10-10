<div id="header">

</div>

<div id="content">

<div class="sect1">

## Testing

<div class="sectionbody">

<div class="paragraph">

Spring Boot for {pivotal-gemfire-name} (SBDG), with help from [Spring
Test for {pivotal-gemfire-name}
(STDG)](https://github.com/spring-projects/spring-test-data-geode),
offers first-class support for both unit and integration testing with
{pivotal-gemfire-name} in your Spring Boot applications.

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
<td class="content">See the Spring Test for Apache Geode (STDG) <a
href="https://github.com/spring-projects/spring-test-data-geode/#stdg-in-a-nutshell">documentation</a>
for more details.</td>
</tr>
</tbody>
</table>

</div>

<div class="sect2">

### Unit Testing

<div class="paragraph">

Unit testing with {pivotal-gemfire-name} using mock objects in a Spring
Boot Test requires only that you declare the STDG
`@EnableGemFireMockObjects` annotation in your test configuration:

</div>

<div class="exampleblock">

<div class="title">

Example 1. Unit Test with {pivotal-gemfire-name} using Spring Boot

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringBootApacheGeodeUnitTest extends IntegrationTestsSupport {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveAndFindUserIsSuccessful() {

        User jonDoe = User.as("jonDoe");

        assertThat(this.userRepository.save(jonDoe)).isNotNull();

        User jonDoeFoundById = this.userRepository.findById(jonDoe.getName()).orElse(null);

        assertThat(jonDoeFoundById).isEqualTo(jonDoe);
    }

    @SpringBootApplication
    @EnableGemFireMockObjects
    @EnableEntityDefinedRegions(basePackageClasses = User.class)
    static class TestConfiguration { }

}

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "as")
@Region("Users")
class User {

    @Id
    @lombok.NonNull
    private String name;

}

interface UserRepository extends CrudRepository<User, String> { }
```

</div>

</div>

</div>

</div>

<div class="paragraph">

This test class is not a “pure” unit test, particularly since it
bootstraps an actual Spring `ApplicationContext` using Spring Boot.
However, it does mock all {pivotal-gemfire-name} objects, such as the
`Users` `Region` declared by the `User` application entity class, which
was annotated with SDG’s `@Region` mapping annotation.

</div>

<div class="paragraph">

This test class conveniently uses Spring Boot’s auto-configuration to
auto-configure an {pivotal-gemfire-name} `ClientCache` instance. In
addition, SDG’s `@EnableEntityDefinedRegions` annotation was used to
conveniently create the {pivotal-gemfire-name} "Users\` `Region` to
store instances of `User`.

</div>

<div class="paragraph">

Finally, Spring Data’s Repository abstraction was used to conveniently
perform basic CRUD (such as `save`) and simple (OQL) query (such as
`findById`) data access operations on the `Users` `Region`.

</div>

<div class="paragraph">

Even though the {pivotal-gemfire-name} objects (such as the `Users`
`Region`) are “mock objects”, you can still perform many of the data
access operations required by your Spring Boot application’s components
in an {pivotal-gemfire-name} API-agnostic way — that is, by using
Spring’s powerful programming model and constructs.

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
<td class="content">By extending STDG’s
<code>org.springframework.data.gemfire.tests.integration.IntegrationTestSupport</code>
class, you ensure that all {pivotal-gemfire-name} mock objects and
resources are properly released after the test class runs, thereby
preventing any interference with downstream tests.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

While STDG tries to [mock the functionality and
behavior](https://github.com/spring-projects/spring-test-data-geode/#mock-regions-with-data)
for many `Region` operations, it is not pragmatic to mock them all. For
example, it would not be practical to mock `Region` query operations
involving complex OQL statements that have sophisticated predicates.

</div>

<div class="paragraph">

If such functional testing is required, the test might be better suited
as an integration test. Alternatively, you can follow the advice in this
section about [unsupported Region
operations](https://github.com/spring-projects/spring-test-data-geode/#mocking-unsupported-region-operations).

</div>

<div class="paragraph">

In general, STDG provides the following capabilities when mocking
{pivotal-gemfire-name} objects:

</div>

<div class="ulist">

- [Mock Object Scope & Lifecycle
  Management](https://github.com/spring-projects/spring-test-data-geode#mock-object-scope—​lifecycle-management)

- [Support for Mock Regions with
  Data](https://github.com/spring-projects/spring-test-data-geode#mock-regions-with-data)

- [Support for Mocking Region
  Callbacks](https://github.com/spring-projects/spring-test-data-geode#mock-region-callbacks)

- [Support for Mocking Unsupported Region
  Operations](https://github.com/spring-projects/spring-test-data-geode#mocking-unsupported-region-operations)

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
<td class="content">See the documentation on <a
href="https://github.com/spring-projects/spring-test-data-geode/#unit-testing-with-stdg">Unit
Testing with STDG</a> for more details.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect2">

### Integration Testing

<div class="paragraph">

Integration testing with {pivotal-gemfire-name} in a Spring Boot Test is
as simple as **not** declaring STDG’s `@EnableGemFireMockObjects`
annotation in your test configuration. You may then want to use SBDG’s
`@EnableClusterAware` annotation to conditionally detect the presence of
a {pivotal-gemfire-name} cluster:

</div>

<div class="exampleblock">

<div class="title">

Example 2. Using `@EnableClusterAware` in test configuration

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@SpringBootApplication
@EnableClusterAware
@EnableEntityDefinedRegions(basePackageClasses = User.class)
static class TestConfiguration { }
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The SBDG `@EnableClusterAware` annotation conveniently toggles your
auto-configured `ClientCache` instance between local-only mode and
client/server. It even pushes configuration metadata (such as `Region`
definitions) up to the servers in the cluster that are required by the
application to store data.

</div>

<div class="paragraph">

In most cases, in addition to testing with “live” {pivotal-gemfire-name}
objects (such as Regions), we also want to test in a client/server
capacity. This unlocks the full capabilities of the
{pivotal-gemfire-name} data management system in a Spring context and
gets you as close as possible to production from the comfort of your
IDE.

</div>

<div class="paragraph">

Building on our example from the section on [Unit
Testing](#geode-testing-unit), you can modify the test to use “live”
{pivotal-gemfire-name} objects in a client/server topology as follows:

</div>

<div class="exampleblock">

<div class="title">

Example 3. Integration Test with {pivotal-gemfire-name} using Spring
Boot

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@ActiveProfiles("client")
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.data.gemfire.management.use-http=false")
public class SpringBootApacheGeodeIntegrationTest extends ForkingClientServerIntegrationTestsSupport {

    @BeforeClass
    public static void startGeodeServer() throws IOException {
        startGemFireServer(TestGeodeServerConfiguration.class);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveAndFindUserIsSuccessful() {

        User jonDoe = User.as("jonDoe");

        assertThat(this.userRepository.save(jonDoe)).isNotNull();

        User jonDoeFoundById = this.userRepository.findById(jonDoe.getName()).orElse(null);

        assertThat(jonDoeFoundById).isEqualTo(jonDoe);
        assertThat(jonDoeFoundById).isNotSameAs(jonDoe);
    }

    @SpringBootApplication
    @EnableClusterAware
    @EnableEntityDefinedRegions(basePackageClasses = User.class)
    @Profile("client")
    static class TestGeodeClientConfiguration { }

    @CacheServerApplication
    @Profile("server")
    static class TestGeodeServerConfiguration {

        public static void main(String[] args) {

            new SpringApplicationBuilder(TestGeodeServerConfiguration.class)
                .web(WebApplicationType.NONE)
                .profiles("server")
                .build()
                .run(args);
        }
    }
}

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "as")
@Region("Users")
class User {

    @Id
    @lombok.NonNull
    private String name;

}

interface UserRepository extends CrudRepository<User, String> { }
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The application client/server-based integration test class extend STDG’s
`org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport`
class. This ensures that all {pivotal-gemfire-name} objects and
resources are properly cleaned up after the test class runs. In
addition, it coordinates the client and server components of the test
(for example connecting the client to the server using a random port).

</div>

<div class="paragraph">

The {pivotal-gemfire-name} server is started in a `@BeforeClass` setup
method:

</div>

<div class="listingblock">

<div class="title">

Start the {pivotal-gemfire-name} server

</div>

<div class="content">

``` highlight
class SpringBootApacheGeodeIntegrationTest extends ForkingClientServerIntegrationTestsSupport {

  @BeforeClass
  public static void startGeodeServer() throws IOException {
    startGemFireServer(TestGeodeServerConfiguration.class);
  }
}
```

</div>

</div>

<div class="paragraph">

STDG lets you configure the {pivotal-gemfire-name} server with Spring
configuration, specified in the `TestGeodeServerConfiguration` class.
The Java class needs to provide a `main` method. It uses the
`SpringApplicationBuilder` to bootstrap the {pivotal-gemfire-name}
`CacheServer` application:

</div>

<div class="exampleblock">

<div class="title">

Example 4. {pivotal-gemfire-name} server configuration

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@CacheServerApplication
@Profile("server")
static class TestGeodeServerConfiguration {

  public static void main(String[] args) {

    new SpringApplicationBuilder(TestGeodeServerConfiguration.class)
      .web(WebApplicationType.NONE)
      .profiles("server")
      .build()
      .run(args);
  }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

In this case, we provide minimal configuration, since the configuration
is determined and pushed up to the server by the client. For example, we
do not need to explicitly create the `Users` `Region` on the server
since it is implicitly handled for you by the SBDG/STDG frameworks from
the client.

</div>

<div class="paragraph">

We take advantage of Spring profiles in the test setup to distinguish
between the client and server configuration. Keep in mind that the test
is the “client” in this arrangement.

</div>

<div class="paragraph">

The STDG framework does what the supporting class demands: “forking” the
Spring Boot-based, {pivotal-gemfire-name} `CacheServer` application in a
separate JVM process. Subsequently, the STDG framework stops the server
upon completion of the tests in the test class.

</div>

<div class="paragraph">

You are free to start your servers or cluster however you choose. STDG
provides this capability as a convenience for you, since it is a common
concern.

</div>

<div class="paragraph">

This test class is simple. STDG can handle much more complex test
scenarios.

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
<td class="content">Review SBDG’s test suite to witness the full power
and functionality of the STDG framework for yourself.</td>
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
<td class="content">See the documentation on <a
href="https://github.com/spring-projects/spring-test-data-geode/#integration-testing-with-stdg">Integration
Testing with STDG</a> for more details.</td>
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

Last updated 2022-10-10 12:14:35 -0700

</div>

</div>
