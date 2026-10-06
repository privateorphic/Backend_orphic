package com.company.employeemanagement.specification;

import com.company.employeemanagement.entity.Task;
import com.company.employeemanagement.entity.TaskPriority;
import com.company.employeemanagement.entity.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecification {

    private TaskSpecification() {}

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, cb) ->
                priority == null ? cb.conjunction() : cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> assignedTo(Long userId) {
        return (root, query, cb) ->
                userId == null ? cb.conjunction() : cb.equal(root.get("assignedTo").get("id"), userId);
    }

    public static Specification<Task> inDepartment(Long deptId) {
        return (root, query, cb) ->
                deptId == null ? cb.conjunction() : cb.equal(root.get("department").get("id"), deptId);
    }

    public static Specification<Task> deadlineBefore(LocalDate date) {
        return (root, query, cb) ->
                date == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("deadline"), date);
    }

    public static Specification<Task> deadlineAfter(LocalDate date) {
        return (root, query, cb) ->
                date == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("deadline"), date);
    }

    public static Specification<Task> titleContains(String keyword) {
        return (root, query, cb) ->
                keyword == null ? cb.conjunction() :
                        cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }
}
