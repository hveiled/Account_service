package account.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserAccess {

    @NotEmpty
    private String user;
    private String operation;
}
