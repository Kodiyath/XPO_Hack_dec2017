package com.xpo.alexaintegration.rest;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller which responds to requests on /.
 */
@RequestMapping(path = "/")
@RestController
public class RootController {
    /**
     * Responds to requests on /.
     *
     * @return Response.
     */
    @GetMapping
    public String hello() {
        return "Hello from XPO Logistics skill: " + LocalDateTime.now();
    }
}