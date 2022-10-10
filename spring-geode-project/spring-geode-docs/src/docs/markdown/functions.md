<div id="header">

</div>

<div id="content">

<div class="sect1">

## Function Implementations & Executions

<div class="sectionbody">

<div class="paragraph">

This chapter is about using {pivotal-gemfire-name} in a Spring context
for distributed computing use cases.

</div>

<div class="sect2">

### Background

<div class="paragraph">

Distributed computing, particularly in conjunction with data access and
mutation operations, is a very effective and efficient use of clustered
computing resources. This is similar to
{wikipedia-docs}/MapReduce\[MapReduce\].

</div>

<div class="paragraph">

A naively conceived query returning potentially hundreds of thousands
(or even millions) of rows of data in a result set to the application
that queried and requested the data can be very costly, especially under
load. Therefore, it is typically more efficient to move the processing
and computations on the predicated data set to where the data resides,
perform the required computations, summarize the results, and then send
the reduced data set back to the client.

</div>

<div class="paragraph">

Additionally, when the computations are handled in parallel, across the
cluster of computing resources, the operation can be performed much more
quickly. This typically involves intelligently organizing the data using
various partitioning (a.k.a. sharding) strategies to uniformly balance
the data set across the cluster.

</div>

<div class="paragraph">

{pivotal-gemfire-name} addresses this very important application concern
in its
{apache-geode-docs}/developing/function_exec/chapter_overview.html\[Function
execution\] framework.

</div>

<div class="paragraph">

Spring Data for {pivotal-gemfire-name}
{spring-data-geode-docs-html}/#function-annotations\[builds\] on this
Function execution framework by letting developers
{spring-data-geode-docs-html}/#function-implementation\[implement\] and
{spring-data-geode-docs-html}/#function-execution\[execute\]
{pivotal-gemfire-name} functions with a simple POJO-based annotation
configuration model.

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
<td class="content">See
{spring-data-geode-docs-html}/#_implementation_vs_execution[the section
about implementation versus execution] for the difference between
Function implementation and execution.</td>
</tr>
</tbody>
</table>

</div>

<div class="paragraph">

Taking this a step further, Spring Boot for {pivotal-gemfire-name}
auto-configures and enables both Function implementation and execution
out-of-the-box. Therefore, you can immediately begin writing Functions
and invoking them without having to worry about all the necessary
plumbing to begin with. You can rest assured that it works as expected.

</div>

</div>

<div class="sect2">

### Applying Functions

<div class="paragraph">

Earlier, when we talked about [caching](#geode-caching-provider), we
described a `FinancialLoanApplicationService` class that could process
eligibility when someone (represented by a `Person` object) applied for
a financial loan.

</div>

<div class="paragraph">

This can be a very resource intensive and expensive operation, since it
might involve collecting credit and employment history, gathering
information on outstanding loans, and so on. We applied caching in order
to not have to recompute or redetermine eligibility every time a loan
office may want to review the decision with the customer.

</div>

<div class="paragraph">

But, what about the process of computing eligibility in the first place?

</div>

<div class="paragraph">

Currently, the application’s `FinancialLoanApplicationService` class
seems to be designed to fetch the data and perform the eligibility
determination in place. However, it might be far better to distribute
the processing and even determine eligibility for a larger group of
people all at once, especially when multiple, related people are
involved in a single decision, as is typically the case.

</div>

<div class="paragraph">

We can implement an `EligibilityDeterminationFunction` class by using
SDG:

</div>

<div class="exampleblock">

<div class="title">

Example 1. Function implementation

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Component
class EligibilityDeterminationFunction {

    @GemfireFunction(HA = true, hasResult = true, optimizeForWrite=true)
    public EligibilityDecision determineEligibility(FunctionContext functionContext, Person person, Timespan timespan) {
        // ...
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

By using the SDG
{spring-data-geode-javadoc}/org/springframework/data/gemfire/function/annotation/GemfireFunction.html\[`@GemfireFunction`\]
annotation, we can implement our Function as a POJO method. SDG
appropriately handles registering this POJO method as a proper Function
with {pivotal-gemfire-name}.

</div>

<div class="paragraph">

If we now want to call this function from our Spring Boot `ClientCache`
application, we can define a function execution interface with a method
name that matches the function name and that targets the execution on
the `EligibilityDecisions` Region:

</div>

<div class="exampleblock">

<div class="title">

Example 2. Function execution

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@OnRegion("EligibilityDecisions")
interface EligibilityDeterminationExecution {

  EligibilityDecision determineEligibility(Person person, Timespan timespan);

}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

We can then inject an instance of the
`EligibilityDeterminationExecution` interface into our
`FinancialLoanApplicationService`, as we would any other object or
Spring bean:

</div>

<div class="exampleblock">

<div class="title">

Example 3. Function use

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Service
class FinancialLoanApplicationService {

    private final EligibilityDeterminationExecution execution;

    public LoanApplicationService(EligibilityDeterminationExecution execution) {
        this.execution = execution;
    }

    @Cacheable("EligibilityDecisions")
    EligibilityDecision processEligibility(Person person, Timespan timespan) {
        return this.execution.determineEligibility(person, timespan);
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

As with caching, no additional configuration is required to enable and
find your application Function implementations and executions. You can
simply build and run. Spring Boot for {pivotal-gemfire-name} handles the
rest.

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
<td class="content">It is common to "implement" and register your
application Functions on the server and "execute" them from the
client.</td>
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

Last updated 2022-10-10 12:13:25 -0700

</div>

</div>
