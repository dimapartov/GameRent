package org.example.gamerent.models.base;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@MappedSuperclass
public abstract class IdCreatedModified {

    protected Long id;
    protected LocalDateTime created;
    protected LocalDateTime modified;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    public LocalDateTime getCreated() {
        return created;
    }

    @UpdateTimestamp
    @Column(name = "modified", nullable = false)
    public LocalDateTime getModified() {
        return modified;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

}