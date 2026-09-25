package com.example.studentreport.service;

import com.example.studentreport.dto.TodoItemDTO;
import com.example.studentreport.entity.TodoItem;
import com.example.studentreport.repository.TodoItemRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TodoItemService {

    @Autowired
    private TodoItemRepository todoItemRepository;

    // 获取所有待办事项
    public List<TodoItemDTO> getAllTodos(Long userId) {
        return todoItemRepository.findByUserId(userId).stream()
                .map(entity -> {
                    TodoItemDTO dto = new TodoItemDTO();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 创建待办事项
    public TodoItemDTO createTodo(TodoItemDTO dto) {
        TodoItem entity = new TodoItem();
        BeanUtils.copyProperties(dto, entity);
        TodoItem savedEntity = todoItemRepository.save(entity);
        
        TodoItemDTO result = new TodoItemDTO();
        BeanUtils.copyProperties(savedEntity, result);
        return result;
    }

    // 更新待办事项
    public Optional<TodoItemDTO> updateTodo(Long id, TodoItemDTO dto) {
        return todoItemRepository.findById(id)
                .map(existingEntity -> {
                    BeanUtils.copyProperties(dto, existingEntity);
                    TodoItem updatedEntity = todoItemRepository.save(existingEntity);
                    
                    TodoItemDTO result = new TodoItemDTO();
                    BeanUtils.copyProperties(updatedEntity, result);
                    return result;
                });
    }

    // 删除待办事项
    public boolean deleteTodo(Long id) {
        if (todoItemRepository.existsById(id)) {
            todoItemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 更新待办事项状态
    public Optional<TodoItemDTO> updateTodoStatus(Long id, boolean completed) {
        return todoItemRepository.findById(id)
                .map(existingEntity -> {
                    existingEntity.setCompleted(completed);
                    TodoItem updatedEntity = todoItemRepository.save(existingEntity);
                    
                    TodoItemDTO result = new TodoItemDTO();
                    BeanUtils.copyProperties(updatedEntity, result);
                    return result;
                });
    }
}