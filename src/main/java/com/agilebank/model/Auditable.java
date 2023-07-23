package com.agilebank.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.util.Date;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U> {

    @Column(name = "created_by")
    @CreatedBy
    protected U createdBy;

    @Column(name = "last_modified_by")
    @LastModifiedBy
    protected U lastModifiedBy;

    @Column(name = "created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @CreatedDate
    protected Date createdDate;

    @Column(name = "last_modified_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @LastModifiedDate
    protected Date lastModifiedDate;
}
