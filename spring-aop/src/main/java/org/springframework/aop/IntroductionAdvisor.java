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
 * IntroductionAdvisor 只能用于类级别的拦截 <br>
 *
 * 引介切面<br>
 *
 * Spring 有5种增强:前置(BeforeAdvice),后置(AfterAdvice),异常增强(ThrowsAdvice),
 * 环绕增强(RoundAdvice),引入增强(IntroductionAdvice)<br>
 *
 * 引入增强（Introduction Advice）的概念: 一个类,没有实现A接口,在不修改代码的基础上,使其具备A接口的功能<br>
 *
 * 为了更好的了解IntroductionAdvisor，我先有必要讲解下IntroductionInfo和IntroductionInterceptor；<br>
 *
 */
public interface IntroductionAdvisor extends Advisor, IntroductionInfo {

	/**
	 *
	 * 它只有class filter,因为它只能作用在类层面上
	 * Return the filter determining which target classes this introduction
	 * should apply to.
	 * <p>This represents the class part of a pointcut. Note that method
	 * matching doesn't make sense to introductions.
	 * @return the class filter
	 */
	ClassFilter getClassFilter();


	/**
	 * 判断这些接口,是否真的能够增强,
	 * Can the advised interfaces be implemented by the introduction advice?
	 * Invoked before adding an IntroductionAdvisor.
	 * @throws IllegalArgumentException if the advised interfaces can't be
	 * implemented by the introduction advice
	 */
	void validateInterfaces() throws IllegalArgumentException;

}
