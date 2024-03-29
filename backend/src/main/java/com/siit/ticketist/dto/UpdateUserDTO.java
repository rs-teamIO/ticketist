package com.siit.ticketist.dto;

import com.siit.ticketist.model.RegisteredUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor
public class UpdateUserDTO {

    @NotBlank(message = "Username may not be blank")
    private String username;
    @NotBlank(message = "Old password may not be blank")
    private String oldPassword;

    private String newPassword;

    private String newPasswordRepeat;

    @NotBlank(message = "E-mail may not be blank")
    @Email(message = "Invalid E-mail format", regexp = "[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
    private String email;
    @NotBlank(message = "First Name may not be blank")
    private String firstName;
    @NotBlank(message = "Last Name may not be blank")
    private String lastName;

    private String phone;

    public RegisteredUser convertToEntity() {
        RegisteredUser registeredUser = new RegisteredUser(username, oldPassword, email, firstName, lastName);
        registeredUser.setPhone(this.getPhone());

        return registeredUser;
    }
}
