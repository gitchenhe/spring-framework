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

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.lang.Nullable;

/**
 * Bean的定义主要是由BeanDefintion来描述的,作为Spring中用于包装Bean的数据结构.
 *
 * BeanDefine描述了一个bean的实例,包括属性值,构造方法参数值,继承自它的类的更多信息.
 *
 * BeanDefine仅仅是一个接口,主要功能是允许BeanFactoryPostProcessor,例如:PropertyPlaceHolderConfigure
 * 能够检索并修改属性值和别的bean的元数据
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

	/**
	 * 作用于 - 单例,容器中只存在一个共享的实例
	 */
	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

	/**
	 * 作用于 - 原型,每次请求都会生成一个新的实例. 比如spring集成structs
	 */
	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;


	/**
	 * Role表示是应用程序的一部分,通常是用户自定义的bean
	 *
	 *
	 */
	int ROLE_APPLICATION = 0;

	/**
	 * ROLE_SUPPORT = 1 也就是说,这个bean是由用户自己定义的,是从配置文件中来的
	 */
	int ROLE_SUPPORT = 1;

	/**
	 * ROLE_INFRASTRUCTURE = 2 说明bean是Spring的,和用户没关系
	 */
	int ROLE_INFRASTRUCTURE = 2;


	// Modifiable attributes

	/**
	 * 如果父类存在,则设置这个bean定义的父定义的名称
	 */
	void setParentName(@Nullable String parentName);

	/**
	 */
	@Nullable
	String getParentName();

	/**
	 * 指定此bean定义的bean类名称
	 *
	 * 类名称可以在bean factory后期处理中修改,通常用它的解析变体,替换原来的名称
	 */
	void setBeanClassName(@Nullable String beanClassName);

	/**
	 */
	@Nullable
	String getBeanClassName();

	/**
	 */
	void setScope(@Nullable String scope);

	/**
	 */
	@Nullable
	String getScope();

	/**
	 */
	void setLazyInit(boolean lazyInit);

	/**
	 */
	boolean isLazyInit();

	/**
	 */
	void setDependsOn(@Nullable String... dependsOn);

	/**
	 */
	@Nullable
	String[] getDependsOn();

	/**
	 * 设置这个bean是否是获得自动装配到其他bean的候选人
	 * 需要注意的是,此标志旨在仅影响基于类型的自动装配
	 * 他不会影响按名称的自动装配,即使指定的beana没有标记为autowire候选,也可以解决这个问题
	 * 因此,如果名称匹配,通过名称的自动装配,将注入一个bean
	 *
	 */
	void setAutowireCandidate(boolean autowireCandidate);

	/**
	 * bean define 是否是自动注入的候选者
	 */
	boolean isAutowireCandidate();

	/**
	 * 是否为主候选bean,  使用注解@Primary
	 */
	void setPrimary(boolean primary);

	/**
	 */
	boolean isPrimary();

	/**
	 */
	void setFactoryBeanName(@Nullable String factoryBeanName);

	/**
	 * Return the factory bean name, if any.
	 */
	@Nullable
	String getFactoryBeanName();

	/**
	 */
	void setFactoryMethodName(@Nullable String factoryMethodName);

	/**
	 * Return a factory method, if any.
	 */
	@Nullable
	String getFactoryMethodName();

	/**
	 * 构造方法参数值
	 */
	ConstructorArgumentValues getConstructorArgumentValues();

	/**
	 * 构造方法是否有参数
	 */
	default boolean hasConstructorArgumentValues() {
		return !getConstructorArgumentValues().isEmpty();
	}

	/**
	 * 获取普通属性集合
	 */
	MutablePropertyValues getPropertyValues();

	/**
	 */
	default boolean hasPropertyValues() {
		return !getPropertyValues().isEmpty();
	}


	// 只读属性

	/**
	 * 是否是单例
	 */
	boolean isSingleton();

	/**
	 * 是否是原型
	 */
	boolean isPrototype();

	/**
	 * 是否是抽象
	 */
	boolean isAbstract();

	/**
	 * 上面的role
	 */
	int getRole();

	/**
	 * 描述?
	 */
	@Nullable
	String getDescription();

	/**
	 * 描述什么
	 */
	@Nullable
	String getResourceDescription();

	/**
	 */
	@Nullable
	BeanDefinition getOriginatingBeanDefinition();

}
