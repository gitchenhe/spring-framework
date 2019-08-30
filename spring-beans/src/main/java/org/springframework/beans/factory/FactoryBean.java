/*
 * Copyright 2002-2016 the original author or authors.
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

import org.springframework.lang.Nullable;

/**
 * <h3>FactoryBean并不是简单的Bean,而是可以产生或修饰 由BeanFactory产生的实例的Bean</h3>
 * Spring中所有的实例都是由BeanFactory创建出来的, 而 FactoryBean可以在BeanFactory实例的基础上做修改.<br>
 * 从而产生一个新的或修饰对象生成的工厂Bean <br>
 *
 * BeanFactory创建的对象,实际上是由FactoryBean的getObject(),返回的对象
 *
 */
public interface FactoryBean<T> {

	/**
	 * 返回当前工厂创建的实例
	 */
	@Nullable
	T getObject() throws Exception;

	/**
	 * <h3>返回工厂创建的对象的类型</h3>
	 *
	 * <p>这里可以用于检查一个 类型 有没有实例化的对象,例如在自动装配的时候</p>
	 */
	@Nullable
	Class<?> getObjectType();

	/**
	 * 如果当前工厂创建的实例是单例的,可以缓存实例.
	 */
	default boolean isSingleton() {
		return true;
	}

}
