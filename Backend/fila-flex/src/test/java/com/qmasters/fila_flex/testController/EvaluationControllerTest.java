package com.qmasters.fila_flex.testController;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.qmasters.fila_flex.controller.EvaluationController;
import com.qmasters.fila_flex.dto.EvaluationDTO;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.Evaluation;
import com.qmasters.fila_flex.service.EvaluationService;

class EvaluationControllerTest {

    @Mock
    private EvaluationService evaluationService;

    @InjectMocks
    private EvaluationController evaluationController;

    @BeforeEach
    void setUp() {
        // Inicializar mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEvaluation() {
        EvaluationDTO dto = new EvaluationDTO();
        dto.setRating(4);
        dto.setComment("Great service");
        dto.setAppointmentTypeId(1L);

        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setId(1L);

        Evaluation evaluation = new Evaluation();
        evaluation.setRating(4);
        evaluation.setComment("Great service");
        evaluation.setAppointmentType(appointmentType);

        when(evaluationService.addEvaluation(dto)).thenReturn(evaluation);

        EvaluationDTO responseDTO = evaluationController.createEvaluation(dto);

        assertEquals(4, responseDTO.getRating());
        assertEquals("Great service", responseDTO.getComment());
        assertEquals(1L, responseDTO.getAppointmentTypeId());
    }

    @Test
    void testGetAverageRating() {
        when(evaluationService.calculateAverageRating()).thenReturn(4.0);

        ResponseEntity<Double> response = evaluationController.getAverageRating();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(4.0, response.getBody());
    }

    @Test
    void testGetAllEvaluations() {
        Evaluation evaluation = new Evaluation();
        evaluation.setRating(4);
        evaluation.setComment("Good");
        evaluation.setAppointmentType(new AppointmentType());

        when(evaluationService.getAllEvaluations()).thenReturn(ResponseEntity.ok(List.of(evaluation)));

        List<EvaluationDTO> evaluations = evaluationController.listEvaluations();

        assertEquals(1, evaluations.size());
        assertEquals(4, evaluations.get(0).getRating());
        assertEquals("Good", evaluations.get(0).getComment());
    }

    @Test
    void testGetAllEvaluationsWhenResponseBodyIsNull() {
        when(evaluationService.getAllEvaluations()).thenReturn(ResponseEntity.ok(null));

        List<EvaluationDTO> evaluations = evaluationController.listEvaluations();

        assertNotNull(evaluations); // Certifica que a lista não é null
        assertEquals(0, evaluations.size()); // Deve ser uma lista vazia
    }
}