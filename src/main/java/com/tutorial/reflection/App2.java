package com.tutorial.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class App2
{
	private Map<Class, Object> userSpecifiedObject = new HashMap<Class, Object>();
	private AtomicLong atomicLong = new AtomicLong(420);
	private AtomicInteger atomicInteger = new AtomicInteger(108);
	private Map<String, Method> availableMethods;

	private Object inputObject;

	private void execute(Object inputObject)
	{
		Class klass = inputObject.getClass();
		availableMethods = getAllPublicMethods(klass);

		Field[] fields = klass.getDeclaredFields();

		try
		{
			for (Field singleField : fields)
			{
				Object fieldObjectToValidate = getFieldObject(singleField, inputObject);
				Object expectedObject = getExpectedObject(singleField, inputObject);

				if (fieldObjectToValidate != null && expectedObject != null)
				{
					System.out.println(fieldObjectToValidate.getClass());
				}

				if (Collection.class.isAssignableFrom(singleField.getType()))
				{
					// if(!checkImmutability()){
					// return false;
					// }
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private Object createObject(Field field)
	{
		Type type = field.getType();
		if (Collection.class.isAssignableFrom((Class<?>) type))
		{
			return createObject(field.getGenericType());
		}
		else if (Map.class.isAssignableFrom((Class<?>) type))
		{
			return createObject(field.getGenericType());
		}
		else if (field.getType().isArray())
		{
			return createArrays(field.getType().getComponentType());
		}

		return null;
	}

	private Object createArrays(Type type)
	{
		Object o = Array.newInstance((Class) type, 1);
		Array.set(o, 0, createObject(type));
		return o;
	}

	private Object createObject(Type type)
	{
		try
		{
			if (type == String.class)
			{
				return "Test " + atomicLong.incrementAndGet();
			}
			else if (type == Long.class || type == Long.TYPE)
			{
				return atomicLong.incrementAndGet();
			}
			else if (type == Integer.class || type == Integer.TYPE)
			{
				return atomicInteger.incrementAndGet();
			}
			else if (type == Boolean.class || type == Boolean.TYPE)
			{
				return true;
			}
			else if (type == Date.class)
			{
				return new Date();
			}
			else if (((ParameterizedType) type).getRawType() == List.class)
			{
				ParameterizedType pType = (ParameterizedType) type;
				List<Object> ll = new ArrayList<Object>();
				ll.add(createObject(pType.getActualTypeArguments()[0]));
				ll.add(createObject(pType.getActualTypeArguments()[0]));
				return ll;
			}
			else if (((ParameterizedType) type).getRawType() == Set.class)
			{
				ParameterizedType pType = (ParameterizedType) type;
				Set<Object> set = new HashSet<Object>(1);
				set.add(createObject(pType.getActualTypeArguments()[0]));
				return set;
			}
			else if (((ParameterizedType) type).getRawType() == Map.class)
			{
				ParameterizedType pType = (ParameterizedType) type;
				Map<Object, Object> map = new HashMap<Object, Object>(1);
				map.put(createObject(pType.getActualTypeArguments()[0]),
						createObject(pType.getActualTypeArguments()[1]));
				return map;
			}
		}
		catch (ClassCastException e)
		{
			System.out.println(String.format("%s couldnot be constructed", type.getTypeName()));
		}

		return null;
	}

	private Object getFieldObject(Field singleField, Object inputObject)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		if (!Modifier.isFinal(singleField.getModifiers()))
		{
			Method setterMethod = availableMethods.get(createSetterMethodName(singleField));
			invokeSetterMethod(setterMethod, inputObject, createObject(singleField));
		}

		singleField.setAccessible(true);
		return singleField.get(inputObject);
	}

	private Object getExpectedObject(Field field, Object inputObject)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method getterMethod = availableMethods.get(createGetterMethodName(field));
		return invokeGetterMethod(getterMethod, inputObject);
	}

	private String createGetterMethodName(Field field)
	{
		String variableName = field.getName();

		String prefix = "";
		if (field.getType() == Boolean.TYPE || field.getType() == Boolean.class)
		{
			prefix = "is";
		}
		else
		{
			prefix = "get";
		}

		return prefix + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);
	}

	private String createSetterMethodName(Field field)
	{
		String variableName = field.getName();

		return "set" + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);
	}

	private Map<String, Method> getAllPublicMethods(Class klass)
	{
		Method[] methods = klass.getMethods();
		HashMap<String, Method> methodsByName = new HashMap<String, Method>(methods.length);
		for (Method singleMethod : methods)
		{
			methodsByName.put(singleMethod.getName(), singleMethod);
		}

		return methodsByName;
	}

	private void invokeSetterMethod(Method method, Object inputObject, Object fieldObject)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		method.invoke(inputObject, fieldObject);
	}

	private Object invokeGetterMethod(Method method, Object inputObject)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return method.invoke(inputObject);
	}

	public static void main(String[] args)
	{
		new App2().execute(new SomeClass4());
	}
}
