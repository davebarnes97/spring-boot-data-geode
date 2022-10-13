---
Title: Externalized Configuration
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



Like Spring Boot itself (see
{spring-boot-docs-html}/boot-features-external-config.html\[Spring
Boot’s documentation\]), Spring Boot for {pivotal-gemfire-name} (SBDG)
supports externalized configuration.





By externalized configuration, we mean configuration metadata stored in
Spring Boot
{spring-boot-docs-html}/boot-features-external-config.html#boot-features-external-config-application-property-files\[`application.properties`\].
You can even separate concerns by addressing each concern in an
individual properties file. Optionally, you could also enable any given
property file for only a specific
{spring-boot-docs-html}/boot-features-external-config.html#boot-features-external-config-profile-specific-properties\[profile\].





You can do many other powerful things, such as (but not limited to)
using
{spring-boot-docs-html}/boot-features-external-config.html#boot-features-external-config-placeholders-in-properties\[placeholders\]
in properties,
{spring-boot-docs-html}/boot-features-external-config.html#boot-features-encrypting-properties\[encrypting\]
properties, and so on. In this section, we focus particularly on
{spring-boot-docs-html}/boot-features-external-config.html#boot-features-external-config-typesafe-configuration-properties\[type
safety\].





Like Spring Boot, Spring Boot for {pivotal-gemfire-name} provides a
hierarchy of classes that captures configuration for several
{pivotal-gemfire-name} features in an associated
`@ConfigurationProperties` annotated class. Again, the configuration
metadata is specified as well-known, documented properties in one or
more Spring Boot `application.properties` files.





For instance, a Spring Boot, {pivotal-gemfire-name} `ClientCache`
application might be configured as follows:







Example 1. Spring Boot `application.properties` containing Spring Data
properties for {pivotal-gemfire-name}









``` highlight
# Spring Boot application.properties used to configure {geode-name}

spring.data.gemfire.name=MySpringBootApacheGeodeApplication

# Configure general cache properties
spring.data.gemfire.cache.copy-on-read=true
spring.data.gemfire.cache.log-level=debug

# Configure ClientCache specific properties
spring.data.gemfire.cache.client.durable-client-id=123
spring.data.gemfire.cache.client.keep-alive=true

# Configure a log file
spring.data.gemfire.logging.log-file=/path/to/geode.log

# Configure the client's connection Pool to the servers in the cluster
spring.data.gemfire.pool.locators=10.105.120.16[11235],boombox[10334]
```











You can use many other properties to externalize the configuration of
your Spring Boot, {pivotal-gemfire-name} applications. See the
{spring-data-geode-javadoc}/org/springframework/data/gemfire/config/annotation/package-frame.html\[Javadoc\]
for specific configuration properties. Specifically, review the
`enabling` annotation attributes.





You may sometimes require access to the configuration metadata
(specified in properties) in your Spring Boot applications themselves,
perhaps to further inspect or act on a particular configuration setting.
You can access any property by using Spring’s
{spring-framework-javadoc}/org/springframework/core/env/Environment.html\[`Environment`\]
abstraction:







Example 2. Using the Spring `Environment`









``` highlight
@Configuration
class GeodeConfiguration {

    void readConfigurationFromEnvironment(Environment environment) {
        boolean copyOnRead = environment.getProperty("spring.data.gemfire.cache.copy-on-read",
            Boolean.TYPE, false);
    }
}
```











While using `Environment` is a nice approach, you might need access to
additional properties or want to access the property values in a
type-safe manner. Therefore, you can now, thanks to SBDG’s
auto-configured configuration processor, access the configuration
metadata by using `@ConfigurationProperties` classes.





To add to the preceding example, you can now do the following:







Example 3. Using `GemFireProperties`









``` highlight
@Component
class MyApplicationComponent {

    @Autowired
    private GemFireProperties gemfireProperties;

    public void someMethodUsingGemFireProperties() {

        boolean copyOnRead = this.gemfireProperties.getCache().isCopyOnRead();

        // do something with `copyOnRead`
    }
}
```











Given a handle to
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/configuration/GemFireProperties.html\[`GemFireProperties`\],
you can access any of the configuration properties that are used to
configure {pivotal-gemfire-name} in a Spring context. You need only
autowire an instance of `GemFireProperties` into your application
component.





See the complete reference for the
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/configuration/package-frame.html\[SBDG
`@ConfigurationProperties` classes and supporting classes\].





### Externalized Configuration of Spring Session



You can access the externalized configuration of Spring Session when you
use {pivotal-gemfire-name} as your (HTTP) session state caching
provider.





In this case, you need only acquire a reference to an instance of the
{spring-boot-data-geode-javadoc}/org/springframework/geode/boot/autoconfigure/configuration/SpringSessionProperties.html\[`SpringSessionProperties`\]
class.





As shown earlier in this chapter, you can specify Spring Session for
{pivotal-gemfire-name} (SSDG) properties as follows:







Example 4. Spring Boot `application.properties` for Spring Session using
{pivotal-gemfire-name} as the (HTTP) session state caching provider









``` highlight
# Spring Boot application.properties used to configure {geode-name} as a (HTTP) session state caching provider
# in Spring Session

spring.session.data.gemfire.session.expiration.max-inactive-interval-seconds=300
spring.session.data.gemfire.session.region.name=UserSessions
```











Then, in your application, you can do something similar to the following
example:







Example 5. Using `SpringSessionProperties`









``` highlight
@Component
class MyApplicationComponent {

    @Autowired
    private SpringSessionProperties springSessionProperties;

    public void someMethodUsingSpringSessionProperties() {

        String sessionRegionName = this.springSessionProperties
            .getSession().getRegion().getName();

        // do something with `sessionRegionName`
    }
}
```

















<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:12:18 -0700




