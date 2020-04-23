/*
 * Copyright 2020 the original author or authors.
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
package org.springframework.geode.context.annotation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@literal refreshable} {@link ApplicationContext} capable of loading {@link Class component classes} common
 * in {@link Annotation} based configuration as well as scanning {@link String configuration locations}.
 *
 * @author John Blum
 * @see org.springframework.beans.factory.config.BeanDefinition
 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry
 * @see org.springframework.beans.factory.support.BeanNameGenerator
 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.annotation.AnnotatedBeanDefinitionReader
 * @see org.springframework.context.annotation.AnnotationConfigRegistry
 * @see org.springframework.context.annotation.ClassPathBeanDefinitionScanner
 * @see org.springframework.context.annotation.ScopeMetadataResolver
 * @see org.springframework.context.support.AbstractRefreshableConfigApplicationContext
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public class RefreshableAnnotationConfigApplicationContext extends AbstractRefreshableConfigApplicationContext
		implements AnnotationConfigRegistry {

	protected static final boolean USE_DEFAULT_FILTERS = true;

	@Nullable
	private BeanNameGenerator beanNameGenerator;

	private volatile DefaultListableBeanFactory beanFactory;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Set<String> basePackages = new LinkedHashSet<>();
	private final Set<Class<?>> componentClasses = new LinkedHashSet<>();

	@Nullable
	private ScopeMetadataResolver scopeMetadataResolver;

	/**
	 * Constructs an new instance of the {@link RefreshableAnnotationConfigApplicationContext}
	 * with default container state an no {@literal parent} {@link ApplicationContext}.
	 *
	 * @see #refreshBeanFactory()
	 */
	public RefreshableAnnotationConfigApplicationContext() {
		refreshBeanFactory(); // TODO: Not certain this is a good idea; see comment below on createBeanFactory() method!
	}

	/**
	 * Constructs a new instance of the {@link RefreshableAnnotationConfigApplicationContext} initialized with
	 * the {@literal parent} {@link ApplicationContext}.
	 *
	 * @param parent parent {@link ApplicationContext} to this context.
	 * @see org.springframework.context.ApplicationContext
	 * @see #refreshBeanFactory()
	 */
	public RefreshableAnnotationConfigApplicationContext(@Nullable ApplicationContext parent) {
		super(parent);
		refreshBeanFactory(); // TODO: Not certain this is a good idea; see comment below on createBeanFactory() method!
	}

	/**
	 * Configures the {@link BeanNameGenerator} strategy used by this {@link ApplicationContext} to generate
	 * {@link String bean names} for {@link BeanDefinition bean definitions}.
	 *
	 * @param beanNameGenerator {@link BeanNameGenerator} used to generate {@link String bean names}
	 * for {@link BeanDefinition bean definitions}.
	 * @see org.springframework.beans.factory.support.BeanNameGenerator
	 */
	public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = beanNameGenerator;
	}

	/**
	 * Returns the {@link Optional optionally} configured {@link BeanNameGenerator} strategy used by this
	 * {@link ApplicationContext} to generate {@link String bean names} for {@link BeanDefinition bean definitions}.
	 *
	 * @return the {@link BeanNameGenerator} strategy used to generate {@link String bean names}
	 * for {@link BeanDefinition bean definitions}.
	 * @see org.springframework.beans.factory.support.BeanNameGenerator
	 * @see java.util.Optional
	 */
	protected Optional<BeanNameGenerator> getBeanNameGenerator() {
		return Optional.ofNullable(this.beanNameGenerator);
	}

	/**
	 * Returns the configured {@link Logger} used to log framework {@link String messages} to the application log.
	 *
	 * @return the configured {@link Logger}.
	 * @see org.slf4j.Logger
	 */
	protected Logger getLogger() {
		return this.logger;
	}

	/**
	 * Configures the {@link ScopeMetadataResolver} strategy used by this {@link ApplicationContext} to resolve
	 * the {@literal scope} for {@link BeanDefinition bean definitions}.
	 *
	 * @param scopeMetadataResolver {@link ScopeMetadataResolver} used to resolve the {@literal scope}
	 * of {@link BeanDefinition bean definitions}.
	 * @see org.springframework.context.annotation.ScopeMetadataResolver
	 */
	public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
		this.scopeMetadataResolver = scopeMetadataResolver;
	}

	/**
	 * Returns the {@link Optional optionally} configured {@link ScopeMetadataResolver} strategy used by
	 * this {@link ApplicationContext} to resolve the {@literal scope} for {@link BeanDefinition bean definitions}.
	 *
	 * @return the configured {@link ScopeMetadataResolver} used to resolve the {@literal scope}
	 * for {@link BeanDefinition bean definitions}.
	 * @see org.springframework.context.annotation.ScopeMetadataResolver
	 * @see java.util.Optional
	 */
	public Optional<ScopeMetadataResolver> getScopeMetadataResolver() {
		return Optional.ofNullable(this.scopeMetadataResolver);
	}

	protected boolean isUsingDefaultFilters() {
		return USE_DEFAULT_FILTERS;
	}

	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException {

		AnnotatedBeanDefinitionReader reader = configure(newAnnotatedBeanDefinitionReader(beanFactory));

		ClassPathBeanDefinitionScanner scanner = configure(newClassBeanDefinitionScanner(beanFactory));

		getBeanNameGenerator().ifPresent(beanNameGenerator -> {
			reader.setBeanNameGenerator(beanNameGenerator);
			scanner.setBeanNameGenerator(beanNameGenerator);
			beanFactory.registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
		});

		getScopeMetadataResolver().ifPresent(scopeMetadataResolver -> {
			reader.setScopeMetadataResolver(scopeMetadataResolver);
			scanner.setScopeMetadataResolver(scopeMetadataResolver);
		});

		Arrays.stream(ArrayUtils.nullSafeArray(getConfigLocations(), String.class)).forEach(configLocation -> {
			try {
				Class<?> type = ClassUtils.forName(configLocation, getClassLoader());
				getLogger().trace("Registering [{}]", configLocation);
				reader.register(type);
			}
			catch (ClassNotFoundException cause) {

				getLogger().trace(String.format("Could not load class for config location [%s] - trying package scan.",
					configLocation), cause);

				if (scanner.scan(configLocation) == 0) {
					getLogger().debug("No component classes found for specified class/package [{}]", configLocation);
				}
			}
		});
	}

	private AnnotatedBeanDefinitionReader configure(AnnotatedBeanDefinitionReader reader) {

		Set<Class<?>> componentClasses = this.componentClasses;

		if (!componentClasses.isEmpty()) {
			getLogger().debug("Registering component classes: {}", componentClasses);
			reader.register(ClassUtils.toClassArray(componentClasses));
		}

		return reader;
	}

	private ClassPathBeanDefinitionScanner configure(ClassPathBeanDefinitionScanner scanner) {

		Set<String> basePackages = this.basePackages;

		if (!basePackages.isEmpty()) {
			getLogger().debug("Scanning base packages: {}", basePackages);
			scanner.scan(StringUtils.toStringArray(basePackages));
		}

		return scanner;
	}

	// TODO: Not entirely certain this is a good idea (yet); However, it does follow the BeanFactory creation pattern
	//  used by //  the GenericApplicationContext (& extensions, e.g. AnnotationConfigApplicationContext) that I am
	//  trying to //  preserve in this RefreshableAnnotationConfigApplicationContext implementation. However, the
	//  AnnotationConfigApplicationContext implementation is NOT refreshable either, although
	//  AnnotationConfigWebApplicationContext is, so... hmm!
	// This overridden createBeanFactory() method effectively maintains the BeanFactory as a Singleton!
	@Override
	protected synchronized DefaultListableBeanFactory createBeanFactory() {

		if (this.beanFactory == null) {
			this.beanFactory = super.createBeanFactory();
		}

		return this.beanFactory;
	}

	protected AnnotatedBeanDefinitionReader newAnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
		return new AnnotatedBeanDefinitionReader(registry, getEnvironment());
	}

	protected ClassPathBeanDefinitionScanner newClassBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		return new ClassPathBeanDefinitionScanner(registry, isUsingDefaultFilters(), getEnvironment());
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void register(Class<?>... componentClasses) {

		Arrays.stream(ArrayUtils.nullSafeArray(componentClasses, Class.class))
			.filter(Objects::nonNull)
			.forEach(this.componentClasses::add);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void scan(String... basePackages) {

		Arrays.stream(ArrayUtils.nullSafeArray(basePackages, String.class))
			.filter(StringUtils::hasText)
			.forEach(this.basePackages::add);
	}
}
