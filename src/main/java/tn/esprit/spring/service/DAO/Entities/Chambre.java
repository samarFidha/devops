package tn.esprit.spring.service.DAO.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_CHAMBRE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chambre implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long idChambre;

    @Column(unique = true)
    long numeroChambre;

    @Enumerated(EnumType.STRING)
    TypeChambre typeC;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    Bloc bloc;

    // Making the field 'private' as suggested.
    @OneToMany
    private List<Reservation> reservations = new ArrayList<>();
}
