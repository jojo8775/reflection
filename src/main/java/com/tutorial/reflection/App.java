package com.tutorial.reflection;

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
		
		System.out.println(Reflection.verifier(new SomeClass4()).verify());
		
		System.out.println(Reflection.verifier(new TestClass1()).verify());
		
//		
//		Class klass = SomeClass.class;
//		
//		Field[] ff = klass.getDeclaredFields();
//		
//		for(Field sf : ff){
//			System.out.println(sf.getType());
//			
//			if(Collection.class.isAssignableFrom(sf.getType())){
//				System.out.println("================");
//				System.out.println(sf.getGenericType());
//				
//				Type returnType = sf.getGenericType();
//				
//				if(returnType instanceof ParameterizedType){
//				    ParameterizedType type = (ParameterizedType) returnType;
//				    Type[] typeArguments = type.getActualTypeArguments();
//				    for(Type typeArgument : typeArguments){
//				        Class typeArgClass = (Class) typeArgument;
//				        System.out.println("typeArgClass = " + typeArgClass);
//				    }
//				}
//			}
//			
//			if(sf.getType() == Map.class){
//				System.out.println("================");
//				System.out.println(sf.getGenericType());
//				
//				Type returnType = sf.getGenericType();
//				
//				if(returnType instanceof ParameterizedType){
//				    ParameterizedType type = (ParameterizedType) returnType;
//				    Type[] typeArguments = type.getActualTypeArguments();
//				    for(Type typeArgument : typeArguments){
////				        Class typeArgClass = (Class) typeArgument;
//				        System.out.println("typeArgClass = " + typeArgument.getTypeName());
//				    }
//				}
//			}
//		}
	}
}
