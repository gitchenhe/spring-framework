package org.springframework.transaction.interceptor;

import org.junit.Test;
import org.springframework.aop.TrueClassFilter;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class TransactionProxyFactoryBeanTest {

	@Test
	public void test1() {
		Work work = new Work();
		DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
		defaultListableBeanFactory.registerSingleton("s", TrueClassFilter.class);
		defaultListableBeanFactory.preInstantiateSingletons();
		System.out.println((defaultListableBeanFactory.getBean("s")));
		System.out.println((defaultListableBeanFactory.getBean("s") instanceof TrueClassFilter));

		TransactionProxyFactoryBean factoryBean = new TransactionProxyFactoryBean();
		factoryBean.setBeanFactory(defaultListableBeanFactory);
	}

	public static class Work {
		public void doWork() {
			System.out.println("工作");
		}
	}

}