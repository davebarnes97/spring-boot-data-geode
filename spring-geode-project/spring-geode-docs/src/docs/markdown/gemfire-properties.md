<div id="header">

</div>

<div id="content">

<div class="sect1">

## Using Geode Properties

<div class="sectionbody">

<div class="paragraph">

As of Spring Boot for {pivotal-gemfire-name} (SBDG) 1.3, you can declare
{pivotal-gemfire-name} properties from `gemfire.properties` in Spring
Boot `application.properties`.

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
{apache-geode-docs}/reference/topics/gemfire_properties.html[User Guide]
for a complete list of valid {pivotal-gemfire-name} properties.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Note that you can declare only valid Geode properties in
`gemfire.properties` or, alternatively, `gfsecurity.properties`.

</div>

<div class="paragraph">

The following example shows how to declare properties in
`gemfire.properties`:

</div>

<div class="exampleblock">

<div class="title">

Example 1. Valid `gemfire.properties`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Geode Properties in gemfire.properties

name=ExampleCacheName
log-level=TRACE
enable-time-statistics=true
durable-client-id=123
# ...
```

</div>

</div>

</div>

</div>

<div class="paragraph">

All of the properties declared in the preceding example correspond to
valid Geode properties. It is illegal to declare properties in
`gemfire.properties` that are not valid Geode properties, even if those
properties are prefixed with a different qualifier (such as `spring.*`).
{pivotal-gemfire-name} throws an `IllegalArgumentException` for invalid
properties.

</div>

<div class="paragraph">

Consider the following `gemfire.properties` file with an
`invalid-property`:

</div>

<div class="exampleblock">

<div class="title">

Example 2. Invalid `gemfire.properties`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Geode Properties in gemfire.properties

name=ExampleCacheName
invalid-property=TEST
```

</div>

</div>

</div>

</div>

<div class="paragraph">

{pivotal-gemfire-name} throws an `IllegalArgumentException`:

</div>

<div class="exampleblock">

<div class="title">

Example 3. `IllegalArgumentException` thrown by {pivotal-gemfire-name}
for Invalid Property (Full Text Omitted)

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
Exception in thread "main" java.lang.IllegalArgumentException: Unknown configuration attribute name invalid-property.
Valid attribute names are: ack-severe-alert-threshold ack-wait-threshold archive-disk-space-limit ...
    at o.a.g.internal.AbstractConfig.checkAttributeName(AbstractConfig.java:333)
    at o.a.g.distributed.internal.AbstractDistributionConfig.checkAttributeName(AbstractDistributionConfig.java:725)
    at o.a.g.distributed.internal.AbstractDistributionConfig.getAttributeType(AbstractDistributionConfig.java:887)
    at o.a.g.internal.AbstractConfig.setAttribute(AbstractConfig.java:222)
    at o.a.g.distributed.internal.DistributionConfigImpl.initialize(DistributionConfigImpl.java:1632)
    at o.a.g.distributed.internal.DistributionConfigImpl.<init>(DistributionConfigImpl.java:994)
    at o.a.g.distributed.internal.DistributionConfigImpl.<init>(DistributionConfigImpl.java:903)
    at o.a.g.distributed.internal.ConnectionConfigImpl.lambda$new$2(ConnectionConfigImpl.java:37)
    at o.a.g.distributed.internal.ConnectionConfigImpl.convert(ConnectionConfigImpl.java:73)
    at o.a.g.distributed.internal.ConnectionConfigImpl.<init>(ConnectionConfigImpl.java:36)
    at o.a.g.distributed.internal.InternalDistributedSystem$Builder.build(InternalDistributedSystem.java:3004)
    at o.a.g.distributed.internal.InternalDistributedSystem.connectInternal(InternalDistributedSystem.java:269)
    at o.a.g.cache.client.ClientCacheFactory.connectInternalDistributedSystem(ClientCacheFactory.java:280)
    at o.a.g.cache.client.ClientCacheFactory.basicCreate(ClientCacheFactory.java:250)
    at o.a.g.cache.client.ClientCacheFactory.create(ClientCacheFactory.java:216)
    at org.example.app.ApacheGeodeClientCacheApplication.main(...)
```

</div>

</div>

</div>

</div>

<div class="paragraph">

It is inconvenient to have to separate {pivotal-gemfire-name} properties
from other application properties, or to have to declare only
{pivotal-gemfire-name} properties in a `gemfire.properties` file and
application properties in a separate properties file, such as Spring
Boot `application.properties`.

</div>

<div class="paragraph">

Additionally, because of {pivotal-gemfire-name}'s constraint on
properties, you cannot use the full power of Spring Boot when you
compose `application.properties`.

</div>

<div class="paragraph">

You can include certain properties based on a Spring profile while
excluding other properties. This is essential when properties are
environment- or context-specific.

</div>

<div class="paragraph">

Spring Data for {pivotal-gemfire-name} already provides a wide range of
properties mapping to {pivotal-gemfire-name} properties.

</div>

<div class="paragraph">

For example, the SDG `spring.data.gemfire.locators` property maps to the
`gemfire.locators` property (`locators` in `gemfire.properties`) from
{pivotal-gemfire-name}. Likewise, there are a full set of SDG properties
that map to the corresponding {pivotal-gemfire-name} properties in the
[Appendix](#geode-configuration-metadata-springdata).

</div>

<div class="paragraph">

You can express the Geode properties shown earlier as SDG properties in
Spring Boot `application.properties`, as follows:

</div>

<div class="exampleblock">

<div class="title">

Example 4. Configuring Geode Properties using SDG Properties

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Spring Data for {geode-name} properties in application.properties

spring.data.gemfire.name=ExampleCacheName
spring.data.gemfire.cache.log-level=TRACE
spring.data.gemfire.cache.client.durable-client-id=123
spring.data.gemfire.stats.enable-time-statistics=true
# ...
```

</div>

</div>

</div>

</div>

<div class="paragraph">

However, some {pivotal-gemfire-name} properties have no equivalent SDG
property, such as `gemfire.groups` (`groups` in `gemfire.properties`).
This is partly due to the fact that many {pivotal-gemfire-name}
properties are applicable only when configured on the server (such as
`groups` or `enforce-unique-host`).

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
<td class="content">See the <code>@EnableGemFireProperties</code>
annotation
({spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/EnableGemFireProperties.html[attributes])
from SDG for a complete list of {pivotal-gemfire-name} properties with
no corresponding SDG property.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Furthermore, many of the SDG properties also correspond to API calls.

</div>

<div class="paragraph">

For example,
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#keepAlive\[`spring.data.gemfire.cache.client.keep-alive`\]
translates to the
{apache-geode-javadoc}/org/apache/geode/cache/client/ClientCache.html#close-boolean\[`ClientCache.close(boolean keepAlive)`\]
API call.

</div>

<div class="paragraph">

Still, it would be convenient to be able to declare application and
{pivotal-gemfire-name} properties together, in a single properties file,
such as Spring Boot `application.properties`. After all, it is not
uncommon to declare JDBC Connection properties in a Spring Boot
`application.properties` file.

</div>

<div class="paragraph">

Therefore, as of SBDG 1.3, you can now declare {pivotal-gemfire-name}
properties in Spring Boot `application.properties` directly, as follows:

</div>

<div class="exampleblock">

<div class="title">

Example 5. Geode Properties declared in Spring Boot
`application.properties`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Spring Boot application.properties

server.port=8181
spring.application.name=ExampleApp
gemfire.durable-client-id=123
gemfire.enable-time-statistics=true
```

</div>

</div>

</div>

</div>

<div class="paragraph">

This is convenient and ideal for several reasons:

</div>

<div class="ulist">

- If you already have a large number of {pivotal-gemfire-name}
  properties declared as `gemfire.` properties (either in
  `gemfire.properties` or `gfsecurity.properties`) or declared on the
  Java command-line as JVM System properties (such as
  `-Dgemfire.name=ExampleCacheName`), you can reuse these property
  declarations.

- If you are unfamiliar with SDG’s corresponding properties, you can
  declare Geode properties instead.

- You can take advantage of Spring features, such as Spring profiles.

- You can also use property placeholders with Geode properties (such as
  `gemfire.log-level=${external.log-level.property}`).

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
<td class="content">We encourage you to use the SDG properties, which
cover more than {pivotal-gemfire-name} properties.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

However, SBDG requires that the Geode property must have the `gemfire.`
prefix in Spring Boot `application.properties`. This indicates that the
property belongs to {pivotal-gemfire-name}. Without the `gemfire.`
prefix, the property is not appropriately applied to the
{pivotal-gemfire-name} cache instance.

</div>

<div class="paragraph">

It would be ambiguous if your Spring Boot applications integrated with
several technologies, including {pivotal-gemfire-name}, and they too had
matching properties, such as `bind-address` or `log-file`.

</div>

<div class="paragraph">

SBDG makes a best attempt to log warnings when a Geode property is
invalid or is not set. For example, the following Geode property would
result in logging a warning:

</div>

<div class="exampleblock">

<div class="title">

Example 6. Invalid {pivotal-gemfire-name} Property

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Spring Boot application.properties

spring.application.name=ExampleApp
gemfire.non-existing-property=TEST
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The resulting warning in the log would read:

</div>

<div class="exampleblock">

<div class="title">

Example 7. Invalid Geode Property Warning Message

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
[gemfire.non-existing-property] is not a valid Apache Geode property
```

</div>

</div>

</div>

</div>

<div class="paragraph">

If a Geode Property is not properly set, the following warning is
logged:

</div>

<div class="exampleblock">

<div class="title">

Example 8. Invalide Geode Property Value Warning Message

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
Apache Geode Property [gemfire.security-manager] was not set
```

</div>

</div>

</div>

</div>

<div class="paragraph">

With regards to the third point mentioned earlier, you can now compose
and declare Geode properties based on a context (such as your
application environment) using Spring profiles.

</div>

<div class="paragraph">

For example, you might start with a base set of properties in Spring
Boot `application.properties`:

</div>

<div class="exampleblock">

<div class="title">

Example 9. Base Properties

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
server.port=8181
spring.application.name=ExampleApp
gemfire.durable-client-id=123
gemfire.enable-time-statistics=false
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Then you can vary the properties by environment, as the next two
listings (for QA and production) show:

</div>

<div class="exampleblock">

<div class="title">

Example 10. QA Properties

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Spring Boot application-qa.properties

server.port=9191
spring.application.name=TestApp
gemfire.enable-time-statistics=true
gemfire.enable-network-partition-detection=true
gemfire.groups=QA
# ...
```

</div>

</div>

</div>

</div>

<div class="exampleblock">

<div class="title">

Example 11. Production Properties

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Spring Boot application-prod.properties

server.port=80
spring.application.name=ProductionApp
gemfire.archive-disk-space-limit=1000
gemfire.archive-file-size-limit=50
gemfire.enforce-unique-host=true
gemfire.groups=PROD
# ...
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You can then apply the appropriate set of properties by configuring the
Spring profile with `-Dspring.profiles.active=prod`. You can also enable
more than one profile at a time with
`-Dspring.profiles.active=profile1,profile2,…​,profileN`

</div>

<div class="paragraph">

If both `spring.data.gemfire.*` properties and the matching
{pivotal-gemfire-name} properties are declared in Spring Boot
`application.properties`, the SDG properties take precedence.

</div>

<div class="paragraph">

If a property is specified more than once, as would potentially be the
case when composing multiple Spring Boot `application.properties` files
and you enable more than one Spring profile at time, the last property
declaration wins. In the example shown earlier, the value for
`gemfire.groups` would be `PROD` when `-Dspring.profiles.active=qa,prod`
is configured.

</div>

<div class="paragraph">

Consider the following Spring Boot `application.properties`:

</div>

<div class="exampleblock">

<div class="title">

Example 12. Property Precedence

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Spring Boot application.properties

gemfire.durable-client-id=123
spring.data.gemfire.cache.client.durable-client-id=987
```

</div>

</div>

</div>

</div>

<div class="paragraph">

The `durable-client-id` is `987`. It does not matter which order the SDG
or {pivotal-gemfire-name} properties are declared in Spring Boot
`application.properties`. The matching SDG property overrides the
{pivotal-gemfire-name} property when duplicates are found.

</div>

<div class="paragraph">

Finally, you cannot refer to Geode properties declared in Spring Boot
`application.properties` with the SBDG `GemFireProperties` class (see
the
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/configuration/GemFireProperties.html\[Javadoc\]).

</div>

<div class="paragraph">

Consider the following example:

</div>

<div class="exampleblock">

<div class="title">

Example 13. Geode Properties declared in Spring Boot
`application.properties`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
# Spring Boot application.properties

gemfire.name=TestCacheName
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Given the preceding property, the following assertion holds:

</div>

<div class="exampleblock">

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
import org.springframework.geode.boot.autoconfigure.configuration.GemFireProperties;

@RunWith(SpringRunner.class)
@SpringBootTest
class GemFirePropertiesTestSuite {

    @Autowired
    private GemFireProperties gemfireProperties;

    @Test
    public void gemfirePropertiesTestCase() {
        assertThat(this.gemfireProperties.getCache().getName()).isNotEqualTo("TestCacheName");
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
<td class="content">You can declare <code>application.properties</code>
in the <code>@SpringBootTest</code> annotation. For example, you could
have declared <code>gemfire.name</code> in the annotation by setting
<code>@SpringBootTest(properties = { "gemfire.name=TestCacheName" })</code>
for testing purposes instead of declaring the property in a separate
Spring Boot <code>application.properties</code> file.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Only `spring.data.gemfire.*` prefixed properties are mapped to the SBDG
`GemFireProperties` class hierarchy.

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
<td class="content">Prefer SDG properties over Geode properties. See the
SDG properties reference in the <a
href="#geode-configuration-metadata-springdata">Appendix</a>.</td>
</tr>
</tbody>
</table>

</div>

</div>

</div>

</div>

<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:12:34 -0700

</div>

</div>
