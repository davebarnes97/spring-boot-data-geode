/*
 * Copyright 2019 the original author or authors.
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
package org.springframework.geode.boot.autoconfigure.template;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Resource;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.boot.autoconfigure.ContinuousQueryAutoConfiguration;
import org.springframework.geode.boot.autoconfigure.RegionTemplateAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import example.app.books.model.Author;
import example.app.books.model.Book;
import example.app.books.model.ISBN;

/**
 * Integration tests for {@link RegionTemplateAutoConfiguration} using SDG's {@link EnableEntityDefinedRegions}
 * annotation to define {@link Region Regions} and associated Templates.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.geode.boot.autoconfigure.RegionTemplateAutoConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SuppressWarnings("unused")
public class EntityDefinedRegionTemplateAutoConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("authorsTemplate")
	private GemfireTemplate authorsTemplate;

	@Autowired
	@Qualifier("booksTemplate")
	private GemfireTemplate booksTemplate;

	@Resource(name = "Authors")
	private Region<Long, Author> authors;

	@Resource(name = "Books")
	private Region<ISBN, Book> books;

	@Test
	public void authorsRegionTemplateIsPresent() {

		assertThat(this.authorsTemplate).isNotNull();
		assertThat(this.authorsTemplate.getRegion()).isEqualTo(this.authors);
	}

	@Test
	public void booksRegionTemplateIsPresent() {

		assertThat(this.booksTemplate).isNotNull();
		assertThat(this.booksTemplate.getRegion()).isEqualTo(this.books);
	}

	@EnableGemFireMockObjects
	@SpringBootApplication(exclude = ContinuousQueryAutoConfiguration.class)
	@EnableEntityDefinedRegions(basePackageClasses = Book.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
	static class TestApplicationConfiguration { }

}
