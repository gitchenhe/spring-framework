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

package org.springframework.core.convert.support;

import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.lang.Nullable;

/**
 * 默认类型转换
 */
public class DefaultConversionService extends GenericConversionService {

	@Nullable
	private static volatile DefaultConversionService sharedInstance;

	/**
	 */
	public DefaultConversionService() {
		//设置默认转换器
		addDefaultConverters(this);
	}


	/**
	 * 设置默认转换器服务,未设置返回DefaultConversionService
	 */
	public static ConversionService getSharedInstance() {
		DefaultConversionService cs = sharedInstance;
		if (cs == null) {
			synchronized (DefaultConversionService.class) {
				cs = sharedInstance;
				if (cs == null) {
					cs = new DefaultConversionService();
					sharedInstance = cs;
				}
			}
		}
		return cs;
	}

	/**
	 * 默认类型转换器集合
	 */
	public static void addDefaultConverters(ConverterRegistry converterRegistry) {
		//标量转换器
		addScalarConverters(converterRegistry);
		//集合转换器
		addCollectionConverters(converterRegistry);

		//byte buffer 转换器
		converterRegistry.addConverter(new ByteBufferConverter((ConversionService) converterRegistry));
		//String 转 timeZone
		converterRegistry.addConverter(new StringToTimeZoneConverter());
		converterRegistry.addConverter(new ZoneIdToTimeZoneConverter());
		converterRegistry.addConverter(new ZonedDateTimeToCalendarConverter());

		//对象转换器
		converterRegistry.addConverter(new ObjectToObjectConverter());
		converterRegistry.addConverter(new IdToEntityConverter((ConversionService) converterRegistry));
		converterRegistry.addConverter(new FallbackObjectToStringConverter());
		converterRegistry.addConverter(new ObjectToOptionalConverter((ConversionService) converterRegistry));
	}

	/**
	 * Add common collection converters.
	 * @param converterRegistry the registry of converters to add to
	 * (must also be castable to ConversionService, e.g. being a {@link ConfigurableConversionService})
	 * @throws ClassCastException if the given ConverterRegistry could not be cast to a ConversionService
	 * @since 4.2.3
	 */
	public static void addCollectionConverters(ConverterRegistry converterRegistry) {
		ConversionService conversionService = (ConversionService) converterRegistry;
		//数组转集合
		converterRegistry.addConverter(new ArrayToCollectionConverter(conversionService));
		//集合转数组
		converterRegistry.addConverter(new CollectionToArrayConverter(conversionService));

		//数组转数组
		converterRegistry.addConverter(new ArrayToArrayConverter(conversionService));
		//集合转集合
		converterRegistry.addConverter(new CollectionToCollectionConverter(conversionService));
		//字典转字典
		converterRegistry.addConverter(new MapToMapConverter(conversionService));

		//集合转字符串
		converterRegistry.addConverter(new ArrayToStringConverter(conversionService));
		//字符串转集合
		converterRegistry.addConverter(new StringToArrayConverter(conversionService));

		//数组转对象
		converterRegistry.addConverter(new ArrayToObjectConverter(conversionService));
		//对象转数组
		converterRegistry.addConverter(new ObjectToArrayConverter(conversionService));

		//集合转字符串
		converterRegistry.addConverter(new CollectionToStringConverter(conversionService));
		//字符串转集合
		converterRegistry.addConverter(new StringToCollectionConverter(conversionService));

		//集合转对象
		converterRegistry.addConverter(new CollectionToObjectConverter(conversionService));
		//对象转几个
		converterRegistry.addConverter(new ObjectToCollectionConverter(conversionService));

		//流转换
		converterRegistry.addConverter(new StreamConverter(conversionService));
	}

	/**
	 * 标量转换器
	 * @param converterRegistry
	 */
	private static void addScalarConverters(ConverterRegistry converterRegistry) {
		converterRegistry.addConverterFactory(new NumberToNumberConverterFactory());

		converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
		converterRegistry.addConverter(Number.class, String.class, new ObjectToStringConverter());

		converterRegistry.addConverter(new StringToCharacterConverter());
		converterRegistry.addConverter(Character.class, String.class, new ObjectToStringConverter());

		converterRegistry.addConverter(new NumberToCharacterConverter());
		converterRegistry.addConverterFactory(new CharacterToNumberFactory());

		converterRegistry.addConverter(new StringToBooleanConverter());
		converterRegistry.addConverter(Boolean.class, String.class, new ObjectToStringConverter());

		converterRegistry.addConverterFactory(new StringToEnumConverterFactory());
		converterRegistry.addConverter(new EnumToStringConverter((ConversionService) converterRegistry));

		converterRegistry.addConverterFactory(new IntegerToEnumConverterFactory());
		converterRegistry.addConverter(new EnumToIntegerConverter((ConversionService) converterRegistry));

		converterRegistry.addConverter(new StringToLocaleConverter());
		converterRegistry.addConverter(Locale.class, String.class, new ObjectToStringConverter());

		converterRegistry.addConverter(new StringToCharsetConverter());
		converterRegistry.addConverter(Charset.class, String.class, new ObjectToStringConverter());

		converterRegistry.addConverter(new StringToCurrencyConverter());
		converterRegistry.addConverter(Currency.class, String.class, new ObjectToStringConverter());

		converterRegistry.addConverter(new StringToPropertiesConverter());
		converterRegistry.addConverter(new PropertiesToStringConverter());

		converterRegistry.addConverter(new StringToUUIDConverter());
		converterRegistry.addConverter(UUID.class, String.class, new ObjectToStringConverter());
	}

}
