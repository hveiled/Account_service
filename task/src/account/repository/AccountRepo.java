package account.repository;

import account.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Payment, Long> {

    @Override
    <S extends Payment> List<S> saveAll(Iterable<S> entities);
    List<Payment> findAllByEmployeeOrderByIdDesc(String employeeEmail);
    List<Payment> findAllByPeriodAndEmployeeOrderByIdDesc(String period, String name);
    boolean existsByEmployeePeriod(String employeePeriod);
    Optional<Payment> findPaymentByEmployeeAndPeriod(String employee, String period);
}
