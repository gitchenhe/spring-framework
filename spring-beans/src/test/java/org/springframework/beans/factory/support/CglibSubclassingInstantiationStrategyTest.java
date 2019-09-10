package org.springframework.beans.factory.support;

import org.junit.Test;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibSubclassingInstantiationStrategyTest {

	@Test
	public void enhancerTest() {
		//创建Enhance对象
		Enhancer enhancer = new Enhancer();
		//设置当前bean的class类型
		enhancer.setSuperclass(TestClass.class);
		//设置spring的命名策略
		enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
		enhancer.setCallback(new Cb());
		Object object = enhancer.create();
		System.out.println(object.toString());
	}


	public static class TestClass {
		public TestClass() {

		}
	}

	public static class Cb implements MethodInterceptor {
		public Cb(){
			System.out.println("初始化了");
		}

		@Override
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			System.out.println("调用方法前: " + method);
			Object result = methodProxy.invokeSuper(obj, args);
			System.out.println("调用方法后: " + method);
			return result;
		}
	}

}