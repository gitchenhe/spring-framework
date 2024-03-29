/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.aop;

/**
 * <h1>引介信息</h1>
 *
 * 提供描述引言所需信息的接口
 *
 * IntroductionAdvisor必须实现这个接口。若`org.aopalliance.aop.Advice`直接实现了此接口，
 *
 * 它可议独立的当作introduction来使用而不用依赖IntroductionAdvisor。这种情况下，这个advice可议自描述，不仅提供。。。
 */
public interface IntroductionInfo {

	/**
	 * 返回额外给Advisor 或者 advice实现的接口们
	 */
	Class<?>[] getInterfaces();

}
