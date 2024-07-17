package com.dev.h3nry.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AuthorisationController {

    @GetMapping("/demo")
    public ResponseEntity<String> getAuthorisation() {
        return ResponseEntity.ok("Welcome to this Demo APPLICATION");
    }
}
