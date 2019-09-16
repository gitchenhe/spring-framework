package org.springframework.aop.framework;

import com.sun.tools.internal.xjc.generator.bean.DualObjectFactoryGenerator;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;


public class DefaultAopProxyFactoryTest {

	@Test
	public void test1() throws Throwable {
		Person person = new Person();
		Dog dog = new Dog();

		AdvisedSupport support = new AdvisedSupport();
		//support.setTarget(dog);

		DefaultAopProxyFactory defaultAopProxyFactory = new DefaultAopProxyFactory();
		//AopProxy aopProxy = defaultAopProxyFactory.createAopProxy(support);

		/*Dog dogProxy = (Dog) aopProxy.getProxy();
		dogProxy.walk();
		System.out.println("对象类型: " + dogProxy);

		System.out.println("是代理方法:" + AopUtils.isCglibProxy(dogProxy));*/

		support.setTarget(person);
		support.setExposeProxy(true);
		support.setInterfaces(ClassUtils.getAllInterfaces(person));
		support.setTargetClass(Person.class);


	}

	interface Animal{
		void walk();
	}

	static class Person implements Animal{

		@Override
		public void walk() {
			System.out.println("人行走");
		}
	}

	static class Dog {
		public void walk() {
			System.out.println("狗跑");
		}
	}
}