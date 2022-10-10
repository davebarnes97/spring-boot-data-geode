<div id="header">

</div>

<div id="content">

<div class="sect1">

## Apache Geode API Extensions

<div class="sectionbody">

<div class="paragraph">

When using the Spring programming model and abstractions, it should not
be necessary to use {pivotal-gemfire-name}
{apache-geode-javadoc}\[APIs\] at all — for example, when using the
Spring Cache Abstraction for caching or the Spring Data Repository
abstraction for DAO development. There are many more examples.

</div>

<div class="paragraph">

For certain use cases, users may require low level access to
fine-grained functionally. Spring Boot for {pivotal-gemfire-name}'s
`org.springframework.geode:apache-geode-extensions` module and library
builds on {pivotal-gemfire-name}'s APIs by including several extensions
with enhanced functionality to offer an experience familiar to Spring
users inside a Spring context.

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
<td class="content">Spring Data for {pivotal-gemfire-name} (SDG) also
{spring-data-geode-docs-html}/#apis[includes] additional extensions to
{pivotal-gemfire-name}'s APIs.</td>
</tr>
</tbody>
</table>

</div>

<div class="sect2">

### `SimpleCacheResolver`

<div class="paragraph">

In some cases, it is necessary to acquire a reference to the cache
instance in your application components at runtime. For example, you
might want to create a temporary `Region` on the fly to aggregate data
for analysis.

</div>

<div class="paragraph">

Typically, you already know the type of cache your application is using,
since you must declare your application to be either a client
(`ClientCache`) in the
{apache-geode-docs}/topologies_and_comm/cs_configuration/chapter_overview.html\[client/server
topology\], or a
{apache-geode-docs}/topologies_and_comm/p2p_configuration/chapter_overview.html\[peer
member or node\] (`Cache`) in the cluster on startup. This is expressed
in configuration when creating the cache instance required to interact
with the {pivotal-gemfire-name} data management system. In most cases,
your application will be a client. SBDG makes this decision easy, since
it auto-configures a `ClientCache` instance, [by
default](#geode-clientcache-applications).

</div>

<div class="paragraph">

In a Spring context, the cache instance created by the framework is a
managed bean in the Spring container. You can inject a reference to the
[*Singleton*](https://en.wikipedia.org/wiki/Singleton_pattern) cache
bean into any other managed application component:

</div>

<div class="exampleblock">

<div class="title">

Example 1. Autowired Cache Reference using Dependency Injection (DI)

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Service
class CacheMonitoringService {

    @Autowired
    ClientCache clientCache;

    // use the clientCache object reference to monitor the cache as necessary

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

However, in cases where your application component or class is not
managed by Spring and you need a reference to the cache instance at
runtime, SBDG provides the abstract
`org.springframework.geode.cache.SimpleCacheResolver` class (see its
{spring-boot-data-geode-javadoc}/org/springframework/geode/cache/SimpleCacheResolver.html\[Javadoc\]).

</div>

<div class="exampleblock">

<div class="title">

Example 2. `SimpleCacheResolver` API

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
package org.springframework.geode.cache;

abstract class SimpleCacheResolver {

    <T extends GemFireCache> T require() { }

    <T extends GemFireCache> Optional<T> resolve() { }

    Optional<ClientCache> resolveClientCache() { }

    Optional<Cache> resolvePeerCache() { }

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

`SimpleCacheResolver` adheres to [SOLID OO
Principles](https://en.wikipedia.org/wiki/SOLID). This class is abstract
and extensible so that you can change the algorithm used to resolve
client or peer cache instances as well as mock its methods in unit
tests.

</div>

<div class="paragraph">

Additionally, each method is precise. For example,
`resolveClientCache()` resolves a reference to a cache only if the cache
instance is a “client.” If a cache exists but is a “peer” cache
instance, `resolveClientCache()` returns `Optional.EMPTY`. The behavior
of `resolvePeerCache()` is similar.

</div>

<div class="paragraph">

`require()` returns a non-`Optional` reference to a cache instance and
throws an `IllegalStateException` if a cache is not present.

</div>

</div>

<div class="sect2">

### `CacheUtils`

<div class="paragraph">

Under the hood, `SimpleCacheResolver` delegates some of its functions to
the
{spring-boot-data-geode-javadoc}/org/springframework/geode/util/CacheUtils.html\[`CacheUtils`\]
abstract utility class, which provides additional, convenient
capabilities when you use a cache.

</div>

<div class="paragraph">

While there are utility methods to determine whether a cache instance
(that is, a `GemFireCache`) or Region is a client or a peer, one of the
more useful functions is to extract all the values from a Region.

</div>

<div class="paragraph">

To extract all the values stored in a Region, call
`CacheUtils.collectValues(:Region<?, T>)`. This method returns a
`Collection<T>` that contains all the values stored in the given
`Region`. The method is smart and knows how to handle the `Region`
appropriately regardless of whether the `Region` is a client or a peer.
This distinction is important, since client `PROXY` Regions store no
values.

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
<td class="content">Caution is advised when you get all values from a
Region. While getting filtered reference values from a
non-transactional, reference data only [<code>REPLICATE</code>] Region
is quite useful, getting all values from a transactional,
[<code>PARTITION</code>] Region can prove quite detrimental, especially
in production. Getting all values from a Region can be useful during
testing.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect2">

### `MembershipListenerAdapter` and `MembershipEvent`

<div class="paragraph">

Another useful API hidden by {pivotal-gemfire-name} is the membership
events and listener interface. This API is especially useful on the
server side when your Spring Boot application serves as a peer member of
an {pivotal-gemfire-name} distributed system.

</div>

<div class="paragraph">

When a peer member is disconnected from the distributed system, perhaps
due to a network failure, the member is forcibly removed from the
cluster. This node immediately enters a reconnecting state, trying to
establish a connection back to the cluster. Once reconnected, the peer
member must rebuild all cache objects (`Cache`, `Region` instances,
`Index` instances, `DiskStore` instances, and so on). All previous cache
objects are now invalid, and their references are stale.

</div>

<div class="paragraph">

In a Spring context, this is particularly problematic since most
{pivotal-gemfire-name} objects are *Singleton* beans declared in and
managed by the Spring container. Those beans may be injected and used in
other framework and application components. For instance, `Region`
instances are injected into SDG’s `GemfireTemplate`, Spring Data
Repositories and possibly application-specific data access objects
([DAOs](https://en.wikipedia.org/wiki/Data_access_object)).

</div>

<div class="paragraph">

If references to those cache objects become stale on a forced disconnect
event, there is no way to auto-wire fresh object references into the
dependent application or framework components when the peer member is
reconnected, unless the Spring `ApplicationContext` is “refreshed”. In
fact, there is no way to even know that this event has occurred, since
the {pivotal-gemfire-name} `MembershipListener` API and corresponding
events are “internal”.

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
<td class="content">The Spring team explored the idea of creating
proxies for all types of cache objects (<code>Cache</code>,
<code>Region</code>, <code>Index</code>, <code>DiskStore</code>,
<code>AsyncEventQueue</code>, <code>GatewayReceiver</code>,
<code>GatewaySender</code>, and others) used by Spring. The proxies
would know how to obtain a fresh reference on a reconnect event.
However, this turns out to be more problematic than it is worth. It is
easier to “refresh” the Spring <code>ApplicationContext</code>, although
doing so is no less expensive. Neither way is ideal. See <a
href="https://jira.spring.io/browse/SGF-921">SGF-921</a> and <a
href="https://jira.spring.io/browse/SGF-227">SGF-227</a> for further
details.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

In the case where membership events are useful to the Spring Boot
application, SBDG provides the following
{spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/package-frame.html\[API\]:

</div>

<div class="ulist">

- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/MembershipListenerAdapter.html\[`MembershipListenerAdapter`\]

- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/MembershipEvent.html\[`MembershipEvent`\]

</div>

<div class="paragraph">

The abstract `MembershipListenerAdapter` class implements
{pivotal-gemfire-name}'s
`org.apache.geode.distributed.internal.MembershipListener` interface to
simplify the event handler method signatures by using an appropriate
`MembershipEvent` type to encapsulate the actors in the event.

</div>

<div class="paragraph">

The abstract `MembershipEvent` class is further subclassed to represent
specific membership event types that occur within the
{pivotal-gemfire-name} system:

</div>

<div class="ulist">

- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/support/MemberDepartedEvent.html\[`MemberDepartedEvent`\]

- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/support/MemberJoinedEvent.html\[`MemberJoinedEvent`\]

- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/support/MemberSuspectEvent.html\[`MemberSuspectEvent`\]

- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/support/QuorumLostEvent.html\[`QuorumLostEvent`\]

</div>

<div class="paragraph">

The API is depicted in the following UML diagram:

</div>

<div class="imageblock">

<div class="content">

![membership api uml](./images/membership-api-uml.png)

</div>

</div>

<div class="paragraph">

The membership event type is further categorized with an appropriate
enumerated value,
{spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/MembershipEvent.Type.html\[`MembershipEvent.Type`\],
as a property of the `MembershipEvent` itself (see
{spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/MembershipEvent.html#getType--\[`getType()`\]).

</div>

<div class="paragraph">

The type hierarchy is useful in `instanceof` expressions, while the
`Enum` is useful in `switch` statements.

</div>

<div class="paragraph">

You can see one particular implementation of the
`MembershipListenerAdapter` with the
{spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/ApplicationContextMembershipListener.html\[`ApplicationContextMembershipListener`\]
class, which does exactly as we described earlier, handling
forced-disconnect/auto-reconnect membership events inside a Spring
container in order to refresh the Spring `ApplicationContext`.

</div>

</div>

<div class="sect2">

### PDX

<div class="paragraph">

{pivotal-gemfire-name}'s PDX serialization framework is yet another API
that falls short of a complete stack.

</div>

<div class="paragraph">

For instance, there is no easy or direct way to serialize an object as
PDX bytes. It is also not possible to modify an existing `PdxInstance`
by adding or removing fields, since doing so would require a new PDX
type. In this case, you must create a new `PdxInstance` and copy from an
existing `PdxInstance`. Unfortunately, the {pivotal-gemfire-name} API
offers no help in this regard. It is also not possible to use PDX in a
client, local-only mode without a server, since the PDX type registry is
only available and managed on servers in a cluster.

</div>

<div class="sect3">

#### `PdxInstanceBuilder`

<div class="paragraph">

In such cases, SBDG conveniently provides the
{spring-boot-data-geode-javadoc}/org/springframework/geode/pdx/PdxInstanceBuilder.html\[`PdxInstanceBuilder`\]
class, appropriately named after the [Builder software design
pattern](https://en.wikipedia.org/wiki/Builder_pattern). The
`PdxInstanceBuilder` also offers a fluent API for constructing
`PdxInstances`:

</div>

<div class="exampleblock">

<div class="title">

Example 3. `PdxInstanceBuilder` API

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
class PdxInstanceBuilder {

    PdxInstanceFactory copy(PdxInstance pdx);

    Factory from(Object target);

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

For example, you could serialize an application domain object as PDX
bytes with the following code:

</div>

<div class="exampleblock">

<div class="title">

Example 4. Serializing an Object to PDX

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Component
class CustomerSerializer {

    PdxInstance serialize(Customer customer) {

        return PdxInstanceBuilder.create()
            .from(customer)
            .create();
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

You could then modify the `PdxInstance` by copying from the original:

</div>

<div class="exampleblock">

<div class="title">

Example 5. Copy `PdxInstance`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Component
class CustomerDecorator {

    @Autowired
    CustomerSerializer serializer;

    PdxIntance decorate(Customer customer) {

        PdxInstance pdxCustomer = serializer.serialize(customer);

        return PdxInstanceBuilder.create()
            .copy(pdxCustomer)
            .writeBoolean("vip", isImportant(customer))
            .create();
    }
}
```

</div>

</div>

</div>

</div>

</div>

<div class="sect3">

#### `PdxInstanceWrapper`

<div class="paragraph">

SBDG also provides the
{spring-boot-data-geode-javadoc}/org/springframework/geode/pdx/PdxInstanceWrapper.html\[`PdxInstanceWrapper`\]
class to wrap an existing `PdxInstance` in order to provide more control
during the conversion from PDX to JSON and from JSON back into a POJO.
Specifically, the wrapper gives you more control over the configuration
of Jackson’s `ObjectMapper`.

</div>

<div class="paragraph">

The `ObjectMapper` constructed by {pivotal-gemfire-name}'s own
`PdxInstance` implementation (`PdxInstanceImpl`) is not configurable,
nor was it configured correctly. Unfortunately, since `PdxInstance` is
not extensible, the `getObject()` method fails when converting the JSON
generated from PDX back into a POJO for any practical application domain
model type.

</div>

<div class="paragraph">

The following example wraps an existing `PdxInstance`:

</div>

<div class="exampleblock">

<div class="title">

Example 6. Wrapping an existing `PdxInstance`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
PdxInstanceWrapper wrapper = PdxInstanceWrapper.from(pdxInstance);
```

</div>

</div>

</div>

</div>

<div class="paragraph">

For all operations on `PdxInstance` except `getObject()`, the wrapper
delegates to the underlying `PdxInstance` method implementation called
by the user.

</div>

<div class="paragraph">

In addition to the decorated `getObject()` method, the
`PdxInstanceWrapper` provides a thorough implementation of the
`toString()` method. The state of the `PdxInstance` is output in a
JSON-like `String`.

</div>

<div class="paragraph">

Finally, the `PdxInstanceWrapper` class adds a `getIdentifier()` method.
Rather than put the burden on the user to have to iterate the field
names of the `PdxInstance` to determine whether a field is the identity
field and then call `getField(name)` with the field name to get the ID
(value) — assuming an identity field was marked in the first place — the
`PdxInstanceWrapper` class provides the `getIdentifier()` method to
return the ID of the `PdxInstance` directly.

</div>

<div class="paragraph">

The `getIdentifier()` method is smart in that it first iterates the
fields of the `PdxInstance`, asking each field if it is the identity
field. If no field was marked as the identity field, the algorithm
searches for a field named `id`. If no field with the name `id` exists,
the algorithm searches for a metadata field called `@identifier`, which
refers to the field that is the identity field of the `PdxInstance`.

</div>

<div class="paragraph">

The `@identifier` metadata field is useful in cases where the
`PdxInstance` originated from JSON and the application domain object
uses a natural identifier, rather than a surrogate ID, such as
`Book.isbn`.

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
<td class="content">{pivotal-gemfire-name}'s <code>JSONFormatter</code>
class is not capable of marking the identity field of a
<code>PdxInstance</code> originating from JSON.</td>
</tr>
</tbody>
</table>

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
<td class="content">It is not currently possible to implement the
<code>PdxInstance</code> interface and store instances of this type as a
value in a Region. {pivotal-gemfire-name} assumes all
<code>PdxInstance</code> objects are an implementation created by
{pivotal-gemfire-name} itself (that is, <code>PdxInstanceImpl</code>),
which has a tight coupling to the PDX type registry. An
<code>Exception</code> is thrown if you try to store instances of your
own <code>PdxInstance</code> implementation.</td>
</tr>
</tbody>
</table>

</div>

</div>

<div class="sect3">

#### `ObjectPdxInstanceAdapter`

<div class="paragraph">

In rare cases, you may need to treat an `Object` as a `PdxInstance`,
depending on the context without incurring the overhead of serializing
an `Object` to PDX. For such cases, SBDG offers the
`ObjectPdxInstanceAdapter` class.

</div>

<div class="paragraph">

This might be true when calling a method with a parameter expecting an
argument of, or returning an instance of, type `PdxInstance`,
particularly when {pivotal-gemfire-name}'s `read-serialized` PDX
configuration property is set to `true` and only an object is available
in the current context.

</div>

<div class="paragraph">

Under the hood, SBDG’s `ObjectPdxInstanceAdapter` class uses Spring’s
{spring-framework-javadoc}/org/springframework/beans/BeanWrapper.html\[`BeanWrapper`\]
class along with Java’s introspection and reflection functionality to
adapt the given `Object` and access it with the full
{apache-geode-javadoc}/org/apache/geode/pdx/PdxInstance.html\[`PdxInstance`\]
API. This includes the use of the
{apache-geode-javadoc}/org/apache/geode/pdx/WritablePdxInstance.html\[`WritablePdxInstance`\]
API, obtained from
{apache-geode-javadoc}/org/apache/geode/pdx/PdxInstance.html#createWriter--\[`PdxInstance.createWriter()`\],
to modify the underlying `Object` as well.

</div>

<div class="paragraph">

Like the `PdxInstanceWrapper` class, `ObjectPdxInstanceAdapter` contains
special logic to resolve the identity field and ID of the `PdxInstance`,
including consideration for Spring Data’s
{spring-data-commons-javadoc}/org/springframework/data/annotation/Id.html\[`@Id`\]
mapping annotation, which can be introspected in this case, given that
the underlying `Object` backing the `PdxInstance` is a POJO.

</div>

<div class="paragraph">

The `ObjectPdxInstanceAdapter.getObject()` method returns the wrapped
`Object` used to construct the `ObjectPdxInstanceAdapter` and is,
therefore, automatically deserializable, as determined by the
{apache-geode-javadoc}/org/apache/geode/pdx/PdxInstance.html#isDeserializable--\[`PdxInstance.isDeseriable()`\]
method, which always returns `true`.

</div>

<div class="paragraph">

You can adapt any `Object` as a `PdxInstance`:

</div>

<div class="exampleblock">

<div class="title">

Example 7. Adapt an `Object` as a `PdxInstance`

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
class OfflineObjectToPdxInstanceConverter {

    @NonNull PdxInstance convert(@NonNull Object target) {
        return ObjectPdxInstanceAdapter.from(target);
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Once the [Adapter](https://en.wikipedia.org/wiki/Adapter_pattern) is
created, you can use it to access data on the underlying `Object`.

</div>

<div class="paragraph">

Consider the following example of a `Customer` class:

</div>

<div class="exampleblock">

<div class="title">

Example 8. `Customer` class

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Region("Customers")
class Customer {

    @Id
    private Long id;

    String name;

    // constructors, getters and setters omitted

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Then you can access an instance of `Customer` by using the `PdxInstance`
API:

</div>

<div class="exampleblock">

<div class="title">

Example 9. Accessing an `Object` using the `PdxInstance` API

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
class ObjectPdxInstanceAdapterTest {

    @Test
    public void getAndSetObjectProperties() {

        Customer jonDoe = new Customer(1L, "Jon Doe");

        PdxInstance adapter = ObjectPdxInstanceAdapter.from(jonDoe);

        assertThat(jonDoe.getName()).isEqualTo("Jon Doe");
        assertThat(adapter.getField("name")).isEqualTo("Jon Doe");

        adapter.createWriter().setField("name", "Jane Doe");

        assertThat(adapter.getField("name")).isEqualTo("Jane Doe");
        assertThat(jonDoe.getName()).isEqualTo("Jane Doe");
    }
}
```

</div>

</div>

</div>

</div>

</div>

</div>

<div class="sect2">

### Security

<div class="paragraph">

For testing purposes, SBDG provides a test implementation of
{pivotal-gemfire-name}'s
{apache-geode-javadoc}/org/apache/geode/security/SecurityManager.html\[`SecurityManager`\]
interface, which expects the password to match the username
(case-sensitive) when authenticating.

</div>

<div class="paragraph">

By default, all operations are authorized.

</div>

<div class="paragraph">

To match the expectations of SBDG’s `TestSecurityManager`, SBDG
additionally provides a test implementation of {pivotal-gemfire-name}'s
{apache-geode-javadoc}/org/apache/geode/security/AuthInitialize.html\[`AuthInitialize`\]
interface, which supplies matching credentials for both the username and
password.

</div>

</div>

</div>

</div>

</div>

<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:14:46 -0700

</div>

</div>
