package com.ecommerce.shop.controller;

import com.ecommerce.shop.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE Controller — Clients connect here to receive real-time updates.
 * GET /api/events  →  open persistent SSE connection
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class SseController {

    @Autowired
    private SseService sseService;

    /**
     * Browser connects here once on page load.
     * Connection stays open and receives events pushed from server.
     */
    @GetMapping(produces = "text/event-stream")
    public SseEmitter subscribe() {
        return sseService.register();
    }
}
