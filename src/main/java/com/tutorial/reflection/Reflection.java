package com.tutorial.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Reflection
{
	private Map<Class, Object> userSpecifiedObject = new HashMap<Class, Object>();
	private AtomicLong atomicLong = new AtomicLong(420);
	private AtomicInteger atomicInteger = new AtomicInteger(108);
	private Map<String, Method> availableMethods;

	private Object inputObject;

	public Reflection(Object inputObject)
	{
		this.inputObject = inputObject;
	}

	public static Reflection verifier(Object inputObject)
	{
		return new Reflection(inputObject);
	}

	public Reflection bindObject(Class klass, Object object)
	{
		userSpecifiedObject.put(klass, object);
		return this;
	}

	public boolean verify()
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

				if (fieldObjectToValidate == null || expectedObject == null
						|| !fieldObjectToValidate.equals(expectedObject))
				{
					return false;
				}

				if(Collection.class.isAssignableFrom(singleField.getType())){
					if(!checkImmutability()){
						return false;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			userSpecifiedObject.clear();
		}

		return true;
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

	private Object createObject(Field field)
	{
		if (field.getType() == String.class)
		{
			return "Test " + atomicLong.incrementAndGet();
		}
		else if (field.getType() == Long.class || field.getType() == Long.TYPE)
		{
			return atomicLong.incrementAndGet();
		}
		else if (field.getType() == Integer.class || field.getType() == Integer.TYPE)
		{
			return atomicInteger.incrementAndGet();
		}
		else if (field.getType() == Boolean.class || field.getType() == Boolean.TYPE)
		{
			return true;
		}
		else if (field.getType() == Date.class)
		{
			return new Date();
		}
		else if (field.getType() == List.class)
		{
			List<Object> list = new ArrayList<Object>();
			list.add(new Object());
			list.add(new Object());

			return list;
		}

		return userSpecifiedObject.get(field.getType());
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
}
