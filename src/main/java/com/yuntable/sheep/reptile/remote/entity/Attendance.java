package com.yuntable.sheep.reptile.remote.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class Attendance implements Serializable{
	private String courseCode;
	private String courseName;
	private String teacherCode;
	private String teacherName;
	private Integer week;
	private Integer dayOfWeek;
	private Integer beginLesson;
	private Integer endLesson;
	private Date date;
	private Integer reason;
	
	private int schoolYear;
	private int term;
}
