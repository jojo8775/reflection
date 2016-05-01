package com.tutorial.reflection;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SomeClass3
{
	private List<String> someList;
	private List<List<String>> someTwoDimentaionalList;
	private Set<String> someSet;
	private Map<String, String> someMap;
	private Map<String, List<String>> someComplicatedMap;

	public List<String> getSomeList()
	{
		return someList;
	}

	public void setSomeList(List<String> someList)
	{
		this.someList = someList;
	}

	public List<List<String>> getSomeTwoDimentaionalList()
	{
		return someTwoDimentaionalList;
	}

	public void setSomeTwoDimentaionalList(List<List<String>> someTwoDimentaionalList)
	{
		this.someTwoDimentaionalList = someTwoDimentaionalList;
	}

	public Set<String> getSomeSet()
	{
		return someSet;
	}

	public void setSomeSet(Set<String> someSet)
	{
		this.someSet = someSet;
	}

	public Map<String, String> getSomeMap()
	{
		return someMap;
	}

	public void setSomeMap(Map<String, String> someMap)
	{
		this.someMap = someMap;
	}

	public Map<String, List<String>> getSomeComplicatedMap()
	{
		return someComplicatedMap;
	}

	public void setSomeComplicatedMap(Map<String, List<String>> someComplicatedMap)
	{
		this.someComplicatedMap = someComplicatedMap;
	}
}
