package com.example.studentreport.controller;

import com.example.studentreport.dto.TodoItemDTO;
import com.example.studentreport.service.TodoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TodoItemController {

    @Autowired
    private TodoItemService todoItemService;

    // 获取所有待办事项
    @GetMapping
    public ResponseEntity<List<TodoItemDTO>> getAllTodos(@RequestParam(defaultValue = "1") Long userId) {
        List<TodoItemDTO> todos = todoItemService.getAllTodos(userId);
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    // 创建待办事项
    @PostMapping
    public ResponseEntity<TodoItemDTO> createTodo(@Valid @RequestBody TodoItemDTO dto) {
        TodoItemDTO createdTodo = todoItemService.createTodo(dto);
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    // 更新待办事项
    @PutMapping("/{id}")
    public ResponseEntity<TodoItemDTO> updateTodo(@PathVariable Long id, @Valid @RequestBody TodoItemDTO dto) {
        Optional<TodoItemDTO> updatedTodo = todoItemService.updateTodo(id, dto);
        return updatedTodo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 更新待办事项状态
    @PatchMapping("/{id}/status")
    public ResponseEntity<TodoItemDTO> updateTodoStatus(@PathVariable Long id, @RequestParam boolean completed) {
        Optional<TodoItemDTO> updatedTodo = todoItemService.updateTodoStatus(id, completed);
        return updatedTodo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 删除待办事项
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        boolean deleted = todoItemService.deleteTodo(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}