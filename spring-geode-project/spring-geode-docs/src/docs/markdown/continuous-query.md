<div id="header">

</div>

<div id="content">

<div class="sect1">

## Continuous Query

<div class="sectionbody">

<div class="paragraph">

Some applications must process a stream of events as they happen and
intelligently react in (near) real-time to the countless changes in the
data over time. Those applications need frameworks that can make
processing a stream of events as they happen as easy as possible.

</div>

<div class="paragraph">

Spring Boot for {pivotal-gemfire-name} does just that, without users
having to perform any complex setup or configure any necessary
infrastructure components to enable such functionality. Developers can
define the criteria for the data of interest and implement a handler
(listener) to process the stream of events as they occur.

</div>

<div class="paragraph">

{apache-geode-docs}/developing/continuous_querying/chapter_overview.html\[Continuous
Query (CQ)\] lets you easily define your criteria for the data you need.
With CQ, you can express the criteria that match the data you need by
specifying a query predicate. {pivotal-gemfire-name} implements the
{apache-geode-docs}/developing/querying_basics/query_basics.html\[Object
Query Language (OQL)\] for defining and executing queries. OQL resembles
SQL and supports projections, query predicates, ordering, and
aggregates. Also, when used in CQs, they execute continuously, firing
events when the data changes in such ways as to match the criteria
expressed in the query predicate.

</div>

<div class="paragraph">

Spring Boot for {pivotal-gemfire-name} combines the ease of identifying
the data you need by using an OQL query statement with implementing the
listener callback (handler) in one easy step.

</div>

<div class="paragraph">

For example, suppose you want to perform some follow-up action when a
customer’s financial loan application is either approved or denied.

</div>

<div class="paragraph">

First, the application model for our `EligibilityDecision` class might
look something like the following:

</div>

<div class="exampleblock">

<div class="title">

Example 1. EligibilityDecision class

</div>

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Region("EligibilityDecisions")
class EligibilityDecision {

    private final Person person;

    private Status status = Status.UNDETERMINED;

    private final Timespan timespan;

    enum Status {

        APPROVED,
        DENIED,
        UNDETERMINED,

    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Then we can implement and declare our CQ event handler methods to be
notified when an eligibility decision is either `APPROVED` or `DENIED`:

</div>

<div class="exampleblock">

<div class="content">

<div class="listingblock">

<div class="content">

``` highlight
@Component
class EligibilityDecisionPostProcessor {

    @ContinuousQuery(name = "ApprovedDecisionsHandler",
        query = "SELECT decisions.*
                 FROM /EligibilityDecisions decisions
                 WHERE decisions.getStatus().name().equalsIgnoreCase('APPROVED')")
    public void processApprovedDecisions(CqEvent event) {
        // ...
    }

    @ContinuousQuery(name = "DeniedDecisionsHandler",
        query = "SELECT decisions.*
                 FROM /EligibilityDecisions decisions
                 WHERE decisions.getStatus().name().equalsIgnoreCase('DENIED')")
    public void processDeniedDecisions(CqEvent event) {
        // ...
    }
}
```

</div>

</div>

</div>

</div>

<div class="paragraph">

Thus, when eligibility is processed and a decision has been made, either
approved or denied, our application gets notified, and as an application
developer, you are free to code your handler and respond to the event
any way you like. Also, because our Continuous Query (CQ) handler class
is a component (or a bean in the Spring `ApplicationContext`) you can
auto-wire any other beans necessary to carry out the application’s
intended function.

</div>

<div class="paragraph">

This is not unlike Spring’s
{spring-framework-docs}/integration.html#jms-annotated\[annotation-driven
listener endpoints\], which are used in (JMS) message listeners and
handlers, except in Spring Boot for {pivotal-gemfire-name}, you need not
do anything special to enable this functionality. You can declare the
`@ContinuousQuery` annotation on any POJO method and go to work on other
things.

</div>

</div>

</div>

</div>

<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:13:35 -0700

</div>

</div>
