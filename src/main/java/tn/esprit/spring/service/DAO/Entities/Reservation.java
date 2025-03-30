package tn.esprit.spring.service.DAO.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_RESERVATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reservation implements Serializable {

    @Id
    String idReservation;

    LocalDate anneeUniversitaire;

    boolean estValide;

    // Making the 'etudiants' field private
    @ManyToMany
    @JsonIgnore
    private List<Etudiant> etudiants = new ArrayList<>();
}
