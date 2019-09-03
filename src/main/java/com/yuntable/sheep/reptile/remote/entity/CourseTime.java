package com.yuntable.sheep.reptile.remote.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class CourseTime implements Serializable{
	private Integer beginWeek;
	private Integer endWeek;
	private Integer beginLesson;
	private Integer endLesson;
	private Integer dayOfWeek;
}
