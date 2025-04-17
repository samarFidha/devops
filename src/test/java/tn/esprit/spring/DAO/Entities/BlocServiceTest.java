package tn.esprit.spring.DAO.Entities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.Services.Bloc.BlocService;

import java.util.Arrays;
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
    private Chambre chambre1, chambre2;
    private Foyer foyer;

    @BeforeEach
    void setUp() {
        bloc = Bloc.builder()
                .idBloc(1L)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .build();

        chambre1 = Chambre.builder()
                .idChambre(1L)
                .numeroChambre(101L)
                .build();

        chambre2 = Chambre.builder()
                .idChambre(2L)
                .numeroChambre(102L)
                .build();

        foyer = Foyer.builder()
                .idFoyer(1L)
                .nomFoyer("Foyer A")
                .build();
    }

    @Test
    void testAddOrUpdate2() {
        // Arrange
        bloc.setChambres(Arrays.asList(chambre1, chambre2));

        // Act
        Bloc result = blocService.addOrUpdate2(bloc);

        // Assert
        assertEquals(bloc, result);
        verify(chambreRepository, times(2)).save(any(Chambre.class));
        assertEquals(bloc, chambre1.getBloc());
        assertEquals(bloc, chambre2.getBloc());
    }

    @Test
    void testAddOrUpdate() {
        // Arrange
        bloc.setChambres(Arrays.asList(chambre1, chambre2));
        when(blocRepository.save(bloc)).thenReturn(bloc);

        // Act
        Bloc result = blocService.addOrUpdate(bloc);

        // Assert
        assertEquals(bloc, result);
        verify(blocRepository).save(bloc);
        verify(chambreRepository, times(2)).save(any(Chambre.class));
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Bloc> expectedBlocs = Arrays.asList(bloc);
        when(blocRepository.findAll()).thenReturn(expectedBlocs);

        // Act
        List<Bloc> result = blocService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedBlocs, result);
        verify(blocRepository).findAll();
    }

    @Test
    void testFindById() {
        // Arrange
        when(blocRepository.findById(1L)).thenReturn(Optional.of(bloc));

        // Act
        Bloc result = blocService.findById(1L);

        // Assert
        assertEquals(bloc, result);
        verify(blocRepository).findById(1L);
    }

    @Test
    void testDeleteById() {
        // Act
        blocService.deleteById(1L);

        // Assert
        verify(blocRepository).deleteById(1L);
    }

    @Test
    void testDelete() {
        // Arrange
        bloc.setChambres(Arrays.asList(chambre1, chambre2));

        // Act
        blocService.delete(bloc);

        // Assert
        verify(chambreRepository).delete(chambre1);
        verify(chambreRepository).delete(chambre2);
        verify(blocRepository).delete(bloc);
    }

    @Test
    void testAffecterChambresABloc() {
        // Arrange
        List<Long> numChambres = Arrays.asList(101L, 102L);
        when(blocRepository.findByNomBloc("Bloc A")).thenReturn(bloc);
        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre1);
        when(chambreRepository.findByNumeroChambre(102L)).thenReturn(chambre2);

        // Act
        Bloc result = blocService.affecterChambresABloc(numChambres, "Bloc A");

        // Assert
        assertEquals(bloc, result);
        assertEquals(bloc, chambre1.getBloc());
        assertEquals(bloc, chambre2.getBloc());
        verify(chambreRepository).save(chambre1);
        verify(chambreRepository).save(chambre2);
    }

    @Test
    void testAffecterBlocAFoyer() {
        // Arrange
        when(blocRepository.findByNomBloc("Bloc A")).thenReturn(bloc);
        when(foyerRepository.findByNomFoyer("Foyer A")).thenReturn(foyer);
        when(blocRepository.save(bloc)).thenReturn(bloc);

        // Act
        Bloc result = blocService.affecterBlocAFoyer("Bloc A", "Foyer A");

        // Assert
        assertEquals(bloc, result);
        assertEquals(foyer, bloc.getFoyer());
        verify(blocRepository).save(bloc);
    }
}
