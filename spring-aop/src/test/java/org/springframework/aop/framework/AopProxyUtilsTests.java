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

package org.springframework.aop.framework;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.springframework.aop.SpringProxy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * @author Rod Johnson
 * @author Chris Beams
 */
public class AopProxyUtilsTests {

	@Test
	public void testCompleteProxiedInterfacesWorksWithNull() {
		System.out.println("AdvisedSupport 默认会添加两个接口:Advised,SpringProxy");
		AdvisedSupport as = new AdvisedSupport();
		Class<?>[] completedInterfaces = AopProxyUtils.completeProxiedInterfaces(as);
		assertEquals(2, completedInterfaces.length);
		List<?> ifaces = Arrays.asList(completedInterfaces);
		assertTrue(ifaces.contains(Advised.class));
		assertTrue(ifaces.contains(SpringProxy.class));
	}

	@Test
	public void testCompleteProxiedInterfacesWorksWithNullOpaque() {
		System.out.println("opaque = true 后,不包含Advised接口");
		AdvisedSupport as = new AdvisedSupport();
		as.setOpaque(true);
		Class<?>[] completedInterfaces = AopProxyUtils.completeProxiedInterfaces(as);
		assertEquals(1, completedInterfaces.length);
	}

	@Test
	public void testCompleteProxiedInterfacesAdvisedNotIncluded() {
		AdvisedSupport as = new AdvisedSupport();
		as.addInterface(ITestBean.class);
		as.addInterface(Comparable.class);
		Class<?>[] completedInterfaces = AopProxyUtils.completeProxiedInterfaces(as);
		assertEquals(4, completedInterfaces.length);

		// Can't assume ordering for others, so use a list
		List<?> l = Arrays.asList(completedInterfaces);
		assertTrue(l.contains(Advised.class));
		assertTrue(l.contains(ITestBean.class));
		assertTrue(l.contains(Comparable.class));
	}

	@Test
	public void testCompleteProxiedInterfacesAdvisedIncluded() {
		AdvisedSupport as = new AdvisedSupport();
		as.addInterface(ITestBean.class);
		as.addInterface(Comparable.class);
		as.addInterface(Advised.class);
		Class<?>[] completedInterfaces = AopProxyUtils.completeProxiedInterfaces(as);
		assertEquals(4, completedInterfaces.length);

		// Can't assume ordering for others, so use a list
		List<?> l = Arrays.asList(completedInterfaces);
		assertTrue(l.contains(Advised.class));
		assertTrue(l.contains(ITestBean.class));
		assertTrue(l.contains(Comparable.class));
	}

	@Test
	public void testCompleteProxiedInterfacesAdvisedNotIncludedOpaque() {
		AdvisedSupport as = new AdvisedSupport();
		//排除掉Advised接口
		as.setOpaque(true);
		as.addInterface(ITestBean.class);
		as.addInterface(Comparable.class);
		Class<?>[] completedInterfaces = AopProxyUtils.completeProxiedInterfaces(as);
		assertEquals(3, completedInterfaces.length);

		// Can't assume ordering for others, so use a list
		List<?> l = Arrays.asList(completedInterfaces);
		assertFalse(l.contains(Advised.class));
		assertTrue(l.contains(ITestBean.class));
		assertTrue(l.contains(Comparable.class));
	}

	@Test
	public void testProxiedUserInterfacesWithSingleInterface() {
		ProxyFactory pf = new ProxyFactory();
		pf.setTarget(new TestBean());
		pf.addInterface(ITestBean.class);
		Object proxy = pf.getProxy();
		Class<?>[] userInterfaces = AopProxyUtils.proxiedUserInterfaces(proxy);
		assertEquals(1, userInterfaces.length);
		assertEquals(ITestBean.class, userInterfaces[0]);
	}

	@Test
	public void testProxiedUserInterfacesWithMultipleInterfaces() {
		ProxyFactory pf = new ProxyFactory();
		pf.setTarget(new TestBean());
		pf.addInterface(ITestBean.class);
		pf.addInterface(Comparable.class);
		Object proxy = pf.getProxy();
		Class<?>[] userInterfaces = AopProxyUtils.proxiedUserInterfaces(proxy);
		assertEquals(2, userInterfaces.length);
		assertEquals(ITestBean.class, userInterfaces[0]);
		assertEquals(Comparable.class, userInterfaces[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProxiedUserInterfacesWithNoInterface() {
		Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[0],
				(proxy1, method, args) -> null);
		AopProxyUtils.proxiedUserInterfaces(proxy);
	}

	@Test
	public void cgLibTest(){
		Enhancer enhancer = new Enhancer();
		enhancer.setClassLoader(getClass().getClassLoader());
		enhancer.setSuperclass(TestClass.class);
		enhancer.setCallback(new DefaultCallBack(new TestClass()));

		TestClass test = (TestClass) enhancer.create();
		test.say();
	}


	static class TestClass{
		public void say(){
			System.out.println("hello");
		}
	}

	static class DefaultCallBack implements MethodInterceptor {
		{
			System.out.println("初始化");
		}
		Object object;
		public DefaultCallBack(Object object){
		    this.object = object;
		}

		@Override
		public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
			System.out.println("开始调用了");
			return method.invoke(object,objects);
		}
	}

}
