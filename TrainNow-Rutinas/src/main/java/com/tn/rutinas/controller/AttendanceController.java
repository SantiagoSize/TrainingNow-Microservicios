package com.tn.rutinas.controller;

import com.tn.rutinas.dto.AttendanceDayDto;
import com.tn.rutinas.dto.MonthlyReportDto;
import com.tn.rutinas.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API de asistencia y reportes mensuales.
 * Contrato consumido por AttendanceApi.kt (Android).
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService service;

    @PostMapping
    public ResponseEntity<AttendanceDayDto> register(@Valid @RequestBody AttendanceDayDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(dto));
    }

    @GetMapping("/user/{userId}")
    public List<AttendanceDayDto> getByUser(@PathVariable Long userId) {
        return service.getByUser(userId);
    }

    /** Reporte mensual. month en formato yyyy-MM (ej. 2026-08). */
    @GetMapping("/user/{userId}/report/{month}")
    public MonthlyReportDto getMonthlyReport(@PathVariable Long userId, @PathVariable String month) {
        return service.getMonthlyReport(userId, month);
    }
}
