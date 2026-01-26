package com.example.studentreport.controller;

import com.example.studentreport.dto.StudentReportDTO;
import com.example.studentreport.service.StudentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentReportController {

    @Autowired
    private StudentReportService studentReportService;

    // 获取所有周报
    @GetMapping
    public ResponseEntity<List<StudentReportDTO>> getAllReports() {
        List<StudentReportDTO> reports = studentReportService.getAllReports();
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    // 根据ID获取周报
    @GetMapping("/{id}")
    public ResponseEntity<StudentReportDTO> getReportById(@PathVariable Long id) {
        Optional<StudentReportDTO> report = studentReportService.getReportById(id);
        return report.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 创建周报
    @PostMapping
    public ResponseEntity<StudentReportDTO> createReport(@Valid @RequestBody StudentReportDTO dto) {
        StudentReportDTO createdReport = studentReportService.createReport(dto);
        return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
    }

    // 更新周报
    @PutMapping("/{id}")
    public ResponseEntity<StudentReportDTO> updateReport(@PathVariable Long id, @Valid @RequestBody StudentReportDTO dto) {
        Optional<StudentReportDTO> updatedReport = studentReportService.updateReport(id, dto);
        return updatedReport.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 删除周报
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        boolean deleted = studentReportService.deleteReport(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}