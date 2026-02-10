package com.bosams.learnerparent;

import com.bosams.BoSamsApplication;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = BoSamsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LearnerParentIntegrationTest {
    @Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource static void props(DynamicPropertyRegistry r){r.add("spring.datasource.url",postgres::getJdbcUrl);r.add("spring.datasource.username",postgres::getUsername);r.add("spring.datasource.password",postgres::getPassword);}    
    @Autowired TestRestTemplate rest;

    @Test void admissionTransitionAndInvalidTransition409(){
        var created = rest.postForEntity("/api/v1/admissions/applications", Map.of("schoolId",1,"applicationNo","APP-1","preferredGradeId",1,"preferredAcademicYearId",1,"learnerFirstName","A","learnerLastName","B"), Map.class);
        Long id=((Number)created.getBody().get("id")).longValue();
        var approved = rest.postForEntity("/api/v1/admissions/applications/"+id+"/approve", Map.of("decisionNote","ok"), Map.class);
        var invalid = rest.postForEntity("/api/v1/admissions/applications/"+id+"/reject", Map.of(), Map.class);
        assertThat(approved.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(invalid.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test void learnerArchiveRestoreAndInvalidRestore(){
        Long learnerId = createLearner("L-1",1L,1L,1L);
        var archived=rest.postForEntity("/api/v1/learners/"+learnerId+"/archive",Map.of("reason","moved"),Map.class);
        var restored=rest.postForEntity("/api/v1/learners/"+learnerId+"/restore",null,Map.class);
        var invalid=rest.postForEntity("/api/v1/learners/"+learnerId+"/restore",null,Map.class);
        assertThat(archived.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(restored.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(invalid.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test void parentLinkAndPrimaryContactRule(){
        Long learnerId=createLearner("L-2",1L,1L,1L);
        Long p1=createParent("P-1"); Long p2=createParent("P-2");
        var link1=rest.postForEntity("/api/v1/learners/"+learnerId+"/parents/link",Map.of("schoolId",1,"parentId",p1,"relationshipType","MOTHER","isPrimaryContact",true,"livesWithLearner",true),Map.class);
        var link2=rest.postForEntity("/api/v1/learners/"+learnerId+"/parents/link",Map.of("schoolId",1,"parentId",p2,"relationshipType","FATHER","isPrimaryContact",true,"livesWithLearner",true),Map.class);
        assertThat(link1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(link2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test void attendanceBulkAndDuplicate409(){
        Long learnerId=createLearner("L-3",1L,1L,1L);
        var first=rest.postForEntity("/api/v1/attendance/learners",Map.of("schoolId",1,"academicYearId",1,"termId",1,"date", LocalDate.now().toString(),"period",1,"records",List.of(Map.of("learnerId",learnerId,"status","PRESENT","capturedBy","t"))),Object.class);
        var dup=rest.postForEntity("/api/v1/attendance/learners",Map.of("schoolId",1,"academicYearId",1,"termId",1,"date", LocalDate.now().toString(),"period",1,"records",List.of(Map.of("learnerId",learnerId,"status","ABSENT","capturedBy","t"))),Map.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(dup.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test void disciplineSummaryAggregates(){
        Long learnerId=createLearner("L-4",1L,1L,1L);
        rest.postForEntity("/api/v1/learners/"+learnerId+"/discipline",Map.of("schoolId",1,"entryDate",LocalDate.now().toString(),"entryType","DEMERIT","codeId",1,"points",5),Map.class);
        rest.postForEntity("/api/v1/learners/"+learnerId+"/discipline",Map.of("schoolId",1,"entryDate",LocalDate.now().toString(),"entryType","MERIT","codeId",1,"points",2),Map.class);
        var summary=rest.getForEntity("/api/v1/reports/discipline/summary?schoolId=1&from="+LocalDate.now()+"&to="+LocalDate.now(),Map.class);
        assertThat(summary.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(summary.getBody()).containsKey("pointsDistribution");
    }

    @Test void enrolmentCountsByGradeClass(){
        createLearner("L-5",1L,1L,1L); createLearner("L-6",1L,1L,1L);
        var report=rest.getForEntity("/api/v1/reports/enrolment?schoolId=1&academicYearId=1",Map.class);
        assertThat(report.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Number) report.getBody().get("grandTotal")).intValue()).isGreaterThanOrEqualTo(2);
    }

    private Long createLearner(String no, Long yearId, Long gradeId, Long classId){
        var r=rest.postForEntity("/api/v1/learners",Map.of("schoolId",1,"learnerNo",no,"firstName","Test","lastName",no,"currentAcademicYearId",yearId,"currentGradeId",gradeId,"currentClassRoomId",classId),Map.class);
        return ((Number)r.getBody().get("id")).longValue();
    }
    private Long createParent(String no){
        var r=rest.postForEntity("/api/v1/parents",Map.of("schoolId",1,"parentNo",no,"firstName","P","lastName",no,"phone","+267"),Map.class);
        return ((Number)r.getBody().get("id")).longValue();
    }
}
