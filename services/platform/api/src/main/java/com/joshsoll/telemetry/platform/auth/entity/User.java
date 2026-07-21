package com.joshsoll.telemetry.platform.auth.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import com.joshsoll.telemetry.platform.auth.constants.UserConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Generated
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, length = UserConstants.FIRST_NAME_MAX_LENGTH)
    private String firstName;

    @Column(nullable = false, length = UserConstants.LAST_NAME_MAX_LENGTH)
    private String lastName;

    @Column(nullable = false, unique = true, length = UserConstants.EMAIL_MAX_LENGTH)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String avatarUrl;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected User() {
    }

    public User(
            String firstName,
            String lastName,
            String email,
            String passwordHash,
            String avatarUrl

    ) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.passwordHash = passwordHash;

        Instant now = Instant.now();

        this.createdAt = now;
        this.updatedAt = now;

    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
