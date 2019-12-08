package com.siit.ticketist.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;

/**
 * Represents the system administrator.
 */
@Entity
@NoArgsConstructor
public class Admin extends User {

    public Admin(String username, String email, String password, String firstName, String lastName) {
        super(username, password, email, firstName, lastName);
        this.authorities.add(Role.ADMIN);
    }
}