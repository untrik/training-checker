package edu.rutmiit.audit_service.controller;

import edu.rutmiit.audit_service.model.AuditEntry;
import edu.rutmiit.audit_service.storage.AuditStorage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditStorage auditStorage;

    public AuditController(AuditStorage auditStorage) {
        this.auditStorage = auditStorage;
    }
    /**
     * Возвращает последние аудит-записи.
     *
     * Пример: GET /api/audit?limit=50
     */
    @GetMapping
    public Map<String, Object> getAuditLog(
            @RequestParam(defaultValue = "100") int limit) {

        List<AuditEntry> entries = auditStorage.findLatest(limit);

        return Map.of(
                "totalEntries", auditStorage.count(),
                "showing", entries.size(),
                "entries", entries
        );
    }
}
