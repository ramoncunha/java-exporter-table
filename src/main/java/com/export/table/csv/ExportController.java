package com.export.table.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private static final Logger log = LoggerFactory.getLogger(ExportController.class);

    private ExportService exportService;

    /**
     * Endpoint simplificado que inicia a exportação das tabelas fixas
     */
    @PostMapping("/run")
    public ResponseEntity<ExportResponse> triggerExport() {
        log.info("Recebido pedido de exportação das tabelas");

        String jobId = UUID.randomUUID().toString();

        // Iniciar a exportação de forma assíncrona
        CompletableFuture.runAsync(() -> exportService.exportTablesToS3(jobId, request.getTables()));

        return ResponseEntity.accepted()
                .body(new ExportResponse(jobId, "Exportação iniciada com sucesso", request.getTables()));
    }
}
