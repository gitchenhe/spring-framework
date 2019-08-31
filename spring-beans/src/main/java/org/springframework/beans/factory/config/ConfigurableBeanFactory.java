/*
 * Copyright 2002-2018 the original author or authors.
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

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * <h3>ConfigurableBeanFactory 定义BeanFactory的配置, ConfigurableBeanFactory 定义了太多太多的api,比如 类加载器,类型转换器,属性编辑器,
 * BeanPostProcessor,作用于Bean定义,处理bean依赖关系,合并其他ConfigurableBeanFactory,bean如何销毁.</h3>
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

	/**
	 * 单例
	 */
	String SCOPE_SINGLETON = "singleton";

	/**
	 * 原型
	 */
	String SCOPE_PROTOTYPE = "prototype";


	/**
	 * 设置当前facory的父factory
	 */
	void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

	/**
	 * 设置类加载器,默认为上下门环境的类加载器
	 */
	void setBeanClassLoader(@Nullable ClassLoader beanClassLoader);

	/**
	 * 获取类加载器
	 */
	@Nullable
	ClassLoader getBeanClassLoader();

	/**
	 * @todo 不太理解
	 *
	 */
	void setTempClassLoader(@Nullable ClassLoader tempClassLoader);

	/**
	 */
	@Nullable
	ClassLoader getTempClassLoader();

	/**
	 * 设置是否缓存bean的元数据
	 */
	void setCacheBeanMetadata(boolean cacheBeanMetadata);

	/**
	 * 是否缓存bean的元数据
	 */
	boolean isCacheBeanMetadata();

	/**
	 * 设置特殊表达式(EL表达式等)的解析器
	 */
	void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver);

	/**
	 * 获取表达式解析器
	 */
	@Nullable
	BeanExpressionResolver getBeanExpressionResolver();

	/**
	 * 设置类转换器
	 */
	void setConversionService(@Nullable ConversionService conversionService);

	/**
	 * 获取类转换器
	 */
	@Nullable
	ConversionService getConversionService();

	/**
	 * 添加属性编辑器提供者,应用于bean创建的所有过程中
	 * 比如: 属性编辑器实例创建并注册到 注册器中.
	 */
	void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

	/**
	 * 特定类型的属性编辑器
	 */
	void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

	/**
	 * 初始化给定属性编辑器和自定义注册表编辑器,在这个bean注册工厂
	 */
	void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

	/**
	 * 类型转换器
	 */
	void setTypeConverter(TypeConverter typeConverter);

	/**
	 * 获取类型转换器
	 */
	TypeConverter getTypeConverter();

	/**
	 * 字符串resolver,应该类似占位符解析
	 */
	void addEmbeddedValueResolver(StringValueResolver valueResolver);

	/**
	 * 是否有string value resolver
	 */
	boolean hasEmbeddedValueResolver();

	/**
	 * 根据value,获取实际值
	 */
	@Nullable
	String resolveEmbeddedValue(String value);

	/**
	 * 添加一个bean后置处理程序
	 */
	void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

	/**
	 * bean后置处理程序数量
	 */
	int getBeanPostProcessorCount();

	/**
	 *注册Scope
	 */
	void registerScope(String scopeName, Scope scope);

	/**
	 * 获取已经注册的scopeNames
	 */
	String[] getRegisteredScopeNames();

	/**
	 * 根据scopeName获取scope实例
	 */
	@Nullable
	Scope getRegisteredScope(String scopeName);

	/**
	 * 访问控制
	 */
	AccessControlContext getAccessControlContext();

	/**
	 * 复制所有相关配置给其它工厂
	 */
	void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

	/**
	 * 给定一个beanName,注册别名
	 */
	void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

	/**
	 * 别名解析器
	 */
	void resolveAliases(StringValueResolver valueResolver);

	/**
	 * @todo 不太明白
	 */
	BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * 是否是 FactoryBean
	 */
	boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 显式地控制指定的bean的创作现状。
	 * @todo 这又是神马
	 */
	void setCurrentlyInCreation(String beanName, boolean inCreation);

	/**
	 * Determine whether the specified bean is currently in creation.
	 * @param beanName the name of the bean
	 * @return whether the bean is currently in creation
	 * @since 2.5
	 */
	boolean isCurrentlyInCreation(String beanName);

	/**
	 * 根据name,注册依赖的bean
	 */
	void registerDependentBean(String beanName, String dependentBeanName);

	/**
	 * 获取依赖的Bena
	 */
	String[] getDependentBeans(String beanName);

	/**
	 * 依赖beanName的bean
	 */
	String[] getDependenciesForBean(String beanName);

	/**
	 * 销毁bean的实例
	 */
	void destroyBean(String beanName, Object beanInstance);

	/**
	 * 销毁scope的bean
	 */
	void destroyScopedBean(String beanName);

	/**
	 * 销毁所有单例的bean
	 */
	void destroySingletons();

}
