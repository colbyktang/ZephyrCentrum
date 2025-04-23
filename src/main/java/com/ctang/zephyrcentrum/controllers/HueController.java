package com.ctang.zephyrcentrum.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/wind")
public class HueController {

    @GetMapping("/lights")
    public ResponseEntity<String> getLights() {
        return ResponseEntity.ok("Got Philips Lights from HueController!");
    }

    @PutMapping("/lights/{lightId}/state")
    public ResponseEntity<Map<String, Object>> toggleLight(
            @PathVariable String lightId,
            @RequestBody Map<String, Object> lightState) {
        System.out.println("Received update for light: " + lightId);
        System.out.println("Light state: " + lightState);
        return ResponseEntity.ok(lightState);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    public static class LightState {
    private Boolean on;
    private Integer brightness;
    private String effect;

    // Getters and setters
    public Boolean getOn() {
        return on;
    }

    public void setOn(Boolean on) {
        this.on = on;
    }

    public Integer getBrightness() {
        return brightness;
    }

    public void setBrightness(Integer brightness) {
        this.brightness = brightness;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    @Override
    public String toString() {
        return "LightState{" +
                "on=" + on +
                ", brightness=" + brightness +
                ", effect='" + effect + '\'' +
                '}';
    }
}
}
