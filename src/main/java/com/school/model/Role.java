package com.school.model;

public enum Role {
    ROLE_ADMIN,
    ROLE_TEACHER,
    ROLE_STUDENT;

    public String getSimpleName() {
        return name().replace("ROLE_", "");
    }
}
