package pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer actorId;
    private String firstName;
    private String lastName;

    private Date lastUpdate;
}



