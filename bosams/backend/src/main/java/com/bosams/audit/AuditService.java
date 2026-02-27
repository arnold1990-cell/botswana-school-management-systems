package com.bosams.audit;

import com.bosams.domain.AuditLog;
import com.bosams.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository repository;
    public AuditService(AuditLogRepository repository) { this.repository = repository; }
    public void log(Long actorUserId, String action, String entity, String entityId, String note) {
        AuditLog log = new AuditLog();
        log.setActorUserId(actorUserId); log.setAction(action); log.setEntity(entity); log.setEntityId(entityId); log.setNote(note);
        repository.save(log);
    }
}
