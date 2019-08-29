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

package org.springframework.core;

/**
 * 定义了一些别名管理方法
 */
public interface AliasRegistry {

	/**
	 * 给name注册一个别名
	 */
	void registerAlias(String name, String alias);

	/**
	 * 移除别名
	 */
	void removeAlias(String alias);

	/**
	 * 判断name是否是一个别名
	 */
	boolean isAlias(String name);

	/**
	 * 获取name的所有别名
	 */
	String[] getAliases(String name);

}
