package tn.esprit.spring.service.Services.Reservation;

import tn.esprit.spring.service.DAO.Entities.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface IReservationService {
    Reservation addOrUpdate(Reservation r);
    List<Reservation> findAll();
    Reservation findById(String id);
    void deleteById(String id);
    void delete(Reservation r);
    Reservation ajouterReservationEtAssignerAChambreEtAEtudiant(Long numChambre, long cin);
    long getReservationParAnneeUniversitaire(LocalDate debutAnnee, LocalDate finAnnee);
    String annulerReservation(long cinEtudiant);
    void affectReservationAChambre(String idRes, long idChambre);  // Add this method
    void annulerReservations();  // Add this method
}
