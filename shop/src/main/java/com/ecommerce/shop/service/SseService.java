package com.ecommerce.shop.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SSE (Server-Sent Events) Service
 * Broadcasts real-time events to ALL connected clients (users + admin on any device).
 * When admin adds/updates/deletes a product or updates order status,
 * all user browsers receive the event instantly — no page refresh needed.
 */
@Service
public class SseService {

    // Thread-safe list of all connected browsers (one per open tab/device)
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /**
     * A new client (browser tab) connects — register its emitter.
     */
    public SseEmitter register() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // never timeout

        emitters.add(emitter);

        // Clean up when client disconnects
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    /**
     * Broadcast an event to ALL connected clients.
     *
     * @param eventType  e.g. "product-added", "product-updated", "order-status-changed"
     * @param data       JSON string payload
     */
    public void broadcast(String eventType, String data) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventType)
                        .data(data));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);
    }
}
