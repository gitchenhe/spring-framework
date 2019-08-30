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

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * <h2>定义了一个工厂,返回一个实例对象</h2>
 * <p>通常使用这个接口来封装一个返回一个新实例的通用工厂(原型)的一些目标对象在每次调用.
 *
 */
@FunctionalInterface
public interface ObjectFactory<T> {

	/**
	 * 返回对象实例
	 */
	T getObject() throws BeansException;

}
