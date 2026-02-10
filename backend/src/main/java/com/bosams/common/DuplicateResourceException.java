package com.bosams.common;

public class DuplicateResourceException extends RuntimeException {
    private final String entityName;
    private final String fieldName;
    private final String conflictingValue;
    private final Long schoolId;

    public DuplicateResourceException(String entityName, String fieldName, String conflictingValue, Long schoolId) {
        super("Duplicate " + entityName + " for " + fieldName + "='" + conflictingValue + "' in schoolId=" + schoolId);
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.conflictingValue = conflictingValue;
        this.schoolId = schoolId;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getConflictingValue() {
        return conflictingValue;
    }

    public Long getSchoolId() {
        return schoolId;
    }
}
