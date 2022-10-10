<div id="header">

</div>

<div id="content">

<div class="sect1">

## Samples

<div class="sectionbody">

<div class="quoteblock">

> <div class="admonitionblock note">
>
> <table>
> <colgroup>
> <col style="width: 50%" />
> <col style="width: 50%" />
> </colgroup>
> <tbody>
> <tr class="odd">
> <td class="icon"><div class="title">
> Note
> </div></td>
> <td class="content">You are viewing Samples for Spring Boot for Apache
> Geode (SBDG) version 1.27.</td>
> </tr>
> </tbody>
> </table>
>
> </div>

</div>

<div class="paragraph">

This section contains working examples that show how to use Spring Boot
for {pivotal-gemfire-name} (SBDG) effectively.

</div>

<div class="paragraph">

Some examples focus on specific use cases (such as (HTTP) session state
caching), while other examples show how SBDG works under the hood, to
give you a better understanding of what is actually happening and how to
debug problems with your Spring Boot {pivotal-gemfire-name}
applications.

</div>

<table class="tableblock frame-all grid-all stretch">
<caption>Table 1. Example Spring Boot applications using
{pivotal-gemfire-name}</caption>
<colgroup>
<col style="width: 33%" />
<col style="width: 33%" />
<col style="width: 33%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Guide</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Source</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p><a
href="guides/getting-started.html">Getting Started with Spring Boot for
{pivotal-gemfire-name}</a></p></td>
<td class="tableblock halign-left valign-top"><p>Explains how to get
started quickly, easily, and reliably building {pivotal-gemfire-name}
powered applications with Spring Boot.</p></td>
<td
class="tableblock halign-left valign-top"><p>{github-samples-url}/intro/getting-started[Getting
Started]</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p><a
href="guides/boot-configuration.html">Spring Boot Auto-Configuration for
{pivotal-gemfire-name}</a></p></td>
<td class="tableblock halign-left valign-top"><p>Explains what
auto-configuration is provided by SBDG and what the auto-configuration
does.</p></td>
<td
class="tableblock halign-left valign-top"><p>{github-samples-url}/boot/configuration[Spring
Boot Auto-Configuration]</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p><a
href="guides/boot-actuator.html">Spring Boot Actuator for
{pivotal-gemfire-name}</a></p></td>
<td class="tableblock halign-left valign-top"><p>Explains how to use
Spring Boot Actuator for {pivotal-gemfire-name} and how it
works.</p></td>
<td
class="tableblock halign-left valign-top"><p>{github-samples-url}/boot/actuator[Spring
Boot Actuator]</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p><a
href="guides/boot-security.html">Spring Boot Security for
{pivotal-gemfire-name}</a></p></td>
<td class="tableblock halign-left valign-top"><p>Explains how to
configure auth and TLS with SSL when you use {pivotal-gemfire-name} in
your Spring Boot applications.</p></td>
<td
class="tableblock halign-left valign-top"><p>{github-samples-url}/boot/security[Spring
Boot Security]</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p><a
href="guides/caching-look-aside.html">Look-Aside Caching with Spring’s
Cache Abstraction and {pivotal-gemfire-name}</a></p></td>
<td class="tableblock halign-left valign-top"><p>Explains how to enable
and use Spring’s Cache Abstraction with {pivotal-gemfire-name} as the
caching provider for look-aside caching.</p></td>
<td
class="tableblock halign-left valign-top"><p>{github-samples-url}/caching/look-aside[Look-Aside
Caching]</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p><a
href="guides/caching-inline.html">Inline Caching with Spring’s Cache
Abstraction and {pivotal-gemfire-name}</a></p></td>
<td class="tableblock halign-left valign-top"><p>Explains how to enable
and use Spring’s Cache Abstraction with {pivotal-gemfire-name} as the
caching provider for inline caching. This sample builds on the
look-aside caching sample.</p></td>
<td
class="tableblock halign-left valign-top"><p>{github-samples-url}/caching/inline[Inline
Caching]</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p><a
href="guides/caching-inline-async.html">Asynchronous Inline Caching with
Spring’s Cache Abstraction and {pivotal-gemfire-name}</a></p></td>
<td class="tableblock halign-left valign-top"><p>Explains how to enable
and use Spring’s Cache Abstraction with {pivotal-gemfire-name} as the
caching provider for asynchronous inline caching. This sample builds on
the look-aside and inline caching samples.</p></td>
<td
class="tableblock halign-left valign-top"><p>{github-samples-url}/caching/inline-async[Asynchronous
Inline Caching]</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p><a
href="guides/caching-near.html">Near Caching with Spring’s Cache
Abstraction and {pivotal-gemfire-name}</a></p></td>
<td class="tableblock halign-left valign-top"><p>Explains how to enable
and use Spring’s Cache Abstraction with {pivotal-gemfire-name} as the
caching provider for near caching. This sample builds on the look-aside
caching sample.</p></td>
<td
class="tableblock halign-left valign-top"><p>{github-samples-url}/caching/near[Near
Caching]</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p><a
href="guides/caching-multi-site.html">Multi-Site Caching with Spring’s
Cache Abstraction and {pivotal-gemfire-name}</a></p></td>
<td class="tableblock halign-left valign-top"><p>Explains how to enable
and use Spring’s Cache Abstraction with {pivotal-gemfire-name} as the
caching provider for multi-site caching. This sample builds on the
look-aside caching sample.</p></td>
<td
class="tableblock halign-left valign-top"><p>{github-samples-url}/caching/multi-site[Multi-Site
Caching]</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p><a
href="guides/caching-http-session.html">HTTP Session Caching with Spring
Session and {pivotal-gemfire-name}</a></p></td>
<td class="tableblock halign-left valign-top"><p>Explains how to enable
and use Spring Session with {pivotal-gemfire-name} to manage HTTP
session state.</p></td>
<td
class="tableblock halign-left valign-top"><p>{github-samples-url}/caching/http-session[HTTP
Session Caching]</p></td>
</tr>
</tbody>
</table>

Table 1. Example Spring Boot applications using {pivotal-gemfire-name}

</div>

</div>

</div>

<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:15:43 -0700

</div>

</div>
