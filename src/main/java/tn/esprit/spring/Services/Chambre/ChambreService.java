package tn.esprit.spring.Services.Chambre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ChambreService implements IChambreService {
    ChambreRepository repo;
    BlocRepository blocRepository;

    private static final String PLACE_AVAILABLE_MESSAGE = "Le nombre de place disponible pour la chambre ";
    private static final String ROOM_FULL_MESSAGE = "La chambre ";

    @Override
    public Chambre addOrUpdate(Chambre c) {
        return repo.save(c);
    }

    @Override
    public List<Chambre> findAll() {
        return repo.findAll();
    }

    @Override
    public Chambre findById(long id) {
        return repo.findById(id).orElse(null);  // Handle Optional correctly
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Chambre c) {
        repo.delete(c);
    }

    @Override
    public List<Chambre> getChambresParNomBloc(String nomBloc) {
        return repo.findByBlocNomBloc(nomBloc);
    }

    @Override
    public long nbChambreParTypeEtBloc(TypeChambre type, long idBloc) {
        return repo.countByTypeCAndBlocIdBloc(type, idBloc);
    }

    @Override
    public List<Chambre> getChambresNonReserveParNomFoyerEtTypeChambre(String nomFoyer, TypeChambre type) {
        LocalDate[] academicYearDates = getAcademicYearDates();
        List<Chambre> listChambreDispo = new ArrayList<>();

        for (Chambre c : repo.findAll()) {
            if (c.getTypeC().equals(type) && c.getBloc().getFoyer().getNomFoyer().equals(nomFoyer)) {
                long numReservation = countReservationsInAcademicYear(c, academicYearDates);
                if (isRoomAvailable(c, numReservation)) {
                    listChambreDispo.add(c);
                }
            }
        }
        return listChambreDispo;
    }

    @Override
    public void listeChambresParBloc() {
        for (Bloc b : blocRepository.findAll()) {
            log.info("Bloc => " + b.getNomBloc() + " ayant une capacité " + b.getCapaciteBloc());
            if (!b.getChambres().isEmpty()) {  // Check if the list is not empty
                log.info("La liste des chambres pour ce bloc: ");
                b.getChambres().forEach(c -> log.info("NumChambre: " + c.getNumeroChambre() + " type: " + c.getTypeC()));
            } else {
                log.info("Pas de chambre disponible dans ce bloc");
            }
            log.info("********************");
        }
    }

    @Override
    public void pourcentageChambreParTypeChambre() {
        long totalChambre = repo.count();
        double pSimple = (double) (repo.countChambreByTypeC(TypeChambre.SIMPLE) * 100) / totalChambre;
        double pDouble = (double) (repo.countChambreByTypeC(TypeChambre.DOUBLE) * 100) / totalChambre;
        double pTriple = (double) (repo.countChambreByTypeC(TypeChambre.TRIPLE) * 100) / totalChambre;
        log.info("Nombre total des chambre: " + totalChambre);
        log.info("Le pourcentage des chambres pour le type SIMPLE est égale à " + pSimple);
        log.info("Le pourcentage des chambres pour le type DOUBLE est égale à " + pDouble);
        log.info("Le pourcentage des chambres pour le type TRIPLE est égale à " + pTriple);
    }

    @Override
    public void nbPlacesDisponibleParChambreAnneeEnCours() {
        LocalDate[] academicYearDates = getAcademicYearDates();

        for (Chambre c : repo.findAll()) {
            long nbReservation = repo.countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(
                    c.getIdChambre(), true, academicYearDates[0], academicYearDates[1]);

            logRoomAvailability(c, nbReservation);
        }
    }

    private LocalDate[] getAcademicYearDates() {
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
        return new LocalDate[] { dateDebutAU, dateFinAU };
    }

    private long countReservationsInAcademicYear(Chambre c, LocalDate[] academicYearDates) {
        return c.getReservations().stream()
                .filter(reservation -> reservation.getAnneeUniversitaire().isAfter(academicYearDates[0]) &&
                        reservation.getAnneeUniversitaire().isBefore(academicYearDates[1]))
                .count();
    }

    private boolean isRoomAvailable(Chambre c, long numReservation) {
        switch (c.getTypeC()) {
            case SIMPLE: return numReservation == 0;
            case DOUBLE: return numReservation < 2;
            case TRIPLE: return numReservation < 3;
            default: return false;
        }
    }

    private void logRoomAvailability(Chambre c, long nbReservation) {
        switch (c.getTypeC()) {
            case SIMPLE:
                logRoomMessage(c, nbReservation, 1);
                break;
            case DOUBLE:
                logRoomMessage(c, nbReservation, 2);
                break;
            case TRIPLE:
                logRoomMessage(c, nbReservation, 3);
                break;
        }
    }

    private void logRoomMessage(Chambre c, long nbReservation, int maxReservation) {
        if (nbReservation < maxReservation) {
            log.info(PLACE_AVAILABLE_MESSAGE + c.getTypeC() + " " + c.getNumeroChambre() + " est " + (maxReservation - nbReservation) + " place(s) disponible");
        } else {
            log.info(ROOM_FULL_MESSAGE + c.getTypeC() + " " + c.getNumeroChambre() + " est complete");
        }
    }
}
