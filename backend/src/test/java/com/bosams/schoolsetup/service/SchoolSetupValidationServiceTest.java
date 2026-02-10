package com.bosams.schoolsetup.service;

import com.bosams.common.DuplicateResourceException;
import com.bosams.schoolsetup.repository.ClassRoomRepository;
import com.bosams.schoolsetup.repository.DemeritCodeRepository;
import com.bosams.schoolsetup.repository.GradeRepository;
import com.bosams.schoolsetup.repository.HouseRepository;
import com.bosams.schoolsetup.repository.MeritCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchoolSetupValidationServiceTest {

    @Mock GradeRepository grades;
    @Mock ClassRoomRepository classes;
    @Mock HouseRepository houses;
    @Mock MeritCodeRepository merits;
    @Mock DemeritCodeRepository demerits;

    @InjectMocks SchoolSetupValidationService service;

    @Test
    void ensureUniqueGradeNameThrowsOnDuplicateCreate() {
        when(grades.existsBySchoolIdAndName(1L, "Form 1")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.ensureUniqueGradeName(1L, "Form 1", null));
    }

    @Test
    void ensureUniqueGradeNameAllowsCurrentRecordOnUpdate() {
        when(grades.existsBySchoolIdAndNameAndIdNot(1L, "Form 1", 7L)).thenReturn(false);
        assertDoesNotThrow(() -> service.ensureUniqueGradeName(1L, "Form 1", 7L));
    }

    @Test
    void ensureUniqueClassCodeThrowsOnDuplicateCreate() {
        when(classes.existsBySchoolIdAndAcademicYearIdAndCode(1L, 2025L, "A1")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.ensureUniqueClassCode(1L, 2025L, "A1", null));
    }

    @Test
    void ensureUniqueHouseNameThrowsOnDuplicateUpdate() {
        when(houses.existsBySchoolIdAndNameAndIdNot(1L, "Blue", 2L)).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.ensureUniqueHouseName(1L, "Blue", 2L));
    }
}
