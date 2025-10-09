package de.szut.lf8_starter.employee;

import de.szut.lf8_starter.security.AuthenticationService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class EmployeeService {
    private final RestTemplate restTemplate;
    private String url = "https://employee-api.szut.dev/employees";

    public EmployeeService() {
        this.restTemplate = new RestTemplate();
    }

    private HttpEntity<Void> getHttpEntityWithToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AuthenticationService.getCurrentJWT());
        return new HttpEntity<>(headers);
    }

    public boolean checkIfEmployeeExists(Long id) {
        HttpEntity<Void> entity = getHttpEntityWithToken();
        String url = this.url + "/" + id;

        ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return true;
        }

        return false;
    }
}
