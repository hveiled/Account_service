package account.service;

import account.model.Payment;
import account.model.Payroll;
import account.model.UserDetailsImpl;
import account.repository.AccountRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountService {

    @Autowired
    AccountRepo paymentRepo;
    Logger logger = Logger.getGlobal();

    @Transactional
    public void savePayroll(List<Payment> paymentList) throws SQLException {
        paymentList = paymentList.stream()
                .peek(payment -> payment.setEmployeePeriod(payment.getEmployee() + payment.getPeriod()))
                .collect(Collectors.toList());
        for (Payment payment : paymentList) {
            if (paymentRepo.existsByEmployeePeriod(payment.getEmployeePeriod())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Provided period is not unique: " + payment.getPeriod());
            }
            paymentRepo.save(payment);
        }
    }

    public void changePayment(Payment payment) {
        Payment foundPayment = paymentRepo.findPaymentByEmployeeAndPeriod(payment.getEmployee(), payment.getPeriod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Payment not found: " + payment.toString()));
        foundPayment.setSalary(payment.getSalary());
        paymentRepo.save(foundPayment);
    }

    public Object getPayroll(String period) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication().getPrincipal();
        List<Payment> paymentsList = null;
        if (period == null) {
            paymentsList = paymentRepo.findAllByEmployeeOrderByIdDesc(userDetails.getUsername());
        } else {
            paymentsList = paymentRepo.findAllByPeriodAndEmployeeOrderByIdDesc(period, userDetails.getUsername());
        }
        List<Payroll> payrolls = paymentsList.stream()
                .map(payment -> {
                    Payroll payroll = new Payroll();
                    payroll.setName(userDetails.getName());
                    payroll.setLastname(userDetails.getLastname());
                    try {
                        SimpleDateFormat format1 = new SimpleDateFormat("MM-yyyy");
                        SimpleDateFormat format2 = new SimpleDateFormat("MMMMM-yyyy");
                        Date date = format1.parse(payment.getPeriod());
                        payroll.setPeriod(format2.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    payroll.setSalary("" + payment.getSalary()/100 + " dollar(s) " + payment.getSalary()%100 + " cent(s)");
                    return payroll;
                }).collect(Collectors.toList());
        if (payrolls.size() != 1) {
            return payrolls;
        }
        return payrolls.get(0);
    }
}
