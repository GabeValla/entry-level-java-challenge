package com.challenge.api.service;

import com.challenge.api.dto.EmployeeCreateRequest;
import com.challenge.api.model.Employee;
import com.challenge.api.model.EmployeeImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    private final Map<UUID, Employee> employees = new ConcurrentHashMap<>();

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees.values());
    }

    public Employee getEmployeeByUuid(UUID uuid) {
        Employee employee = employees.get(uuid);
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }
        return employee;
    }

    public Employee createEmployee(EmployeeCreateRequest request) {
        validate(request);
        EmployeeImpl employee = new EmployeeImpl();
        UUID uuid = UUID.randomUUID();
        employee.setUuid(uuid);
        employee.setFirstName(request.getFirstName().trim());
        employee.setLastName(request.getLastName().trim());
        employee.setSalary(request.getSalary());
        employee.setAge(request.getAge());
        employee.setJobTitle(request.getJobTitle().trim());
        employee.setEmail(request.getEmail().trim());
        employee.setContractHireDate(request.getContractHireDate());
        employee.setContractTerminationDate(request.getContractTerminationDate());
        employees.put(uuid, employee);
        log.info("Created employee uuid={}", uuid);
        return employee;
    }

    public void deleteEmployee(UUID uuid) {
        Employee removed = employees.remove(uuid);
        if (removed == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }
        log.info("Deleted employee uuid={}", uuid);
    }

    private void validate(EmployeeCreateRequest request) {
        if (request == null || isBlank(request.getFirstName()) || isBlank(request.getLastName())
                || isBlank(request.getJobTitle()) || isBlank(request.getEmail())
                || request.getSalary() == null || request.getAge() == null
                || request.getContractHireDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required fields are missing");
        }
        if (request.getSalary() < 0 || request.getAge() <= 0 || request.getAge() > 120
                || !request.getEmail().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid employee information");
        }
        if (request.getContractTerminationDate() != null
                && request.getContractTerminationDate().isBefore(request.getContractHireDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Termination date precedes hire date");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
