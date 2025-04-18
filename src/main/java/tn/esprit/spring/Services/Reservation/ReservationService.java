package tn.esprit.spring.Services.Reservation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Etudiant;

import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;

import tn.esprit.spring.DAO.Repositories.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService implements IReservationService {
    ReservationRepository repo;
    ChambreRepository chambreRepository;
    EtudiantRepository etudiantRepository;

    @Override
    public Reservation addOrUpdate(Reservation r) {
        return repo.save(r);
    }

    @Override
    public List<Reservation> findAll() {
        return repo.findAll();
    }

    @Override
    public Reservation findById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée avec l'id : " + id));
    }


    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Reservation r) {
        repo.delete(r);
    }

    @Override
    public Reservation ajouterReservationEtAssignerAChambreEtAEtudiant(Long numChambre, long cin) {
        LocalDate dateDebutAU;
        LocalDate dateFinAU;
        int year = LocalDate.now().getYear() % 100;

        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }

        Chambre c = chambreRepository.findByNumeroChambre(numChambre);
        if (c == null) {
            throw new IllegalArgumentException("Chambre non trouvée avec le numéro : " + numChambre);
        }

        Etudiant e = etudiantRepository.findByCin(cin);
        if (e == null) {
            throw new IllegalArgumentException("Etudiant non trouvé avec le CIN : " + cin);
        }

        int numRes = chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(
                c.getIdChambre(), dateDebutAU, dateFinAU
        );
        log.warn("Nombre de réservations existantes pour la chambre {} : {}", c.getNumeroChambre(), numRes);

        boolean ajout = false;
        switch (c.getTypeC()) {
            case SIMPLE:
                ajout = numRes < 1;
                if (!ajout) log.info("Chambre simple remplie !");
                break;
            case DOUBLE:
                ajout = numRes < 2;
                if (!ajout) log.info("Chambre double remplie !");
                break;
            case TRIPLE:
                ajout = numRes < 3;
                if (!ajout) log.info("Chambre triple remplie !");
                break;
        }

        Reservation res = new Reservation();
        if (ajout) {
            String idReservation = dateDebutAU.getYear() + "/" + dateFinAU.getYear() + "-" +
                    c.getBloc().getNomBloc() + "-" + c.getNumeroChambre() + "-" + e.getCin();

            res.setIdReservation(idReservation);
            res.setAnneeUniversitaire(LocalDate.now());
            res.setEstValide(true);
            res.getEtudiants().add(e);

            res = repo.save(res);
            c.getReservations().add(res);
            chambreRepository.save(c);

            log.info("Réservation créée avec succès : {}", idReservation);
        } else {
            log.warn("Aucune réservation créée car la chambre est pleine.");
        }

        return res;
    }


    @Override
    public long getReservationParAnneeUniversitaire(LocalDate debutAnnee, LocalDate finAnnee) {
        return repo.countByAnneeUniversitaireBetween(debutAnnee, finAnnee);
    }

    @Override
    public String annulerReservation(long cinEtudiant) {
        Reservation r = repo.findByEtudiantsCinAndEstValide(cinEtudiant, true);
        Chambre c = chambreRepository.findByReservationsIdReservation(r.getIdReservation());
        c.getReservations().remove(r);
        chambreRepository.save(c);
        repo.delete(r);
        return "La réservation " + r.getIdReservation() + " est annulée avec succés";
    }

    @Override
    public void affectReservationAChambre(String idRes, long idChambre) {
        Optional<Reservation> optionalReservation = repo.findById(idRes);
        Optional<Chambre> optionalChambre = chambreRepository.findById(idChambre);

        if (optionalReservation.isPresent() && optionalChambre.isPresent()) {
            Reservation r = optionalReservation.get();
            Chambre c = optionalChambre.get();

            c.getReservations().add(r);
            chambreRepository.save(c);
        } else {
            StringBuilder errorMsg = new StringBuilder("Erreur d'affectation : ");
            if (optionalReservation.isEmpty()) {
                errorMsg.append("Réservation non trouvée avec l'id: ").append(idRes).append(". ");
            }
            if (optionalChambre.isEmpty()) {
                errorMsg.append("Chambre non trouvée avec l'id: ").append(idChambre).append(".");
            }
            throw new IllegalArgumentException(errorMsg.toString());
        }
    }


    @Override
    public void annulerReservations() {

        LocalDate dateDebutAU;
        LocalDate dateFinAU;

        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }

        for (Reservation reservation : repo.findByEstValideAndAnneeUniversitaireBetween(true, dateDebutAU, dateFinAU)) {
            reservation.setEstValide(false);
            repo.save(reservation);
            log.info("La reservation "+ reservation.getIdReservation()+" est annulée automatiquement");
        }
    }

}
