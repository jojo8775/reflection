package com.tutorial.reflection;

import java.util.List;

public class SomeClass
{
	private final String str2;

	private String str1;
	private int int1;
	private SomeClass2 someObj;
	
	private List<String> ll;

	public SomeClass(String str2)
	{
		this.str2 = str2;
	}

	public String getStr1()
	{
		return str1;
	}

	public void setStr1(String str1)
	{
		this.str1 = str1;
	}

	public int getInt1()
	{
		return int1;
	}

	public void setInt1(int int1)
	{
		this.int1 = int1;
	}

	public String getStr2()
	{
		return str2;
	}

	public SomeClass2 getSomeObj()
	{
		return someObj;
	}

	public void setSomeObj(SomeClass2 someObj)
	{
		this.someObj = someObj;
	}

	public List<String> getLl()
	{
		return ll;
	}

	public void setLl(List<String> ll)
	{
		this.ll = ll;
	}
}
