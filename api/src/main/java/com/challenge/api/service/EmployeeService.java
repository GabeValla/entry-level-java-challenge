package com.challenge.api.service;

import com.challenge.api.model.Employee;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final Map<UUID, Employee> employees = new HashMap<>();

    public Collection<Employee> getAllEmployees() {
        return employees.values();
    }
}
