package com.bosams.repository;
import com.bosams.domain.RefreshToken;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> { Optional<RefreshToken> findByTokenAndRevokedFalse(String token); List<RefreshToken> findByUserIdAndRevokedFalse(Long userId); }
