package com.tutorial.reflection;

import java.lang.reflect.Field;

/**
 * Hello world!
 *
 */
public class App
{
	public static void main(String[] args)
	{
		System.out.println(Reflection.verifier(new SomeClass("SomeString"))
				.bindObject(SomeClass2.class, new SomeClass2()).verify());
		
//		
//		Class klass = M.class;
//		
//		Field[] ff = klass.getDeclaredFields();
//		System.out.println(ff[0].getType());
	}
}
