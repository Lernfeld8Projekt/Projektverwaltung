package de.szut.lf8_starter.employee;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.security.AuthenticationService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

        try {
            ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getStatusCode() == HttpStatus.OK;

        }catch (HttpClientErrorException.NotFound exception) {
            return false;
        }
    }

    public boolean checkIfEmployeeHaveQualification(Long employeeId, Long qualificationId) {
        HttpEntity<Void> entity = getHttpEntityWithToken();
        String url = this.url + "/" + employeeId + "/qualifications";

        try {
            ResponseEntity<SkillSetDTO> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, SkillSetDTO.class);

            SkillSetDTO skillSetDTO = response.getBody();


            if (skillSetDTO != null ) {
                boolean hasQualification = skillSetDTO.getSkillSet().stream().anyMatch(skill -> qualificationId.equals(skill.getId()));
                if (hasQualification) {
                    return true;
                }
            }

            throw new ResourceNotFoundException("Qualification not found for employee on ID: " + qualificationId);
        }catch (HttpClientErrorException exception) {
            throw new ResourceNotFoundException(exception.getMessage());
        }
    }

    public NameDTO getEmployeeName(Long employeeId) {
        HttpEntity<Void> entity = getHttpEntityWithToken();
        String url = this.url + "/" + employeeId;

        try {
            ResponseEntity<NameDTO> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, NameDTO.class);

            NameDTO nameDTO = response.getBody();

            if (nameDTO != null){
                return nameDTO;
            }

            throw new ResourceNotFoundException("Employee not found on ID: " + employeeId);
        }catch (HttpClientErrorException exception) {
            throw new ResourceNotFoundException(exception.getMessage());
        }
    }
}
