package de.szut.lf8_starter.project;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class GetJWT {
    protected static String getToken(){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body =
                "grant_type=client_credentials" +
                        "&username=john" +
                        "&password=nt7su3vuTaxtsmKdlhr2RCbRD4tis5i7zBFJbbTWyeTjrRqTpQ513z73ZlV3" +
                        "&client_id=hitec_api_client" +
                        "&scope=openid";

        HttpEntity<String> request = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://authentik.szut.dev/application/o/token/",
                request,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }
}
