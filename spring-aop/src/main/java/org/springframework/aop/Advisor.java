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

package org.springframework.aop;

import org.aopalliance.aop.Advice;

/**
 * Base interface holding AOP <b>advice</b> (action to take at a joinpoint)
 * and a filter determining the applicability of the advice (such as
 * a pointcut). <i>This interface is not for use by Spring users, but to
 * allow for commonality in support for different types of advice.</i>
 *
 * <p>Spring AOP is based around <b>around advice</b> delivered via method
 * <b>interception</b>, compliant with the AOP Alliance interception API.
 * The Advisor interface allows support for different types of advice,
 * such as <b>before</b> and <b>after</b> advice, which need not be
 * implemented using interception.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface Advisor {

	/**
	 * @since 5.0 Spring5以后才有的  空通知  一般当作默认值
	 */
	Advice EMPTY_ADVICE = new Advice() {};


	/**
	 * 该Advisor 持有的通知器
	 */
	Advice getAdvice();

	/**
	 * 这个有点意思：Spring所有的实现类都是return true(官方说暂时还没有应用到)
	 * 注意：生成的Advisor是单例还是多例不由isPerInstance()的返回结果决定，而由自己在定义bean的时候控制
	 * 理解：和类共享（per-class）或基于实例（per-instance）相关  类共享：类比静态变量   实例共享：类比实例变量
	 */
	boolean isPerInstance();

}
