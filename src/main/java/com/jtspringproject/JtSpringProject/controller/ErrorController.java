package com.jtspringproject.JtSpringProject.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @GetMapping("/403")
    public String accessDenied() {
        logger.warn("Access denied (403) page served");
        return "403";
    }
}
