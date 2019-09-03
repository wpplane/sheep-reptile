package com.yuntable.sheep.reptile.remote.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class Score implements Serializable{
	private String courseCode;
	private String courseName;
	private double studyScore;
	private Integer requiredType;
	private Integer contentType;
	private Integer studyNation;
	private Integer examMethod;
	private double score;
	private String scoreStr;
	private double haveGetStudyScore;
	private double achiPoint;
	private double studyScoreAndAchiPoint;
	private Integer schoolYear;
	private Integer term;
}
