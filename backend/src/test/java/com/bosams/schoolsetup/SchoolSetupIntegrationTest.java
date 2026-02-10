package com.bosams.schoolsetup;

import com.bosams.BoSamsApplication;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = BoSamsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SchoolSetupIntegrationTest {
    @Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource static void props(DynamicPropertyRegistry r){ r.add("spring.datasource.url", postgres::getJdbcUrl); r.add("spring.datasource.username", postgres::getUsername); r.add("spring.datasource.password", postgres::getPassword); }
    @Autowired TestRestTemplate rest;

    @Test void createSchool(){
        var body = Map.of("name","New School","address","Maun");
        var res=rest.postForEntity("/api/v1/school-setup/schools",body,Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).containsEntry("name","New School");
    }

    @Test void createAcademicYearAndTerms(){
        var y=rest.postForEntity("/api/v1/school-setup/academic-years", Map.of("schoolId",1,"name","2026"), Map.class);
        assertThat(y.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long id=((Number)y.getBody().get("id")).longValue();
        var t=rest.postForEntity("/api/v1/school-setup/terms", Map.of("schoolId",1,"academicYearId",id,"name","Term 1"), Map.class);
        assertThat(t.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test void uniquenessFailureGrade(){
        rest.postForEntity("/api/v1/school-setup/grades", Map.of("schoolId",1,"name","Form 1"), Map.class);
        var dup=rest.postForEntity("/api/v1/school-setup/grades", Map.of("schoolId",1,"name","Form 1"), String.class);
        assertThat(dup.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test void listHousesPagination(){
        for(int i=0;i<3;i++) rest.postForEntity("/api/v1/school-setup/houses", Map.of("schoolId",1,"name","HouseX"+i), Map.class);
        var res=rest.getForEntity("/api/v1/school-setup/schools/1/houses?page=0&size=2", Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((List<?>)res.getBody().get("content")).hasSize(2);
    }

    @Test void createGradeAndClassMapping(){
        var y=rest.postForEntity("/api/v1/school-setup/academic-years", Map.of("schoolId",1,"name","2027"), Map.class);
        var g=rest.postForEntity("/api/v1/school-setup/grades", Map.of("schoolId",1,"name","Form 7"), Map.class);
        Long yid=((Number)y.getBody().get("id")).longValue(); Long gid=((Number)g.getBody().get("id")).longValue();
        var c=rest.postForEntity("/api/v1/school-setup/classes", Map.of("schoolId",1,"academicYearId",yid,"gradeId",gid,"code","F7A","name","Form 7 A"), Map.class);
        assertThat(c.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(c.getBody()).containsEntry("gradeId", gid.intValue());
    }
}
