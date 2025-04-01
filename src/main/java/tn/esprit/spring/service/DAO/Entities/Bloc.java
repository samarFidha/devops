package tn.esprit.spring.service.DAO.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_BLOC")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bloc implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long idBloc;

    String nomBloc;
    long capaciteBloc;

    @ManyToOne
    @JsonIgnore
    Foyer foyer;

    @OneToMany(mappedBy = "bloc", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Chambre> chambres = new ArrayList<>();


    // ✅ Custom builder to ensure `chambres` is always initialized
    @Builder
    public Bloc(Long idBloc, String nomBloc, long capaciteBloc, Foyer foyer, List<Chambre> chambres) {
        this.idBloc = (idBloc != null) ? idBloc : 0L;  // Assign default if null
        this.nomBloc = nomBloc;
        this.capaciteBloc = capaciteBloc;
        this.foyer = foyer;
        this.chambres = (chambres != null) ? chambres : new ArrayList<>();
    }
}
