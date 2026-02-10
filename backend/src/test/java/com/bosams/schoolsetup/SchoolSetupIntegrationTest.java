package com.bosams.schoolsetup;

import com.bosams.BoSamsApplication;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = BoSamsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SchoolSetupIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    TestRestTemplate rest;

    @Test
    @SuppressWarnings("unchecked")
    void validationErrorWhenCreatingGradeWithBlankName() {
        var response = rest.postForEntity("/api/v1/school-setup/grades", Map.of("schoolId", 1, "name", "   "), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("detail", "Validation failed");
        var errors = (List<Map<String, String>>) response.getBody().get("errors");
        assertThat(errors).anyMatch(error -> "name".equals(error.get("field")));
    }

    @Test
    void duplicateHouseNameInSameSchoolReturnsConflict() {
        var first = rest.postForEntity("/api/v1/school-setup/houses", Map.of("schoolId", 1, "name", "Red-One"), Map.class);
        var duplicate = rest.postForEntity("/api/v1/school-setup/houses", Map.of("schoolId", 1, "name", "Red-One"), Map.class);

        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(duplicate.getBody()).containsEntry("entity", "House");
        assertThat(duplicate.getBody()).containsEntry("field", "name");
    }

    @Test
    void houseNameCanRepeatAcrossDifferentSchools() {
        var schoolB = rest.postForEntity("/api/v1/school-setup/schools", Map.of("name", "School B"), Map.class);
        var schoolBId = ((Number) schoolB.getBody().get("id")).longValue();

        var houseA = rest.postForEntity("/api/v1/school-setup/houses", Map.of("schoolId", 1, "name", "Red-Two"), Map.class);
        var houseB = rest.postForEntity("/api/v1/school-setup/houses", Map.of("schoolId", schoolBId, "name", "Red-Two"), Map.class);

        assertThat(houseA.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(houseB.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void dbConstraintFallbackReturnsConflictProblemDetail() {
        rest.postForEntity("/api/v1/school-setup/academic-years", Map.of("schoolId", 1, "name", "2028"), Map.class);
        var duplicate = rest.postForEntity("/api/v1/school-setup/academic-years", Map.of("schoolId", 1, "name", "2028"), Map.class);

        assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(duplicate.getBody()).containsEntry("detail", "Request conflicts with existing data.");
        assertThat(duplicate.getBody()).containsEntry("path", "/api/v1/school-setup/academic-years");
    }

    @Test
    void renameGradeToExistingNameReturnsConflict() {
        rest.postForEntity("/api/v1/school-setup/grades", Map.of("schoolId", 1, "name", "Form-X1"), Map.class);
        var g2 = rest.postForEntity("/api/v1/school-setup/grades", Map.of("schoolId", 1, "name", "Form-X2"), Map.class);
        var g2Id = ((Number) g2.getBody().get("id")).longValue();

        ResponseEntity<Map> response = rest.exchange(
                "/api/v1/school-setup/grades/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(Map.of("schoolId", 1, "name", "Form-X1")),
                Map.class,
                g2Id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("entity", "Grade");
        assertThat(response.getBody()).containsEntry("field", "name");
    }
}
