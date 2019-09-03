package com.yuntable.sheep.reptile.remote.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class StudentInfo implements Serializable{
	private String stuId;
	private String name;
	private int sex;
	private String college;
	private String profession;
	private String clazz;
	private int eduSystem;
	private int entryYear;
}
