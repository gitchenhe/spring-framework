package org.springframework.web.servlet.my;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletConfig;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author chenhe
 * @date 2019-09-18 11:06
 * @desc
 */
public class HandlerTest {

	static Logger logger = LoggerFactory.getLogger(MyHandler.class);

	public static final String CONF = "/org/springframework/web/servlet/handler/map1.xml";

	private ConfigurableWebApplicationContext wac;


	public void setUp() throws Exception {
		MockServletContext sc = new MockServletContext("");
		wac = new XmlWebApplicationContext();
		wac.setServletContext(sc);
		wac.setConfigLocations(new String[] {CONF});
		wac.refresh();
	}

	private StaticWebApplicationContext staticWebApplicationContext;

	@Test
	public void before() throws Exception {

		staticWebApplicationContext = new StaticWebApplicationContext();
		staticWebApplicationContext.registerSingleton("MyHandler",MyHandler.class);
		staticWebApplicationContext.registerAlias("MyHandler","/myHandler.do");
		staticWebApplicationContext.registerSingleton("handlerMapping",BeanNameUrlHandlerMapping.class);
		staticWebApplicationContext.registerSingleton("defaultInterceptor",MyHandlerInterceptor.class);
		staticWebApplicationContext.refresh();

		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.init();


		BeanNameUrlHandlerMapping handlerMapping = (BeanNameUrlHandlerMapping) staticWebApplicationContext.getBean("handlerMapping");
		handlerMapping.setInterceptors(staticWebApplicationContext.getBean("defaultInterceptor"));

		MockHttpServletRequest request = new MockHttpServletRequest("GET","/myHandler.do");
		request.addParameter("name","张三");
		HandlerExecutionChain handler = handlerMapping.getHandler(request);
		//System.out.println(handler.getInterceptors().length);
		Controller controller = (Controller) handler.getHandler();

		HttpServletResponse response = new MockHttpServletResponse();
		ModelAndView modelAndView = controller.handleRequest(request,response);
		System.out.println(modelAndView.getModel().get("name"));
		System.out.println(modelAndView.getModel().get("sessionId"));

		dispatcherServlet.service(request,response);
	}

	@Test
	public void testDisPatcherServlet() throws ServletException, IOException {
		MockServletConfig servletConfig = new MockServletConfig(new MockServletContext(), "simple");
		servletConfig.addInitParameter("publishContext", "false");
		servletConfig.addInitParameter("class", "notWritable");
		servletConfig.addInitParameter("unknownParam", "someValue");

		//配置DispatcherServlet
		DispatcherServlet simpleDispatcherServlet = new DispatcherServlet();
		//自定义WebApplicationContext
		simpleDispatcherServlet.setContextClass(MyWebApplicationContext.class);

		simpleDispatcherServlet.init(servletConfig);

		//构建请求与相应
		MockHttpServletRequest request = new MockHttpServletRequest("GET","/myHandler.do");
		request.addParameter("name","张三");
		HttpServletResponse response = new MockHttpServletResponse();

		//请求
		simpleDispatcherServlet.service(request,response);


		System.out.println("响应码: "+ response.getStatus());
		System.out.println("相应信息: " + ((MockHttpServletResponse) response).getContentAsString());

	}

	@Test
	public void test() throws Exception {
		HandlerMapping handlerMapping = (HandlerMapping) wac.getBean("handlerMapping");
		MockHttpServletRequest request = new MockHttpServletRequest("GET","/myHandler.do");
		HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
		Controller controller = (Controller) handlerExecutionChain.getHandler();
		HttpServletResponse response = new MockHttpServletResponse();
		ModelAndView modelAndView = controller.handleRequest(request,response);
		System.out.println(modelAndView.getModel().get("name"));
		System.out.println(handlerExecutionChain.getHandler() instanceof MyHandler);

	}


	public static class MyHandlerInterceptor implements HandlerInterceptor{

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			logger.info("拦截器] preHandle");
			return false;
		}

		@Override
		public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
			logger.info("拦截器] postHandle");
		}

		@Override
		public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
			logger.info("拦截器] afterCompletion");
		}
	}

	/**
	 * controller
	 */
	public static class MyHandler implements Controller {
		@Override
		public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
			logger.info("处理请求");
			ModelAndView modelAndView = new ModelAndView();

			modelAndView.addObject("name", request.getParameter("name"));
			modelAndView.addObject("sessionId",request.getSession().getId());

			modelAndView.setViewName("chenhe");
			return modelAndView;
		}
	}

	public static class MyWebApplicationContext extends StaticWebApplicationContext {
		@Override
		public void refresh() throws BeansException, IllegalStateException {
			registerSingleton("myHandler",MyHandler.class);
			registerAlias("myHandler","/myHandler.do");
			registerSingleton("viewResolver", BeanNameViewResolver.class);
			registerSingleton("viewResolver2", InternalResourceViewResolver.class);
			//registerSingleton("",)
			super.refresh();
		}
	}
}
