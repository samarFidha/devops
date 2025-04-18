package tn.esprit.spring.services.Foyer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.Foyer.FoyerService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

public class FoyerServiceTest {

    @Mock
    private FoyerRepository foyerRepository;

    @Mock
    private UniversiteRepository universiteRepository;

    @Mock
    private BlocRepository blocRepository;

    @InjectMocks
    private FoyerService foyerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Foyer foyer = new Foyer();
        foyer.setIdFoyer(1L);
        foyer.setNomFoyer("Foyer A");
        foyer.setCapaciteFoyer(100);

        when(foyerRepository.save(foyer)).thenReturn(foyer);

        Foyer savedFoyer = foyerService.addOrUpdate(foyer);

        assertNotNull(savedFoyer);
        assertEquals("Foyer A", savedFoyer.getNomFoyer());
        verify(foyerRepository, times(1)).save(foyer);
    }

    @Test
    void testFindAll() {
        Foyer foyer1 = new Foyer();
        foyer1.setIdFoyer(1L);
        foyer1.setNomFoyer("Foyer A");

        Foyer foyer2 = new Foyer();
        foyer2.setIdFoyer(2L);
        foyer2.setNomFoyer("Foyer B");

        when(foyerRepository.findAll()).thenReturn(Arrays.asList(foyer1, foyer2));

        List<Foyer> foyers = foyerService.findAll();

        assertEquals(2, foyers.size());
        verify(foyerRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Foyer foyer = new Foyer();
        foyer.setIdFoyer(1L);
        foyer.setNomFoyer("Foyer A");

        when(foyerRepository.findById(1L)).thenReturn(Optional.of(foyer));

        Foyer foundFoyer = foyerService.findById(1L);

        assertNotNull(foundFoyer);
        assertEquals("Foyer A", foundFoyer.getNomFoyer());
        verify(foyerRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteById() {
        doNothing().when(foyerRepository).deleteById(1L);

        foyerService.deleteById(1L);

        verify(foyerRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete() {
        Foyer foyer = new Foyer();
        foyer.setIdFoyer(1L);
        foyer.setNomFoyer("Foyer A");

        doNothing().when(foyerRepository).delete(foyer);

        foyerService.delete(foyer);

        verify(foyerRepository, times(1)).delete(foyer);
    }

    @Test
    void testAffecterFoyerAUniversite() {
        Foyer foyer = new Foyer();
        foyer.setIdFoyer(1L);
        foyer.setNomFoyer("Foyer A");

        Universite universite = new Universite();
        universite.setIdUniversite(1L);
        universite.setNomUniversite("Universite A");

        when(foyerRepository.findById(1L)).thenReturn(Optional.of(foyer));
        when(universiteRepository.findByNomUniversite("Universite A")).thenReturn(universite);
        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite updatedUniversite = foyerService.affecterFoyerAUniversite(1L, "Universite A");

        assertNotNull(updatedUniversite);
        assertEquals(foyer, updatedUniversite.getFoyer());
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testDesaffecterFoyerAUniversite() {
        Foyer foyer = new Foyer();
        foyer.setIdFoyer(1L);
        foyer.setNomFoyer("Foyer A");

        Universite universite = new Universite();
        universite.setIdUniversite(1L);
        universite.setNomUniversite("Universite A");
        universite.setFoyer(foyer);

        when(universiteRepository.findById(1L)).thenReturn(Optional.of(universite));
        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite updatedUniversite = foyerService.desaffecterFoyerAUniversite(1L);

        assertNotNull(updatedUniversite);
        assertNull(updatedUniversite.getFoyer());
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testAjouterFoyerEtAffecterAUniversite() {
        Foyer foyer = new Foyer();
        foyer.setIdFoyer(1L);
        foyer.setNomFoyer("Foyer A");

        Bloc bloc1 = new Bloc();
        bloc1.setIdBloc(1L);
        bloc1.setNomBloc("Bloc A");

        Bloc bloc2 = new Bloc();
        bloc2.setIdBloc(2L);
        bloc2.setNomBloc("Bloc B");

        foyer.setBlocs(Arrays.asList(bloc1, bloc2));

        Universite universite = new Universite();
        universite.setIdUniversite(1L);
        universite.setNomUniversite("Universite A");

        when(foyerRepository.save(foyer)).thenReturn(foyer);
        when(universiteRepository.findById(1L)).thenReturn(Optional.of(universite));
        when(blocRepository.save(bloc1)).thenReturn(bloc1);
        when(blocRepository.save(bloc2)).thenReturn(bloc2);
        when(universiteRepository.save(universite)).thenReturn(universite);

        Foyer savedFoyer = foyerService.ajouterFoyerEtAffecterAUniversite(foyer, 1L);

        assertNotNull(savedFoyer);
        assertEquals(foyer, universite.getFoyer());
        verify(foyerRepository, times(1)).save(foyer);
        verify(blocRepository, times(1)).save(bloc1);
        verify(blocRepository, times(1)).save(bloc2);
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testAjoutFoyerEtBlocs() {
        Foyer foyer = new Foyer();
        foyer.setIdFoyer(1L);
        foyer.setNomFoyer("Foyer A");

        Bloc bloc1 = new Bloc();
        bloc1.setIdBloc(1L);
        bloc1.setNomBloc("Bloc A");

        Bloc bloc2 = new Bloc();
        bloc2.setIdBloc(2L);
        bloc2.setNomBloc("Bloc B");

        foyer.setBlocs(Arrays.asList(bloc1, bloc2));

        when(foyerRepository.save(foyer)).thenReturn(foyer);
        when(blocRepository.save(bloc1)).thenReturn(bloc1);
        when(blocRepository.save(bloc2)).thenReturn(bloc2);

        Foyer savedFoyer = foyerService.ajoutFoyerEtBlocs(foyer);

        assertNotNull(savedFoyer);
        assertEquals(2, savedFoyer.getBlocs().size());
        verify(foyerRepository, times(1)).save(foyer);
        verify(blocRepository, times(1)).save(bloc1);
        verify(blocRepository, times(1)).save(bloc2);
    }
}
