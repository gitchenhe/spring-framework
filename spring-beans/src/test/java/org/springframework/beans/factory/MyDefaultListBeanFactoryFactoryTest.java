package org.springframework.beans.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.Arrays;

/**
 * @author chenhe
 * @date 2019-09-10 15:41
 * @desc
 */
public class MyDefaultListBeanFactoryFactoryTest {

	protected static final Log logger = LogFactory.getLog(MyDefaultListBeanFactoryFactoryTest.class);

	@Test
	public void test1() {
		DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(Bean1.class);
		ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
		constructorArgumentValues.addIndexedArgumentValue(0,"参数1");
		constructorArgumentValues.addIndexedArgumentValue(1,"参数2");
		rootBeanDefinition.setConstructorArgumentValues(constructorArgumentValues);
		rootBeanDefinition.setDependsOn("bean2");
		defaultListableBeanFactory.registerBeanDefinition("bean1", rootBeanDefinition);


		RootBeanDefinition rootBeanDefinition2 = new RootBeanDefinition(Bean2.class);
		//rootBeanDefinition2.setScope(RootBeanDefinition.SCOPE_PROTOTYPE);
		defaultListableBeanFactory.registerBeanDefinition("bean2", rootBeanDefinition2);
		defaultListableBeanFactory.registerAlias("bean1","noInterfaceClass");

		Object object = defaultListableBeanFactory.getBean("bean1");
		logger.info("对象类型: " + object);

		logger.info("再次获取实例bean1,期望从缓存获取");
		defaultListableBeanFactory.getBean("noInterfaceClass");

		logger.info("----------------分割线--------------------");
		object = defaultListableBeanFactory.getBean("bean2");
		defaultListableBeanFactory.getBean("bean2");
		String interfaceNames = Arrays.asList(defaultListableBeanFactory.getBeanNamesForType(Bean2.class)).toString();
		logger.info("beanName: " + interfaceNames);
	}

	public static class Bean1 {

		public Bean1(String args1,String args2) {
			logger.info("普通Class初始化,参数:("+args1 + "," + args2+").");
		}
	}

	public interface Bean2Interface {
	}

	public static class Bean2 implements Bean2Interface{

		public Bean2() {
			logger.info("实现接口的Class初始化");
		}
	}
}
