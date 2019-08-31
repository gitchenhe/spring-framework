/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * <h3>这个工厂接口最大的特点就是可以列出工厂可以生产的所有实例. 当然工厂并没有直接提供返回所有实例的方法,也没有这个必要.它可以返回指定类型的所有实例.</h3>
 */
public interface ListableBeanFactory extends BeanFactory {

	/**
	 * 当前工厂是否包含 beanName的 定义信息
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 * 当前工厂包含bean define 数量
	 */
	int getBeanDefinitionCount();

	/**
	 * 当前工厂可以创建的所有bean的name
	 */
	String[] getBeanDefinitionNames();

	/**
	 * 返回所有类型与type相同的,所有bean的name
	 */
	String[] getBeanNamesForType(ResolvableType type);

	/**
	 * 返回所有类型与type相同的,所有bean的name
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type);

	/**
	 * 返回所有类型与type相同的,所有bean的name
	 * @param includeNonSingletons 包括非单例
	 * @param allowEagerInit 允许饿汉式初始化
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * 返回所有类型与type相同的,所有bean的name
	 * <br>
	 * 返回结果包含 beanName 与 Bean
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException;

	/**
	 * <h3>返回指定类型的实例</h3>
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException;

	/**
	 * <h3>根据注解返回bean的names</h3>
	 */
	String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

	/**
	 * <h3>根据注查找bean的name和实例</h3>
	 */
	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

	/**
	 * <h3>查找bean上的注解</h3>
	 */
	@Nullable
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException;

}
