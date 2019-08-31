/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.config;

import java.util.Iterator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.lang.Nullable;

/**
 * <h3>提供了BeanDefine的解析,注册功能,在对单例来个预加载(解决循环依赖问题)</h3>
 *
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

	/**
	 * 忽略指定类型的,自动装配,比如String,不会被注入
	 */
	void ignoreDependencyType(Class<?> type);

	/**
	 * 指定接口,忽略自动装配
	 */
	void ignoreDependencyInterface(Class<?> ifc);

	/**
	 * 注册一个特别的特性,与自动装配的值
	 */
	void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue);

	/**
	 * 判断指定的Bean是否有资格作为自动装配的候选人
	 */
	boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
			throws NoSuchBeanDefinitionException;

	/**
	 * 获取BeanDefine
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * 获取所有的BeanName
	 */
	Iterator<String> getBeanNamesIterator();

	/**
	 * 清理元数据缓存
	 */
	void clearMetadataCache();

	/**
	 * 冻结所有Bean的配置
	 */
	void freezeConfiguration();

	/**
	 * 判断是否冻结
	 */
	boolean isConfigurationFrozen();

	/**
	 * 所有的非延迟加载的单例类都实例化
	 */
	void preInstantiateSingletons() throws BeansException;

}
