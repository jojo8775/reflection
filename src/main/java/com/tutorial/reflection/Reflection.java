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

import junit.framework.Assert;

public class Reflection
{
	private Map<Class<?>, Object> userSpecifiedObject = new HashMap<Class<?>, Object>();
	private Map<String, Method> availableMethods;
	private List<String> fieldsToExclude = new ArrayList<String>();
	private AtomicLong atomicLong = new AtomicLong(420);
	private AtomicInteger atomicInteger = new AtomicInteger(108);

	private Object inputObject;

	public Reflection(Object inputObject)
	{
		this.inputObject = inputObject;
	}

	public static Reflection verifier(Object inputObject)
	{
		return new Reflection(inputObject);
	}

	public Reflection bindObject(Class<?> klass, Object object)
	{
		userSpecifiedObject.put(klass, object);
		return this;
	}

	public Reflection excludeField(String fieldName)
	{
		fieldsToExclude.add(fieldName);
		return this;
	}

	public boolean verify()
	{
		Class<?> klass = inputObject.getClass();
		availableMethods = getAllPublicMethods(klass);

		Field[] fields = klass.getDeclaredFields();
		
		boolean status = false;

		try
		{
			for (Field singleField : fields)
			{
				if (fieldsToExclude.contains(singleField.getName()))
				{
					continue;
				}

				Object fieldObjectToValidate = getFieldObject(singleField, inputObject);
				Object expectedObject = getExpectedObject(singleField, inputObject);

				if (fieldObjectToValidate != null && expectedObject != null)
				{
					Assert.assertEquals(fieldObjectToValidate, expectedObject);
				}
				else
				{
					Assert.fail(String.format("Failed to verify for %s", singleField.getName()));
				}
			}
			
			status = true;
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
		
		return status;
	}

	private Object getFieldObject(Field field, Object inputObject)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		if (!Modifier.isFinal(field.getModifiers()))
		{
			Method setterMethod = availableMethods.get(createSetterMethodName(field));
			if (setterMethod == null)
			{
				String errorMessage = String.format("For %s field name no corresponding %s method was found",
						field.getName(), createSetterMethodName(field));
				Assert.fail(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}

			invokeSetterMethod(setterMethod, inputObject, createObject(field));
		}

		field.setAccessible(true);
		return field.get(inputObject);
	}

	private Object getExpectedObject(Field field, Object inputObject)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method getterMethod = availableMethods.get(createGetterMethodName(field));
		if (getterMethod == null)
		{
			String errorMessage = String.format("For %s field name no corresponding %s method was found",
					field.getName(), createGetterMethodName(field));
			Assert.fail(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		return invokeGetterMethod(getterMethod, inputObject);
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

		return createObject(type);
	}

	private Object createArrays(Type type)
	{
		Object object = Array.newInstance((Class<?>) type, 1);
		Array.set(object, 0, createObject(type));
		return object;
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
			else if (userSpecifiedObject.containsKey(type))
			{
				return userSpecifiedObject.get(type);
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
			Assert.fail((String.format("%s couldnot be constructed", type.getTypeName())));
		}

		if (type instanceof ParameterizedType)
		{
			Assert.fail((String.format("%s couldnot be constructed", ((ParameterizedType) type).getRawType())));
		}
		else
		{
			Assert.fail((String.format("%s couldnot be constructed", type.getTypeName())));
		}

		return null;
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

	private Map<String, Method> getAllPublicMethods(Class<?> klass)
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
