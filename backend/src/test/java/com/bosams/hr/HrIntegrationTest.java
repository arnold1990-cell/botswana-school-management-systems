package com.bosams.hr;

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
class HrIntegrationTest {
    @Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource static void props(DynamicPropertyRegistry r){r.add("spring.datasource.url",postgres::getJdbcUrl);r.add("spring.datasource.username",postgres::getUsername);r.add("spring.datasource.password",postgres::getPassword);}    
    @Autowired TestRestTemplate rest;

    @Test void createStaffEducator201(){
        var created = rest.postForEntity("/api/v1/hr/staff", Map.of("schoolId",1,"staffNumber","EMP-1","staffType","EDUCATOR","firstName","Ada","lastName","M"), Map.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).containsEntry("staffNumber", "EMP-1");
    }

    @Test void archiveStaffAndCannotArchiveTwice(){
        Long id = createStaff("EMP-2","EDUCATOR");
        var archived = rest.postForEntity("/api/v1/hr/staff/"+id+"/archive", Map.of("reason","retired"), Map.class);
        var duplicate = rest.postForEntity("/api/v1/hr/staff/"+id+"/archive", Map.of("reason","again"), Map.class);
        assertThat(archived.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test void registerTeacherUniquePerClassYear(){
        Long s1 = createStaff("EMP-3","EDUCATOR"); Long s2 = createStaff("EMP-4","EDUCATOR");
        Long gradeId = ((Number)rest.postForEntity("/api/v1/school-setup/grades", Map.of("schoolId",1,"name","Form X"), Map.class).getBody().get("id")).longValue();
        Long classId = ((Number)rest.postForEntity("/api/v1/school-setup/classes", Map.of("schoolId",1,"academicYearId",1,"gradeId",gradeId,"code","X-1","name","X 1"), Map.class).getBody().get("id")).longValue();
        var ok = rest.postForEntity("/api/v1/hr/register-classes/assign", Map.of("schoolId",1,"staffId",s1,"classRoomId",classId,"academicYearId",1,"role","REGISTER_TEACHER"), Map.class);
        var dup = rest.postForEntity("/api/v1/hr/register-classes/assign", Map.of("schoolId",1,"staffId",s2,"classRoomId",classId,"academicYearId",1,"role","REGISTER_TEACHER"), Map.class);
        assertThat(ok.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(dup.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test void attendanceDuplicateSameDate409(){
        Long s = createStaff("EMP-5","EDUCATOR");
        var d = LocalDate.now().toString();
        var ok = rest.postForEntity("/api/v1/hr/attendance", Map.of("schoolId",1,"date",d,"staffId",s,"status","PRESENT"), Object.class);
        var dup = rest.postForEntity("/api/v1/hr/attendance", Map.of("schoolId",1,"date",d,"staffId",s,"status","ABSENT"), Map.class);
        assertThat(ok.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(dup.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test void leaveWorkflowApproveThenRejectConflict(){
        Long s = createStaff("EMP-6","EDUCATOR");
        var created = rest.postForEntity("/api/v1/hr/leave-requests", Map.of("schoolId",1,"staffId",s,"leaveType","ANNUAL","startDate",LocalDate.now().toString(),"endDate",LocalDate.now().plusDays(1).toString(),"reason","trip"), Map.class);
        Long id = ((Number)created.getBody().get("id")).longValue();
        var approved = rest.postForEntity("/api/v1/hr/leave-requests/"+id+"/approve", Map.of("note","ok"), Map.class);
        var rejected = rest.postForEntity("/api/v1/hr/leave-requests/"+id+"/reject", Map.of("note","late"), Map.class);
        assertThat(approved.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(rejected.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }


    @Test void leaveRequestRejectsSchoolMismatch(){
        Long schoolB=((Number)rest.postForEntity("/api/v1/school-setup/schools", Map.of("name","School B HR"), Map.class).getBody().get("id")).longValue();
        Long s = ((Number)rest.postForEntity("/api/v1/hr/staff", Map.of("schoolId",schoolB,"staffNumber","EMP-X","staffType","EDUCATOR","firstName","X","lastName","Y"), Map.class).getBody().get("id")).longValue();
        var response = rest.postForEntity("/api/v1/hr/leave-requests", Map.of("schoolId",1,"staffId",s,"leaveType","ANNUAL","startDate",LocalDate.now().toString(),"endDate",LocalDate.now().toString(),"reason","trip"), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test void dashboardSummaryCounts(){
        Long s1 = createStaff("EMP-7","EDUCATOR"); Long s2 = createStaff("EMP-8","NON_TEACHING");
        var d = LocalDate.now().toString();
        rest.postForEntity("/api/v1/hr/attendance", Map.of("schoolId",1,"date",d,"records", List.of(Map.of("staffId",s1,"status","PRESENT"), Map.of("staffId",s2,"status","ABSENT"))), Map.class);
        rest.postForEntity("/api/v1/hr/leave-requests", Map.of("schoolId",1,"staffId",s2,"leaveType","SICK","startDate",d,"endDate",d,"reason","flu"), Map.class);
        var summary = rest.getForEntity("/api/v1/hr/dashboard/summary?schoolId=1&from="+d+"&to="+d, Map.class);
        assertThat(summary.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Number)summary.getBody().get("totalStaff")).longValue()).isGreaterThanOrEqualTo(2L);
        assertThat(((Number)summary.getBody().get("pendingLeaveRequests")).longValue()).isGreaterThanOrEqualTo(1L);
    }

    private Long createStaff(String no, String type){
        var created = rest.postForEntity("/api/v1/hr/staff", Map.of("schoolId",1,"staffNumber",no,"staffType",type,"firstName",no,"lastName",no), Map.class);
        return ((Number)created.getBody().get("id")).longValue();
    }
}
