package com.bosams.schoolsetup.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "schools")
public class School extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(length = 255)
    private String address;
    @Column(length = 100)
    private String contactEmail;
    @Column(length = 40)
    private String contactPhone;
    @Column(length = 255)
    private String logoUrl;
    @Column(length = 255)
    private String documentHeader;
    @Column(length = 100)
    private String reportLanguages;
    @Column(length = 100)
    private String defaultTermStructure;
    @Column(length = 100)
    private String documentNumberFormat;
    public Long getId(){return id;} public String getName(){return name;} public void setName(String n){name=n;}
    public String getAddress(){return address;} public void setAddress(String a){address=a;}
    public String getContactEmail(){return contactEmail;} public void setContactEmail(String c){contactEmail=c;}
    public String getContactPhone(){return contactPhone;} public void setContactPhone(String c){contactPhone=c;}
    public String getLogoUrl(){return logoUrl;} public void setLogoUrl(String l){logoUrl=l;}
    public String getDocumentHeader(){return documentHeader;} public void setDocumentHeader(String d){documentHeader=d;}
    public String getReportLanguages(){return reportLanguages;} public void setReportLanguages(String r){reportLanguages=r;}
    public String getDefaultTermStructure(){return defaultTermStructure;} public void setDefaultTermStructure(String d){defaultTermStructure=d;}
    public String getDocumentNumberFormat(){return documentNumberFormat;} public void setDocumentNumberFormat(String d){documentNumberFormat=d;}
}
