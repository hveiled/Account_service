package account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@ToString
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "\\w+@acme.com")
    private String employee;    //user email

    @NotNull
    @Pattern(regexp = "^((0[1-9])|(1[0-2]))-(\\d{4})$", message = "Wrong date!")
    private String period;

    @NotNull
    @Min(value = 0, message = "Salary must be non negative!")
    private Long salary;

//    @JsonIgnore
    @Column(unique = true)
    private String employeePeriod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    @JsonIgnore
    public String getEmployeePeriod() {
        return employeePeriod;
    }

    public void setEmployeePeriod(String employeePeriod) {
        this.employeePeriod = employeePeriod;
    }
}