/*
 * Copyright 2002-2015 the original author or authors.
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

import org.springframework.lang.Nullable;

/**
 * 注册bean的别名,这些bean是共享的,单例的
 */
public interface SingletonBeanRegistry {

	/**
	 * 注册为一的bean,别名为beanName
	 */
	void registerSingleton(String beanName, Object singletonObject);

	/**
	 * 别获取唯一bean
	 */
	@Nullable
	Object getSingleton(String beanName);

	/**
	 * bean是否已经注册过别名
	 */
	boolean containsSingleton(String beanName);

	/**
	 * 获取所有已经注册的bean
	 */
	String[] getSingletonNames();

	/**
	 * 获取已经注册了的bean的数量
	 */
	int getSingletonCount();

	/**
	 * Return the singleton mutex used by this registry (for external collaborators).
	 * @return the mutex object (never {@code null})
	 * @since 4.2
	 */
	Object getSingletonMutex();

}
