package com.example.gato.api.me;

import com.example.gato.api.me.dto.UserProfileDto;
import com.example.gato.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileDto me(Principal principal) {
        return userService.getProfile(principal.getName());
    }
}
