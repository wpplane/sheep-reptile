package com.yuntable.sheep.reptile.remote.entity;

import java.io.Serializable;

public class Constant implements Serializable{

	public static final int REQUIREDTYPE_REQUIRED = 0;
	public static final int REQUIREDTYPE_ELECTIVE = 1;
	public static final int REQUIREDTYPE_FREE_ELECTIVE = 2;
	public static final int REQUIREDTYPE_OTHER = 3;
	
	public static final int CONTENTTYPE_PROFESSION = 0;
	public static final int CONTENTTYPE_PUBLIC = 1;
	public static final int CONTENTTYPE_CURRICULUM_DESIGN = 2;
	public static final int CONTENTTYPE_OTHER = 3;
	
	public static final int TEACHTYPE_FACE = 0;
	public static final int TEACHTYPE_NET = 1;
	
	public static final int EXAMMETHOD_GENERAL = 0;
	public static final int EXAMMETHOD_NOGENERAL = 1;
	public static final int EXAMMETHOD_NORMAL = 2;
	
	public static final int STUDYNATION_FIRST = 0;

	public static final int ATTENDANCEREASON_LEAVE = 0;
	public static final int ATTENDANCEREASON_TRUANCY = 1;
	public static final int ATTENDANCEREASON_EARLYLEAVE = 2;
	
}