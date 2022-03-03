/*
 * Copyright 2017-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.springframework.geode.context.logging;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for Apache Geode Logging configured with the {@literal spring.data.gemfire.logging.level} property
 * in JVM {@link System} {@link System#getProperties() Properties}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see AbstractSpringConfiguredLogLevelPropertyIntegrationTests
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataGemFireLoggingLevelSystemPropertyIntegrationTests
		extends AbstractSpringConfiguredLogLevelPropertyIntegrationTests {

	@BeforeClass
	public static void setup() {
		System.setProperty("spring.data.gemfire.logging.level", "DEBUG");
		System.setProperty("spring.data.gemfire.cache.log-level", "TRACE");
	}
}
