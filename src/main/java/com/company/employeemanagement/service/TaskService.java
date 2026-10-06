package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.task.CreateTaskRequest;
import com.company.employeemanagement.dto.task.EmployeeTaskUpdateRequest;
import com.company.employeemanagement.dto.task.TaskResponse;
import com.company.employeemanagement.dto.task.UpdateTaskRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request);
    TaskResponse getTask(Long id);
    Page<TaskResponse> getAllTasks(Pageable pageable);
    TaskResponse updateTask(Long id, UpdateTaskRequest request);
    void deleteTask(Long id);

    // Employee methods
    TaskResponse createEmployeeTask(CreateTaskRequest request);
    List<TaskResponse> getMyTasks();
    TaskResponse getMyTask(Long id);
    TaskResponse updateMyTaskProgress(Long id, EmployeeTaskUpdateRequest request);
}
