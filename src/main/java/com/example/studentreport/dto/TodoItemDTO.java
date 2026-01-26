package com.example.studentreport.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class TodoItemDTO {

    private Long id;

    @NotBlank(message = "待办事项内容不能为空")
    private String text;
    private boolean completed;
    private Long userId = 1L;
}