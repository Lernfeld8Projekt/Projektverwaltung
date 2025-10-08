package de.szut.lf8_starter.examples.welcome;



import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class WelcomeController {

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome to lf8_starter";
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles(Authentication authentication) {
        return ResponseEntity.ok(authentication.getAuthorities());
    }


}
