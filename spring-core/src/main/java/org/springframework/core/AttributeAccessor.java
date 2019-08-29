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

package org.springframework.core;

import org.springframework.lang.Nullable;

/**
 * 定义了最基本的对任意对象的元数据的修改或者获取
 */
public interface AttributeAccessor {

	/**
	 * 将name定义的属性设置为提供的value.
	 * 通常,用户应该注意通过使用完全限定的名称,来防止与其它元数据属性重复
	 */
	void setAttribute(String name, @Nullable Object value);

	/**
	 * 获取标识为name的属性
	 */
	@Nullable
	Object getAttribute(String name);

	/**
	 * 删除标识为name的属性
	 */
	@Nullable
	Object removeAttribute(String name);

	/**
	 * 名为name的属性是否存在
	 */
	boolean hasAttribute(String name);

	/**
	 * 返回所有属性的名称
	 */
	String[] attributeNames();

}
