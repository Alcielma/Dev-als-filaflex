package com.qmasters.fila_flex.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.AppointmentDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.service.AppointmentService;
import com.qmasters.fila_flex.util.PriorityCondition;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Appointment>> getAllAppointment() {
        return ResponseEntity.ok(appointmentService.getAllAppointment());
    }

    @PostMapping("/create")
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        var appointment = appointmentService.saveAppointment(appointmentDTO);
        return ResponseEntity.ok(appointment);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody AppointmentDTO appointmentDTO) {
        var appointment = appointmentService.updateAppointment(id, appointmentDTO);
        return ResponseEntity.ok(appointment);
    }
    
    @GetMapping("/find-id/{id}")
    public ResponseEntity<Optional<Appointment>> getAppointmentById(@PathVariable Long id) {
        var appointment = appointmentService.findAppointmentById(id);
        if (appointment.isEmpty()) {
            throw new NoSuchElementException("Agendamento não encontrado");
        }
        return ResponseEntity.ok(appointment);
    }

    //Endpoint para buscar agendamentos por ID do usuário
    @GetMapping("/find-user/{userId}")
    public List<Appointment> getAppointmentsByUserId(@PathVariable Long userId) {
        try {
            List<Appointment> appointments = appointmentService.findFullAppointmentsByUserId(userId);

            if (appointments.isEmpty()) {
                throw new NoSuchElementException("Nenhum agendamento encontrado para esse usuário");
            }
            return appointments;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erro ao buscar agendamentos: " + e.getMessage());
    
        }
    }

    @PatchMapping("/{id}/set-priority")
    public ResponseEntity<Appointment> setPriorityCondition(
            @PathVariable("id") Long id,
            @RequestBody PriorityCondition priorityCondition) {
        try {
            Appointment updatedAppointment = appointmentService.setPriorityCondition(id, priorityCondition);
            return ResponseEntity.ok(updatedAppointment);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    //Endpoint para buscar Appointment por intervalo de datas.
    @GetMapping("/between")
    public ResponseEntity<List<Appointment>> getAppointmentBetwenDate(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        var appointment = appointmentService.findByScheduledDateTime(startDate, endDate);

        if (appointment.isEmpty()) {
            throw new NoSuchElementException("Nenhum agendamento encontrado entre essas datas");
        }
        return ResponseEntity.ok(appointment);
    }


    @DeleteMapping("/delete-id/{id}")
    public ResponseEntity<String> deleteAppointmentById(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok("Agendamento removido com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
