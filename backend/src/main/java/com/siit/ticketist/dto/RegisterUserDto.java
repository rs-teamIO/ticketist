package com.siit.ticketist.dto;

import com.siit.ticketist.model.RegisteredUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor
public class RegisterUserDto {

    @NotBlank(message = "Username may not be blank")
    private String username;
    @NotBlank(message = "Password may not be blank")
    private String password;
    private String passwordRepeat;
    @NotBlank(message = "E-mail may not be blank")
    @Email(message = "Invalid E-mail format", regexp = "[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
    private String email;
    @NotBlank(message = "First Name may not be blank")
    private String firstName;
    @NotBlank(message = "Last Name may not be blank")
    private String lastName;

    private String phone;

    public RegisterUserDto(RegisteredUser registeredUser) {
        this.username = registeredUser.getUsername();
        this.email = registeredUser.getEmail();
        this.firstName = registeredUser.getFirstName();
        this.lastName = registeredUser.getLastName();
        this.phone = registeredUser.getPhone();
    }

    public RegisteredUser convertToEntity() {
        return new RegisteredUser(username, password, email, firstName, lastName);
    }
}
