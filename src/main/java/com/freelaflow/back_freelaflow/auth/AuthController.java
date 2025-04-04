package com.freelaflow.back_freelaflow.auth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/token")
public class AuthController {
    @PostMapping("/")
    public String token(@RequestBody User user) {
        String url = "http://192.168.100.46:8080/realms/FreelaFlow/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", user.clientID);
        formData.add("password", user.password);
        formData.add("grant_type", user.grantType);
        formData.add("username", user.username);

        HttpEntity<MultiValueMap<String, String>> entity
                = new HttpEntity<MultiValueMap<String, String>>(formData, headers);

        return restTemplate.postForEntity(url,entity, String.class).getBody();
    }
    public record User (String password, String clientID, String grantType, String username) {}
}
