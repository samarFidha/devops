package tn.esprit.spring.service.Services.Foyer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.service.DAO.Entities.*;
import tn.esprit.spring.service.DAO.Entities.Bloc;
import tn.esprit.spring.service.DAO.Entities.Foyer;
import tn.esprit.spring.service.DAO.Entities.Universite;
import tn.esprit.spring.service.DAO.Repositories.BlocRepository;
import tn.esprit.spring.service.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.service.DAO.Repositories.UniversiteRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class FoyerService implements IFoyerService {
    FoyerRepository repo;
    UniversiteRepository universiteRepository;
    BlocRepository blocRepository;

    @Override
    public Foyer addOrUpdate(Foyer f) {
        return repo.save(f);
    }

    @Override
    public List<Foyer> findAll() {
        return repo.findAll();
    }

    @Override
    public Foyer findById(long id) {
        return repo.findById(id).orElse(null);  // Using .orElse() to handle empty Optional
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Foyer f) {
        repo.delete(f);
    }

    @Override
    public Universite affecterFoyerAUniversite(long idFoyer, String nomUniversite) {
        Foyer f = findById(idFoyer); // Child
        if (f == null) return null; // Handle null value
        Universite u = universiteRepository.findByNomUniversite(nomUniversite); // Parent
        if (u == null) return null; // Handle null value
        u.setFoyer(f);
        return universiteRepository.save(u);
    }

    @Override
    public Universite desaffecterFoyerAUniversite(long idUniversite) {
        Universite u = universiteRepository.findById(idUniversite).orElse(null); // Parent
        if (u == null) return null; // Handle null value
        u.setFoyer(null);
        return universiteRepository.save(u);
    }

    @Override
    public Foyer ajouterFoyerEtAffecterAUniversite(Foyer foyer, long idUniversite) {
        List<Bloc> blocs = foyer.getBlocs();
        Foyer f = repo.save(foyer);
        Universite u = universiteRepository.findById(idUniversite).orElse(null);
        if (u == null) return null; // Handle null value
        for (Bloc bloc : blocs) {
            bloc.setFoyer(foyer);
            blocRepository.save(bloc);
        }
        u.setFoyer(f);
        return universiteRepository.save(u).getFoyer();
    }

    @Override
    public Foyer ajoutFoyerEtBlocs(Foyer foyer) {
        List<Bloc> blocs = foyer.getBlocs();
        foyer = repo.save(foyer);
        for (Bloc b : blocs) {
            b.setFoyer(foyer);
            blocRepository.save(b);
        }
        return foyer;
    }
}
