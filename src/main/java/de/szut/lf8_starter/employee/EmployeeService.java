package de.szut.lf8_starter.employee;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.security.AuthenticationService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

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

        try {
            ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getStatusCode() == HttpStatus.OK;

        }catch (HttpClientErrorException.NotFound exception) {
            return false;
        }
    }

    public Map<String, Object> getEmployeeById(Long id) {
        HttpEntity<Void> entity = getHttpEntityWithToken();
        String urlWithId = this.url + "/" + id;

        try {
            ResponseEntity<Map> response = this.restTemplate.exchange(urlWithId, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Employee with number " + id + " doesn´t exist.");
        }
        throw new ResourceNotFoundException("Employee data could not be loaded.");
    }

}
