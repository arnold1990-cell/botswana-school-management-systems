package com.bosams.repository;
import com.bosams.domain.AuditLog;import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
