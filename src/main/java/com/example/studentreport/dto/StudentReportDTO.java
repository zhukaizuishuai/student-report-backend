package com.example.studentreport.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

import java.util.Date;

@Data
public class StudentReportDTO {

    private Long id;

    @NotBlank(message = "学生姓名不能为空")
    private String studentName;

    @Min(value = 1, message = "考勤天数不能少于1天")
    @Max(value = 7, message = "考勤天数不能超过7天")
    private int attendanceDays;

    @Min(value = 0, message = "作业完成数不能为负数")
    private int homeworkCompleted;

    private String content;
    private String evaluation;
    private Date createdAt;
}