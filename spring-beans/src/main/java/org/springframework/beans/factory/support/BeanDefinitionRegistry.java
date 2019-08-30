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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.AliasRegistry;

/**
 * bean 统一注册接口
 *
 * 将定义bean的资源文件,解析成BeanDefine后,需要将其注册到容器中,这个过程由BeanDefinitionRegistry来完成
 *
 */
public interface BeanDefinitionRegistry extends AliasRegistry {

	/**
	 * 向注册表中注册一个新的BeanDefine实例
	 */
	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
			throws BeanDefinitionStoreException;

	/**
	 * 移除注册表中指定的的BeanDefine实例
	 */
	void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * 获取指定的BeanDefine实例
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * 判断BeanDefine实例是否在注册表中
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 * 取得注册表中所有BeanDefine实例的beanName标识
	 */
	String[] getBeanDefinitionNames();

	/**
	 * 返回注册表中BeanDefine实例数量
	 */
	int getBeanDefinitionCount();

	/**
	 * 判断beanName是否被占用 包括被 普通的别名占用 和 BeanDefine 占用
	 */
	boolean isBeanNameInUse(String beanName);

}
