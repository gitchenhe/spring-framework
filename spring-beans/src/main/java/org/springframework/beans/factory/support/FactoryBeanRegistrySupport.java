/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.beans.factory.support;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.lang.Nullable;

/**
 * 类的主要功能是,通过FactoryBean创建实例,并缓存FactoryBean与之创建的实例
 */
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {

	/**
	 * 存放由 factoryBean 创建的单例对象, FactoryBeanName -> Instance
	  */
	private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);


	/**
	 * factoryBean的类型
	 */
	@Nullable
	protected Class<?> getTypeForFactoryBean(final FactoryBean<?> factoryBean) {
		try {
			if (System.getSecurityManager() != null) {
				return AccessController.doPrivileged((PrivilegedAction<Class<?>>) factoryBean::getObjectType, getAccessControlContext());
			}
			else {
				return factoryBean.getObjectType();
			}
		}
		catch (Throwable ex) {
			// Thrown from the FactoryBean's getObjectType implementation.
			logger.warn("FactoryBean threw exception from getObjectType, despite the contract saying " +
					"that it should return null if the type of its object cannot be determined yet", ex);
			return null;
		}
	}

	/**
	 * 获取由 BeanFactory=beanName,创建的实例
	 */
	@Nullable
	protected Object getCachedObjectForFactoryBean(String beanName) {
		return this.factoryBeanObjectCache.get(beanName);
	}

	/**
	 * <h3>通过FactoryBean创建实例</h3>
	 *  本方法主要关注两点
	 *
	 * <pre>
	 * 1. 实例是否是单例的,如果是的话,先检查单例集合中是否存在,存在的话直接返回.
	 * 	  不存在的话,锁住单例集合,通过检查本地的factoryBeanObjectCache来检查是否有实例穿在
	 * 	  	如果存在,直接返回
	 * 	    如果不存在,通过FactoryBean创建实例,创建成功后,再次检查本地缓存factoryBeanObjectCache,实例是否被创建,
	 * 	    	如果已经创建,直接返回
	 * 	    	如果未创建,把上一步创建的实例缓存,并发布时间,调用实例创建后置程序
	 * 	2. 实例不是单例的, 每次都调用FactoryBean.getObject(),创建新实例,并调用 bean post process
	 * </pre>
	 *
	 */
	protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
		//是单例,并且实例已经被创建
		if (factory.isSingleton() && containsSingleton(beanName)) {
			//锁住单例集合
			synchronized (getSingletonMutex()) {
				Object object = this.factoryBeanObjectCache.get(beanName);

				//获取factoryBean尚未创建过实例
				if (object == null) {
					//使用factoryBean创建实例,实际上调用FactoryBean.getObject()
					object = doGetObjectFromFactoryBean(factory, beanName);

					//再次判断factoryBean是否已经创建过实例
					Object alreadyThere = this.factoryBeanObjectCache.get(beanName);

					if (alreadyThere != null) {
						//使用先创建的实例
						object = alreadyThere;
					}else {
						//如果需要发布bean创建后流程
						if (shouldPostProcess) {
							if (isSingletonCurrentlyInCreation(beanName)) {
								// 实例正在创建中,还未完全创建成功,比如:正在实例化循环引用的实例
								return object;
							}
							//如果实例不再创建中,就把实例加入到创建中队列
							beforeSingletonCreation(beanName);
							try {
								//发布bean实例创建事件
								object = postProcessObjectFromFactoryBean(object, beanName);
							}
							catch (Throwable ex) {
								throw new BeanCreationException(beanName,
										"Post-processing of FactoryBean's singleton object failed", ex);
							}
							finally {
								//从创建中集合移除实例
								afterSingletonCreation(beanName);
							}
						}
						//缓存实例
						if (containsSingleton(beanName)) {
							this.factoryBeanObjectCache.put(beanName, object);
						}
					}
				}
				return object;
			}
		}else {
			//对象不是单例的

			//创建实例
			Object object = doGetObjectFromFactoryBean(factory, beanName);

			//是否需要发布创建流程
			if (shouldPostProcess) {
				try {
					object = postProcessObjectFromFactoryBean(object, beanName);
				}
				catch (Throwable ex) {
					throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex);
				}
			}
			return object;
		}
	}

	/**
	 * 通过factory创建实例
	 */
	private Object doGetObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName)
			throws BeanCreationException {

		Object object;
		try {
			if (System.getSecurityManager() != null) {
				AccessControlContext acc = getAccessControlContext();
				try {
					object = AccessController.doPrivileged((PrivilegedExceptionAction<Object>) factory::getObject, acc);
				}
				catch (PrivilegedActionException pae) {
					throw pae.getException();
				}
			}
			else {
				//创建实例
				object = factory.getObject();
			}
		}
		catch (FactoryBeanNotInitializedException ex) {
			throw new BeanCurrentlyInCreationException(beanName, ex.toString());
		}
		catch (Throwable ex) {
			throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
		}

		// 实例未创建成功,返回NullBean
		// initialized yet: Many FactoryBeans just return null then.
		if (object == null) {
			if (isSingletonCurrentlyInCreation(beanName)) {
				throw new BeanCurrentlyInCreationException(
						beanName, "FactoryBean which is currently in creation returned null from getObject");
			}
			object = new NullBean();
		}
		return object;
	}

	/**
	 *
	 * 从FactoryBean创建的实例的后置处理程序.
	 * 生成的对象将暴露给bean引用
	 *
	 */
	protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
		return object;
	}

	/**
	 * 获取给定bean的FactoryBean
	 */
	protected FactoryBean<?> getFactoryBean(String beanName, Object beanInstance) throws BeansException {
		if (!(beanInstance instanceof FactoryBean)) {
			throw new BeanCreationException(beanName,
					"Bean instance of type [" + beanInstance.getClass() + "] is not a FactoryBean");
		}
		return (FactoryBean<?>) beanInstance;
	}

	/**
	 * Overridden to clear the FactoryBean object cache as well.
	 */
	@Override
	protected void removeSingleton(String beanName) {
		synchronized (getSingletonMutex()) {
			super.removeSingleton(beanName);
			this.factoryBeanObjectCache.remove(beanName);
		}
	}

	/**
	 * Overridden to clear the FactoryBean object cache as well.
	 */
	@Override
	protected void clearSingletonCache() {
		synchronized (getSingletonMutex()) {
			super.clearSingletonCache();
			this.factoryBeanObjectCache.clear();
		}
	}

	/**
	 * Return the security context for this bean factory. If a security manager
	 * is set, interaction with the user code will be executed using the privileged
	 * of the security context returned by this method.
	 * @see AccessController#getContext()
	 */
	protected AccessControlContext getAccessControlContext() {
		return AccessController.getContext();
	}

}
