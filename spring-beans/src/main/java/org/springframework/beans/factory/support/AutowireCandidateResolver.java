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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.lang.Nullable;

/**
 * 用于确定特定的bean define,是否符合特定的依赖项的候选者的策略接口
 */
public interface AutowireCandidateResolver {

	/**
	 * 判断给定的bean定义是否允许被依赖注入
	 */
	default boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
		return bdHolder.getBeanDefinition().isAutowireCandidate();
	}

	/**
	 * 给定的descriptor是否是必须的~~~
	 */
	default boolean isRequired(DependencyDescriptor descriptor) {
		return descriptor.isRequired();
	}

	/**
	 * 给出的默认值,是否是推荐的值
	 */
	@Nullable
	default Object getSuggestedValue(DependencyDescriptor descriptor) {
		return null;
	}

	/**
	 * Build a proxy for lazy resolution of the actual dependency target,
	 * if demanded by the injection point.
	 * <p>The default implementation simply returns {@code null}.
	 * @param descriptor the descriptor for the target method parameter or field
	 * @param beanName the name of the bean that contains the injection point
	 * @return the lazy resolution proxy for the actual dependency target,
	 * or {@code null} if straight resolution is to be performed
	 * @since 4.0
	 */
	@Nullable
	default Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
		return null;
	}

}
