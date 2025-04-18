package tn.esprit.spring.Services.Chambre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
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

    private static final String DISPONIBLE_PREFIX = "Le nombre de place disponible pour la chambre ";
    private static final String CHAMBRE_PREFIX = "La chambre ";
    private static final String COMPLETE_SUFFIX = " est complete";

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
        return repo.findById(id).orElse(null);
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

        List<Chambre> listChambreDispo = new ArrayList<>();
        for (Chambre c : repo.findAll()) {
            if (c.getTypeC().equals(type) && c.getBloc().getFoyer().getNomFoyer().equals(nomFoyer)) {
                long numReservation = c.getReservations().stream()
                        .filter(res -> res.getAnneeUniversitaire().isBefore(dateFinAU)
                                && res.getAnneeUniversitaire().isAfter(dateDebutAU))
                        .count();

                int maxCapacity = switch (c.getTypeC()) {
                    case SIMPLE -> 1;
                    case DOUBLE -> 2;
                    case TRIPLE -> 3;
                };

                if (numReservation < maxCapacity) {
                    listChambreDispo.add(c);
                }
            }
        }
        return listChambreDispo;
    }

    @Override
    public void listeChambresParBloc() {
        for (Bloc b : blocRepository.findAll()) {
            log.info("Bloc => {} ayant une capacité {}", b.getNomBloc(), b.getCapaciteBloc());
            if (b.getChambres().isEmpty()) {
                log.info("Pas de chambre disponible dans ce bloc");
            } else {
                log.info("La liste des chambres pour ce bloc:");
                for (Chambre c : b.getChambres()) {
                    log.info("NumChambre: {} type: {}", c.getNumeroChambre(), c.getTypeC());
                }
            }
            log.info("********************");
        }
    }

    @Override
    public void pourcentageChambreParTypeChambre() {
        long totalChambre = repo.count();
        double pSimple = (repo.countChambreByTypeC(TypeChambre.SIMPLE) * 100.0) / totalChambre;
        double pDouble = (repo.countChambreByTypeC(TypeChambre.DOUBLE) * 100.0) / totalChambre;
        double pTriple = (repo.countChambreByTypeC(TypeChambre.TRIPLE) * 100.0) / totalChambre;

        log.info("Nombre total des chambre: {}", totalChambre);
        log.info("Le pourcentage des chambres pour le type SIMPLE est égale à {}", pSimple);
        log.info("Le pourcentage des chambres pour le type DOUBLE est égale à {}", pDouble);
        log.info("Le pourcentage des chambres pour le type TRIPLE est égale à {}", pTriple);
    }

    @Override
    public void nbPlacesDisponibleParChambreAnneeEnCours() {
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

        for (Chambre c : repo.findAll()) {
            long nbReservation = repo.countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(
                    c.getIdChambre(), true, dateDebutAU, dateFinAU
            );

            int maxCapacity = switch (c.getTypeC()) {
                case SIMPLE -> 1;
                case DOUBLE -> 2;
                case TRIPLE -> 3;
            };

            if (nbReservation < maxCapacity) {
                log.info("{}{} {} est {}", DISPONIBLE_PREFIX, c.getTypeC(), c.getNumeroChambre(), (maxCapacity - nbReservation));
            } else {
                log.info("{}{} {}{}", CHAMBRE_PREFIX, c.getTypeC(), c.getNumeroChambre(), COMPLETE_SUFFIX);
            }
        }
    }
}
