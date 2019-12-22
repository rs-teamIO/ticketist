package com.siit.ticketist.dto;

import com.siit.ticketist.model.RegisteredUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor
public class RegisterUserDto {

    @NotBlank(message = "Username may not be blank")
    private String username;
    @NotBlank(message = "Password may not be blank")
    private String password;
    @NotBlank(message = "E-mail may not be blank")
    @Email(message = "Invalid E-mail format", regexp = "[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
    private String email;
    @NotBlank(message = "First Name may not be blank")
    private String firstName;
    @NotBlank(message = "Last Name may not be blank")
    private String lastName;

    private String phone;

    private String passwordRepeat;

    public RegisteredUser convertToEntity() {
        RegisteredUser registeredUser = new RegisteredUser();
        return new RegisteredUser(username, password, email, firstName, lastName);
    }

    public RegisterUserDto(RegisteredUser user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
    }

}
