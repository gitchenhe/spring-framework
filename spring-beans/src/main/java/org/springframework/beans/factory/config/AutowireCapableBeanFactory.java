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

package org.springframework.beans.factory.config;

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.lang.Nullable;

/**
 * <h3>在BeanFactory基础上实现了对存在实例的管理,可以用这个接口,集成其它框架,捆绑并填充并不由Spring管理生命周期并已经存在的实例,
 * 像集成WebWork的Actions和Tapestry Page就很使用</h3>
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

	/**
	 * 这个常量表名工厂没有自动装配的Bean
	 */
	int AUTOWIRE_NO = 0;

	/**
	 * 根据名称自动装配
	 */
	int AUTOWIRE_BY_NAME = 1;

	/**
	 * 根据类型自动装配
	 */
	int AUTOWIRE_BY_TYPE = 2;

	/**
	 * 表明根据构造方法快速装配
	 */
	int AUTOWIRE_CONSTRUCTOR = 3;

	/**
	 * 表明通过Bean的class的内部来自动装配.弃用
	 */
	@Deprecated
	int AUTOWIRE_AUTODETECT = 4;


	//-------------------------------------------------------------------------
	// 创建和填充外部Bean实例的典型方法
	//-------------------------------------------------------------------------

	/**
	 * 创建bean
	 */
	<T> T createBean(Class<T> beanClass) throws BeansException;

	/**
	 * <h3>使用autowrieBeanProperties装配属性</h3>
	 */
	void autowireBean(Object existingBean) throws BeansException;

	/**
	 * 自动装配属性,填充属性值,使用诸如setBeanName,setBeanFactory这样的工厂回调填充属性,最好还要用post processor
	 */
	Object configureBean(Object existingBean, String beanName) throws BeansException;


	//-------------------------------------------------------------------------
	// 在Bean的生命周期进行细力度控制的专门方法
	//-------------------------------------------------------------------------

	/**
	 * 会执行bean专门的初始化,包括BeanPostProcessors和initialzeBean
	 */
	Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 *
	 */
	Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 */
	void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
			throws BeansException;

	/**
	 */
	void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

	/**
	 */
	Object initializeBean(Object existingBean, String beanName) throws BeansException;

	/**
	 */
	Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException;

	/**
	 */
	Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException;

	/**
	 */
	void destroyBean(Object existingBean);


	//-------------------------------------------------------------------------
	// 委托方法,解决注入点
	//-------------------------------------------------------------------------

	/**
	 */
	<T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

	/**
	 */
	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;

	/**
	 */
	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
			@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;

}
