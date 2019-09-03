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

package org.springframework.core.convert.converter;

/**
 * 注册类型转换器
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface ConverterRegistry {

	/**
	 * 这个注册表添加一个简单的转换。可转换的源/目标类型 来源于转换器的参数化类型。
	 */
	void addConverter(Converter<?, ?> converter);

	/**
	 * 指定两种类型转换的转换器
	 */
	<S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<? super S, ? extends T> converter);

	/**
	 * 添加通用转换器
	 */
	void addConverter(GenericConverter converter);

	/**
	 * 可转换的源/目标类型对, 来源于工厂的参数化类型。
	 */
	void addConverterFactory(ConverterFactory<?, ?> factory);

	/**
	 * 移除类型转换
	 */
	void removeConvertible(Class<?> sourceType, Class<?> targetType);

}
