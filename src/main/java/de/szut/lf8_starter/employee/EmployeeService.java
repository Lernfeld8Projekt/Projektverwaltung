package de.szut.lf8_starter.employee;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmployeeService {

    private final RestTemplate restTemplate;
    private String url = "https://employee.szut.dev/employees";

    public EmployeeService() {
        this.restTemplate = new RestTemplate();
    }

    public HttpStatus checkIfEmployeeExists(Long id) {
        return this.restTemplate.getForObject(this.url + "/" + id, HttpStatus.class);
    }
}
