package account.controller;

import account.model.Payment;
import account.model.Payroll;
import account.model.User;
import account.service.AccountService;
import account.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Validated
@RestController
public class AccountController {

    @Autowired
    UserAuthenticationService service;

    @Autowired
    AccountService accountService;

    @GetMapping("/api/empl/payment")
    public Object payment(
            @RequestParam(required = false) @Pattern(regexp = "^((0[1-9])|(1[0-2]))-(\\d{4})$") String period) {
        return accountService.getPayroll(period);
    }

    @PostMapping("/api/acct/payments")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> uploadPayroll(@RequestBody List<@Valid Payment> payroll) throws SQLException {
        accountService.savePayroll(payroll);
        return Map.of("status", "Added successfully!");
    }

    @PutMapping("/api/acct/payments")
    public Map<String, String> updatePayments(@RequestBody @Valid Payment payment) {
        accountService.changePayment(payment);
        return Map.of("status", "Updated successfully!");
    }
}


