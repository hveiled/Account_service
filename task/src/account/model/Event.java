package account.model;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
public class Event {

    @Id
    @GeneratedValue
    public Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "action")
    private String action;

    @Column(name = "message")
    private String message;

    @Column(name = "subject")
    private String subject;

    @Column(name = "object")
    private String object;

    @Column(name = "path")
    private String path;
}
