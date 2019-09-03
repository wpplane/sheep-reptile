package com.yuntable.sheep.reptile.remote.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class Course implements Serializable{
	private String courseCode;
	private String courseName;
	private double studyScore;
	private Integer totalStudyTime;
	private Integer teachStudyTime;
	private Integer optionStudyTime;
	private Integer requiredType;
	private Integer contentType;
	private Integer teachType;
	private Integer examMethod;
	private String teacher;
	private List<CourseTime> courseTimeList;
	private String place;
	private Integer schoolYear;
	private Integer term;
}
