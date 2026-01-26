package com.example.studentreport.service;

import com.example.studentreport.dto.StudentReportDTO;
import com.example.studentreport.entity.StudentReport;
import com.example.studentreport.repository.StudentReportRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentReportService {

    @Autowired
    private StudentReportRepository studentReportRepository;

    // 生成评价
    private String generateEvaluation(int homeworkCompleted) {
        if (homeworkCompleted >= 8) {
            return "作业完成情况优秀，学习态度非常认真，继续保持！";
        } else if (homeworkCompleted >= 5) {
            return "作业完成情况良好，学习态度积极，建议继续努力！";
        } else if (homeworkCompleted >= 3) {
            return "作业完成情况一般，需要提高学习效率，加强时间管理！";
        } else {
            return "作业完成情况较差，学习态度需要改善，建议家长和老师加强督促！";
        }
    }

    // 生成周报内容
    private String generateReportContent(StudentReportDTO dto, String evaluation) {
        return String.format("【学生周报】%s同学本周表现:出勤%d天，完成作业%d份。%s",
                dto.getStudentName(), dto.getAttendanceDays(), dto.getHomeworkCompleted(), evaluation);
    }

    // 创建周报
    public StudentReportDTO createReport(StudentReportDTO dto) {
        // 生成评价和内容
        String evaluation = generateEvaluation(dto.getHomeworkCompleted());
        String content = generateReportContent(dto, evaluation);
        
        // 设置评价和内容
        dto.setEvaluation(evaluation);
        dto.setContent(content);
        
        // 转换为实体并保存
        StudentReport entity = new StudentReport();
        BeanUtils.copyProperties(dto, entity);
        StudentReport savedEntity = studentReportRepository.save(entity);
        
        // 转换为DTO并返回
        StudentReportDTO result = new StudentReportDTO();
        BeanUtils.copyProperties(savedEntity, result);
        return result;
    }

    // 获取所有周报
    public List<StudentReportDTO> getAllReports() {
        return studentReportRepository.findAll().stream()
                .map(entity -> {
                    StudentReportDTO dto = new StudentReportDTO();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 根据ID获取周报
    public Optional<StudentReportDTO> getReportById(Long id) {
        return studentReportRepository.findById(id)
                .map(entity -> {
                    StudentReportDTO dto = new StudentReportDTO();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                });
    }

    // 更新周报
    public Optional<StudentReportDTO> updateReport(Long id, StudentReportDTO dto) {
        return studentReportRepository.findById(id)
                .map(existingEntity -> {
                    // 生成新的评价和内容
                    String evaluation = generateEvaluation(dto.getHomeworkCompleted());
                    String content = generateReportContent(dto, evaluation);
                    
                    // 更新DTO
                    dto.setEvaluation(evaluation);
                    dto.setContent(content);
                    
                    // 更新实体
                    BeanUtils.copyProperties(dto, existingEntity);
                    StudentReport updatedEntity = studentReportRepository.save(existingEntity);
                    
                    // 转换为DTO并返回
                    StudentReportDTO result = new StudentReportDTO();
                    BeanUtils.copyProperties(updatedEntity, result);
                    return result;
                });
    }

    // 删除周报
    public boolean deleteReport(Long id) {
        if (studentReportRepository.existsById(id)) {
            studentReportRepository.deleteById(id);
            return true;
        }
        return false;
    }
}