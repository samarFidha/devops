package tn.esprit.spring.Services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.service.DAO.Entities.Bloc;
import tn.esprit.spring.service.DAO.Entities.Chambre;
import tn.esprit.spring.service.DAO.Entities.Foyer;
import tn.esprit.spring.service.DAO.Repositories.BlocRepository;
import tn.esprit.spring.service.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.service.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.service.Services.Bloc.BlocService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlocServiceTest {

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private FoyerRepository foyerRepository;

    @InjectMocks
    private BlocService blocService;

    private Bloc bloc;
    private Chambre chambre;
    private Foyer foyer;

    @BeforeEach
    void setUp() {
        bloc = new Bloc();
        bloc.setIdBloc(1L);
        bloc.setNomBloc("Bloc A");

        chambre = new Chambre();
        chambre.setIdChambre(1L);
        chambre.setNumeroChambre(101L);

        foyer = new Foyer();
        foyer.setIdFoyer(1L);
        foyer.setNomFoyer("Foyer A");
    }

    @Test
    void testAddOrUpdate() {
        // Arrange
        List<Chambre> chambres = new ArrayList<>();
        chambres.add(chambre);
        bloc.setChambres(chambres);

        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);
        when(chambreRepository.save(any(Chambre.class))).thenReturn(chambre);

        // Act
        Bloc result = blocService.addOrUpdate(bloc);

        // Assert
        assertNotNull(result);
        assertEquals(bloc.getNomBloc(), result.getNomBloc());
        verify(blocRepository, times(1)).save(any(Bloc.class));
        verify(chambreRepository, times(1)).save(any(Chambre.class));
    }

    @Test
    void testFindById() {
        // Arrange
        when(blocRepository.findById(1L)).thenReturn(Optional.of(bloc));

        // Act
        Bloc result = blocService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(bloc.getIdBloc(), result.getIdBloc());
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(blocRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> blocService.findById(99L));
    }

    @Test
    void testDelete() {
        // Arrange
        List<Chambre> chambres = new ArrayList<>();
        chambres.add(chambre);
        bloc.setChambres(chambres);

        // Act
        blocService.delete(bloc);

        // Assert
        verify(chambreRepository, times(1)).delete(chambre);
        verify(blocRepository, times(1)).delete(bloc);
    }

    @Test
    void testAffecterChambresABloc() {
        // Arrange
        List<Long> chambreNumbers = List.of(101L);
        when(blocRepository.findByNomBloc("Bloc A")).thenReturn(bloc);
        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);

        // Act
        Bloc result = blocService.affecterChambresABloc(chambreNumbers, "Bloc A");

        // Assert
        assertNotNull(result);
        assertEquals(bloc.getNomBloc(), result.getNomBloc());
        verify(chambreRepository, times(1)).save(chambre);
    }

    @Test
    void testAffecterBlocAFoyer() {
        // Arrange
        when(blocRepository.findByNomBloc("Bloc A")).thenReturn(bloc);
        when(foyerRepository.findByNomFoyer("Foyer A")).thenReturn(foyer);
        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);

        // Act
        Bloc result = blocService.affecterBlocAFoyer("Bloc A", "Foyer A");

        // Assert
        assertNotNull(result);
        assertEquals(foyer, result.getFoyer());
        verify(blocRepository, times(1)).save(bloc);
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Bloc> blocs = new ArrayList<>();
        blocs.add(bloc);
        when(blocRepository.findAll()).thenReturn(blocs);

        // Act
        List<Bloc> result = blocService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bloc.getIdBloc(), result.get(0).getIdBloc());
    }

    @Test
    void testDeleteById() {
        // Act
        blocService.deleteById(1L);

        // Assert
        verify(blocRepository, times(1)).deleteById(1L);
    }
}