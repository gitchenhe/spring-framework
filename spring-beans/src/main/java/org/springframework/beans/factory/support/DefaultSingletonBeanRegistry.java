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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.core.SimpleAliasRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <h3>单例创建和销毁,登记实例之间的相互依赖关系</h3>
 *
 * 可以存放单例对象 和 构建单例对象的工厂.
 * <br>
 * 实例存在的时候,会移除它的工厂实例
 *
 * <pre>
 * 1.构造单例对象,缓存所有单例集合
 * 2.实例依赖的所有实例集合
 * 3.所有依赖实例的集合
 * </pre>
 * @author
 */
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {

	/**
	 * 缓存单例对象
	 * name - instance
	 */
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

	/**
	 * 缓存,制造 singletonObjects 的工厂
	 *
	 */
	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

	/** 早期bean实例: bean name --> bean instance */
	private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

	/** 单例 bean的注册表,是有序的 */
	private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

	/** 正在创建的bean name的集合 */
	private final Set<String> singletonsCurrentlyInCreation =
			Collections.newSetFromMap(new ConcurrentHashMap<>(16));

	/** 检查正在创建bean时,出去该集合中的bean */
	private final Set<String> inCreationCheckExclusions =
			Collections.newSetFromMap(new ConcurrentHashMap<>(16));

	/** 用来存储异常 */
	@Nullable
	private Set<Exception> suppressedExceptions;

	/** 当前是否有singleton被销毁 */
	private boolean singletonsCurrentlyInDestruction = false;

	/** bean对应的DisposableBean， DisposableBean接口有一个destroy()。为bean指定DisposableBean,作用类似于设置destroy-method */
	private final Map<String, Object> disposableBeans = new LinkedHashMap<>();

	/** bean包含关系的缓存：key = beanName, value = 它包含的所有bean的 name 的集合  */
	private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap<>(16);

	/** bean依赖关系的缓存： beanName -> 依赖他的所有bean (value中的bean要先于key中的bean销毁) */
	private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);

	/** bean依赖关系的缓存：bean name 对应 该bean依赖的所有bean的name */
	private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);


	@Override
	public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
		Assert.notNull(beanName, "Bean name must not be null");
		Assert.notNull(singletonObject, "Singleton object must not be null");

		synchronized (this.singletonObjects) {
			Object oldObject = this.singletonObjects.get(beanName);
			//name对应的实例已经存在
			if (oldObject != null) {
				throw new IllegalStateException("Could not register object [" + singletonObject +
						"] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
			}
			//添加到容器
			addSingleton(beanName, singletonObject);
		}
	}

	/**
	 * <h3>添加实例对象到容器</h3>
	 * 直接缓存.不会检查实例是否存在
	 * <ol>
	 *     <li>缓存单例对象</li>
	 *     <li>缓存 beanName 与 singletonObject的关系</li>
	 *     <li>移除单例对象对应的工厂和	earlySingleton(因为实例对象已经存在,不再需要工厂)</li>
	 * </ol>
	 */
	protected void addSingleton(String beanName, Object singletonObject) {
		synchronized (this.singletonObjects) {
			//缓存 beanName 与 singletonObject的关系
			this.singletonObjects.put(beanName, singletonObject);

			//已经存在该单例bean的实例，因此对应的工厂和earlySingleton不再需要
			this.singletonFactories.remove(beanName);
			this.earlySingletonObjects.remove(beanName);

			//缓存实例
			this.registeredSingletons.add(beanName);
		}
	}

	/**
	 * <h3>添加制造单例对象的工厂</h3>
	 * <p>如果实例已存在,不会存SingleFactory</p>
	 */
	protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(singletonFactory, "Singleton factory must not be null");

		synchronized (this.singletonObjects) {
			//如果实例已经存在,不需要添加 ObjectFactory
			if (!this.singletonObjects.containsKey(beanName)) {
				this.singletonFactories.put(beanName, singletonFactory);
				this.earlySingletonObjects.remove(beanName);
				this.registeredSingletons.add(beanName);
			}
		}
	}

	/**
	 * <h3>根据bean name 获取实例</h3>
	 * <pre>如果实例不存在,会调用ObjectFactory.getObject(),创建实例并返回.</pre>
	 *
	 * @param beanName
	 * @return
	 */
	@Override
	@Nullable
	public Object getSingleton(String beanName) {
		return getSingleton(beanName, true);
	}

	/**
	 * <h3>获取实例对象</h3>
	 * <pre>
	 *     1.实例对象已经存在 直接返回.
	 *     2.实例对象不存在,并且beanName正在创建中
	 *     	 获取锁(锁可起到等待wait作用,等待创建完成的)
	 *     	 成功获取到锁,如果实例仍然为空.
	 *     	 	如果 allowEarlyReference = true,
	 *     	 		那会调用ObjectFactory.getObject(),创建一个对象,并加到容器中
	 * </pre>
	 * @param beanName
	 * @param allowEarlyReference 如果实例不存在,是否允许使用 ObjectFactory.getObject(),创建实例.
	 */
	@Nullable
	protected Object getSingleton(String beanName, boolean allowEarlyReference) {
		Object singletonObject = this.singletonObjects.get(beanName);

		//实例对象不存在,并且实例正在构建中
		if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
			synchronized (this.singletonObjects) {
				//再次判断实例是否已经存在
				singletonObject = this.earlySingletonObjects.get(beanName);

				//对象未创建.并且 允许早起的引用
				if (singletonObject == null && allowEarlyReference) {
					//获取对象工厂类
					ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
					if (singletonFactory != null) {
						//通过工厂生产实例
						singletonObject = singletonFactory.getObject();
						//缓存创建的实例
						this.earlySingletonObjects.put(beanName, singletonObject);
						//移除工厂
						this.singletonFactories.remove(beanName);
					}
				}
			}
		}
		return singletonObject;
	}

	/**
	 * <h3>获取实例,如果不存在,就用singletonFactory.getObject()创建</h3>
	 */
	public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(beanName, "Bean name must not be null");

		synchronized (this.singletonObjects) {
			//获取实例对象
			Object singletonObject = this.singletonObjects.get(beanName);
			//实例对象不存在
			if (singletonObject == null) {
				//当前正在有实例在销毁
				if (this.singletonsCurrentlyInDestruction) {
					throw new BeanCreationNotAllowedException(beanName,
							"Singleton bean creation not allowed while singletons of this factory are in destruction " +
							"(Do not request a bean from a BeanFactory in a destroy method implementation!)");
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
				}
				//将beanName加入到容器中,标识实例正在创建中
				beforeSingletonCreation(beanName);
				boolean newSingleton = false;
				boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
				if (recordSuppressedExceptions) {
					this.suppressedExceptions = new LinkedHashSet<>();
				}

				//创建实例
				try {
					singletonObject = singletonFactory.getObject();
					newSingleton = true;
				}catch (IllegalStateException ex) {
					//出异常了,并且实例没有创建成功,抛出异常
					singletonObject = this.singletonObjects.get(beanName);
					if (singletonObject == null) {
						throw ex;
					}
				}catch (BeanCreationException ex) {
					if (recordSuppressedExceptions) {
						for (Exception suppressedException : this.suppressedExceptions) {
							ex.addRelatedCause(suppressedException);
						}
					}
					throw ex;
				}
				finally {
					if (recordSuppressedExceptions) {
						this.suppressedExceptions = null;
					}
					afterSingletonCreation(beanName);
				}
				//是新创建的实例
				if (newSingleton) {
					addSingleton(beanName, singletonObject);
				}
			}
			return singletonObject;
		}
	}

	/**
	 * 注册一个例外发生在得到抑制在一个单例bean实例的创建,
	 * 例如一个临时解决循环引用问题。
	 */
	protected void onSuppressedException(Exception ex) {
		synchronized (this.singletonObjects) {
			if (this.suppressedExceptions != null) {
				this.suppressedExceptions.add(ex);
			}
		}
	}

	/**
	 * 移除单例,
	 */
	protected void removeSingleton(String beanName) {
		synchronized (this.singletonObjects) {
			this.singletonObjects.remove(beanName);
			this.singletonFactories.remove(beanName);
			this.earlySingletonObjects.remove(beanName);
			this.registeredSingletons.remove(beanName);
		}
	}

	/**
	 * 单例是否存在
	 * @param beanName
	 * @return
	 */
	@Override
	public boolean containsSingleton(String beanName) {
		return this.singletonObjects.containsKey(beanName);
	}

	@Override
	public String[] getSingletonNames() {
		synchronized (this.singletonObjects) {
			return StringUtils.toStringArray(this.registeredSingletons);
		}
	}

	@Override
	public int getSingletonCount() {
		synchronized (this.singletonObjects) {
			return this.registeredSingletons.size();
		}
	}

	/**
	 * beanName 是否在创建中
	 * @param beanName
	 * @param inCreation true: 正在创建中; false: 不再创建中
	 */
	public void setCurrentlyInCreation(String beanName, boolean inCreation) {
		Assert.notNull(beanName, "Bean name must not be null");
		if (!inCreation) {
			this.inCreationCheckExclusions.add(beanName);
		}
		else {
			this.inCreationCheckExclusions.remove(beanName);
		}
	}

	/**
	 * beanName 实例是否在创建中
	 * @param beanName
	 * @return
	 */
	public boolean isCurrentlyInCreation(String beanName) {
		Assert.notNull(beanName, "Bean name must not be null");
		return (!this.inCreationCheckExclusions.contains(beanName) && isActuallyInCreation(beanName));
	}

	/**
	 * 检查创建中实例列表,返回是否正在创建中
	 * @param beanName
	 * @return
	 */
	protected boolean isActuallyInCreation(String beanName) {
		return isSingletonCurrentlyInCreation(beanName);
	}

	/**
	 * beanName的实例是否正在构建中
	 */
	public boolean isSingletonCurrentlyInCreation(String beanName) {
		return this.singletonsCurrentlyInCreation.contains(beanName);
	}

	/**
	 * <h3>实例对象创建前调用</h3>
	 * 将beanName,加入到正在创建的实例容器中,如果实例正在创建中,会抛出异常
	 */
	protected void beforeSingletonCreation(String beanName) {
		if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
			//对象重复创建,出现了并发问题
			throw new BeanCurrentlyInCreationException(beanName);
		}
	}

	/**
	 * <h3>实例创建完成后调用</h3>
	 * 从正创建中的实例容器中移除beanName
	 */
	protected void afterSingletonCreation(String beanName) {
		if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
			throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
		}
	}


	/**
	 * <h3>注册一次性实例</h3>
	 */
	public void registerDisposableBean(String beanName, DisposableBean bean) {
		synchronized (this.disposableBeans) {
			this.disposableBeans.put(beanName, bean);
		}
	}

	/**
	 * <h3>注册包含关系的bean</h3>
	 * containingBeanName 依赖 containedBeanName
	 *
	 *
	 */
	public void registerContainedBean(String containedBeanName, String containingBeanName) {
		synchronized (this.containedBeanMap) {
			//如果containedBeanMap不为空,就把 key = containingBeanName,value = k 存到 containedBeanMap
			Set<String> containedBeans = this.containedBeanMap.computeIfAbsent(containingBeanName, k -> new LinkedHashSet<>(8));

			if (!containedBeans.add(containedBeanName)) {
				//已经存在 containingBeanName 依赖 containedBeanName 的关系
				return;
			}
		}
		//依赖关系不存在 或 本次是第一次添加依赖关系
		//添加依赖关系
		registerDependentBean(containedBeanName, containingBeanName);
	}

	/**
	 * <h3>添加被依赖关系</h3>
	 * <p>
	 *     beanName 被 dependentBeanName 依赖
	 *     <br/>
	 *     目的是为了,bean销毁的时候,先销毁被依赖的对象
	 * </p>
	 */
	public void registerDependentBean(String beanName, String dependentBeanName) {
		//获取真实名字,为何这里突然用了真实名字?
		String canonicalName = canonicalName(beanName);

		synchronized (this.dependentBeanMap) {

			Set<String> dependentBeans = this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
			//beanName 被 dependentBeanName 依赖
			if (!dependentBeans.add(dependentBeanName)) {
				return;
			}
		}

		//dependentBeanName 依赖 beanName(非别名)
		synchronized (this.dependenciesForBeanMap) {
			Set<String> dependenciesForBean = this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
			dependenciesForBean.add(canonicalName);
		}
	}

	/**
	 * <h3>beanName 是否 被 dependentBeanName 依赖(包含传递依赖)</h3>
	 */
	protected boolean isDependent(String beanName, String dependentBeanName) {
		synchronized (this.dependentBeanMap) {
			return isDependent(beanName, dependentBeanName, null);
		}
	}

	/**
	 * beanName 是否被 dependentBeanName 依赖. 包含传递依赖(A -> B -> C)
	 * @param beanName
	 * @param dependentBeanName
	 * @param alreadySeen  递归调用的时候使用到
	 * @return
	 */
	private boolean isDependent(String beanName, String dependentBeanName, @Nullable Set<String> alreadySeen) {
		if (alreadySeen != null && alreadySeen.contains(beanName)) {
			return false;
		}
		//真实名字
		String canonicalName = canonicalName(beanName);
		//依赖beanName的所有 bean
		Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
		if (dependentBeans == null) {
			return false;
		}
		if (dependentBeans.contains(dependentBeanName)) {
			return true;
		}

		//传递依赖
		for (String transitiveDependency : dependentBeans) {
			if (alreadySeen == null) {
				alreadySeen = new HashSet<>();
			}
			alreadySeen.add(beanName);
			if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <h3>是否有依赖 beanName 的实例</h3>
	 */
	protected boolean hasDependentBean(String beanName) {
		return this.dependentBeanMap.containsKey(beanName);
	}

	/**
	 * <h3>所有依赖beanName的实例 </h3>
	 */
	public String[] getDependentBeans(String beanName) {
		Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
		if (dependentBeans == null) {
			return new String[0];
		}
		synchronized (this.dependentBeanMap) {
			return StringUtils.toStringArray(dependentBeans);
		}
	}

	/**
	 * <h3>beanName依赖的所有对象</h3>
	 */
	public String[] getDependenciesForBean(String beanName) {
		Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
		if (dependenciesForBean == null) {
			return new String[0];
		}
		synchronized (this.dependenciesForBeanMap) {
			return StringUtils.toStringArray(dependenciesForBean);
		}
	}

	/**
	 * <h3>单例销毁</h3>
	 */
	public void destroySingletons() {
		if (logger.isDebugEnabled()) {
			logger.debug("Destroying singletons in " + this);
		}

		synchronized (this.singletonObjects) {
			this.singletonsCurrentlyInDestruction = true;
		}

		String[] disposableBeanNames;
		synchronized (this.disposableBeans) {
			disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
		}
		for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
			destroySingleton(disposableBeanNames[i]);
		}

		this.containedBeanMap.clear();
		this.dependentBeanMap.clear();
		this.dependenciesForBeanMap.clear();

		clearSingletonCache();
	}

	/**
	 * Clear all cached singleton instances in this registry.
	 * @since 4.3.15
	 */
	protected void clearSingletonCache() {
		synchronized (this.singletonObjects) {
			this.singletonObjects.clear();
			this.singletonFactories.clear();
			this.earlySingletonObjects.clear();
			this.registeredSingletons.clear();
			this.singletonsCurrentlyInDestruction = false;
		}
	}

	/**
	 * <h3>单例销毁</h3>
	 * 销毁与之相关的所有实例,所有依赖关系<br>
	 * BeanName不再依赖其它实例,其它实例也不再依赖BeanName
	 *
	 *
	 *
	 */
	public void destroySingleton(String beanName) {
		// 移除单例
		removeSingleton(beanName);

		// Destroy the corresponding DisposableBean instance.
		DisposableBean disposableBean;
		synchronized (this.disposableBeans) {
			disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
		}
		destroyBean(beanName, disposableBean);
	}

	/**
	 * <h3>实例销毁</h3>
	 * 实例销毁前,必须销毁依赖它的实例
	 * <pre>
	 * 1.销毁 BeanName包含的所有Bean.(传递销毁)
	 * 2.从"实例 -> 依赖实例的实例集合"中,移除已经销毁的bean,是从value中移除,等价于beanName不再依赖实例
	 * 3.移除,"bean -> bean依赖的实例" 中,bean = beanName
	 * </pre>
	 */
	protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
		// Trigger destruction of dependent beans first...
		Set<String> dependencies;
		synchronized (this.dependentBeanMap) {
			// 获取依赖beanName 的所有实例
			dependencies = this.dependentBeanMap.remove(beanName);
		}

		//如果有实例依赖BeanName
		if (dependencies != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
			}
			//循环,递归销毁
			for (String dependentBeanName : dependencies) {
				destroySingleton(dependentBeanName);
			}
		}


		// 整整的销毁逻辑开始...
		if (bean != null) {
			try {
				//调用 destroy()方法,释放资源
				bean.destroy();
			}
			catch (Throwable ex) {
				logger.error("Destroy method on bean with name '" + beanName + "' threw an exception", ex);
			}
		}

		// 销毁它包含的所有bean...
		Set<String> containedBeans;
		synchronized (this.containedBeanMap) {
			// 获取它包含的所有bean
			containedBeans = this.containedBeanMap.remove(beanName);
		}
		//循环销毁
		if (containedBeans != null) {
			for (String containedBeanName : containedBeans) {
				destroySingleton(containedBeanName);
			}
		}

		//2.从"实例 -> 依赖实例的实例集合"中,移除已经销毁的bean,是从value中移除,等价于beanName不再依赖实例
		synchronized (this.dependentBeanMap) {
			for (Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Set<String>> entry = it.next();
				Set<String> dependenciesToClean = entry.getValue();
				dependenciesToClean.remove(beanName);
				if (dependenciesToClean.isEmpty()) {
					it.remove();
				}
			}
		}

		// 移除,"bean -> bean依赖的实例" 中,bean = beanName
		this.dependenciesForBeanMap.remove(beanName);
	}

	/**
	 * 返回实例对象集合,用于外部对象放问的时候,互斥
	 */
	public final Object getSingletonMutex() {
		return this.singletonObjects;
	}

}
