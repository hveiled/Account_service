package account.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Size;

public class NewPassword {

    @Size(min = 12, message = "The password length must be at least 12 symbols!")
    @JsonProperty("new_password")
    private String password;

    public NewPassword(String password) {
        this.password = password;
    }

    public NewPassword() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
