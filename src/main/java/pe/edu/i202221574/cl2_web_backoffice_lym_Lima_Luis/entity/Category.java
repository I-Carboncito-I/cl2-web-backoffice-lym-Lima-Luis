package pe.edu.i202221574.cl2_web_backoffice_lym_Lima_Luis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;
    private String name;

    private Date lastUpdate;

    @OneToMany(mappedBy = "category")
    private List<FilmCategory> filmCategories;
}

