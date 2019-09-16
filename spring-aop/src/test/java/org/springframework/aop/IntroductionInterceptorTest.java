package org.springframework.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatePerTargetObjectIntroductionInterceptor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;


public class IntroductionInterceptorTest {

	@Test
	public  void test1() {
		ProxyFactory factory = new ProxyFactory(new Person());
		factory.setProxyTargetClass(true);

		//此处采用IntroductionInterceptor 这个引介增强的拦截器
		Advice advice = new SomeInteIntroductionInterceptor();

		//切点+通知
		Advisor advisor = new DefaultIntroductionAdvisor((DynamicIntroductionAdvice) advice, IOtherInte.class);

		factory.addAdvisor(advisor);

		IOtherInte otherInte = (IOtherInte) factory.getProxy();
		otherInte.doOther("hello");
		System.out.println("---------------------------------------------------------");
		Person person = (Person) factory.getProxy();
		person.say("你好");
	}

	@Test
	public void test2(){
		ProxyFactory factory = new ProxyFactory(new Person());
		factory.setProxyTargetClass(true);

		//此处采用IntroductionInterceptor 这个引介增强的拦截器
		Advice advice = new DelegatePerTargetObjectIntroductionInterceptor(OtherImpl.class,IOtherInte.class);

		//切点+通知
		Advisor advisor = new DefaultIntroductionAdvisor((DynamicIntroductionAdvice) advice, IOtherInte.class);

		factory.addAdvisor(advisor);

		Person person = (Person) factory.getProxy();
		person.say("haha");

		IOtherInte iOtherInte = (IOtherInte) factory.getProxy();
		iOtherInte.doOther("other");

	}

	static class Person {
		public String say(String str) {
			System.out.println("say " + str);

			return "收到: " + str;
		}
	}

	interface IOtherInte {
		String doOther(String other);
	}

	static class SomeInteIntroductionInterceptor extends DelegatingIntroductionInterceptor implements IOtherInte {

		/*@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Object result;
			if (implementsInterface(invocation.getMethod().getDeclaringClass())) {
				System.out.println("引介增强 invoke");
				result = invocation.getMethod().invoke(this, invocation.getArguments());
			}else {
				result =  invocation.proceed();
			}
			System.out.println("[执行结果] " + result);
			return result;
		}

		@Override
		public boolean implementsInterface(Class<?> intf) {
			return intf.isAssignableFrom(IOtherInte.class);
		}*/

		@Override
		public String doOther(String other) {
			System.out.println("给人贴标签");

			return "收到 "+other;
		}
	}

	static class OtherImpl implements IOtherInte{

		@Override
		public String doOther(String other) {
			System.out.println(other);
			return "OtherImpl 收到 " + other;
		}
	}
}