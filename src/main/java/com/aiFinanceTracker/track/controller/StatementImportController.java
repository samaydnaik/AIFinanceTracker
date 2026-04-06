package com.aiFinanceTracker.track.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aiFinanceTracker.track.service.StatementImportService;

@RestController
@RequestMapping("/api/import")
public class StatementImportController {

    private final StatementImportService importService;

    public StatementImportController(StatementImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/sbi")
    public ResponseEntity<String> importSbi(@RequestParam("file") MultipartFile file) {
        try {
            importService.importSbiCsv(file.getInputStream());
            return ResponseEntity.ok("Imported successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Import failed: " + e.getMessage());
        }
    }
}