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

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * 1.定义了通过别名或类型,检索bean实例的几种方式
 * 2.通过&beanName,获取bean的facotry
 */
public interface BeanFactory {

	/**
	 *
	 * 通过&beanName,获取bean的工厂类
	 */
	String FACTORY_BEAN_PREFIX = "&";


	/**
	 * 返回一个实例,这可能是共享或独立,指定的bean.
	 * 这种方法允许一个Spring Bean工厂用作替代单例或原型设计模式.
	 */
	Object getBean(String name) throws BeansException;

	/**
	 * 获取bean
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * 获取bean
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * 获取bean
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	/**
	 * 获取bean
	 */
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;


	/**
	 * bean是否存在
	 */
	boolean containsBean(String name);

	/**
	 * bean是否单例
	 */
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * bean是否是原型模式
	 */
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 别名是否是指定类型
	 */
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * 别名是否是指定类型
	 */
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * 根据别名,查找bean的类型
	 */
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 获取bean的所有别名
	 */
	String[] getAliases(String name);

}
