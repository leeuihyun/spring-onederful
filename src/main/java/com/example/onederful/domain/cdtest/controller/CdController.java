package com.example.onederful.domain.cdtest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class CdController {

	@GetMapping
	public ResponseEntity<?> test() {
		return ResponseEntity.ok("cd successful");
	}
}
