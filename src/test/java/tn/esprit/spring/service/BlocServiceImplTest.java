package tn.esprit.spring.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tn.esprit.spring.service.DAO.Entities.Bloc;
import tn.esprit.spring.service.DAO.Repositories.BlocRepository;
import tn.esprit.spring.service.Services.Bloc.BlocService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class BlocServiceImplTest {

    @MockBean
    private BlocRepository blocRepository;

    @Autowired
    private BlocService blocService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    void testAjouterBloc() {
        Bloc bloc = Bloc.builder()
                .nomBloc("Bloc Test")
                .capaciteBloc(30)
                .chambres(new ArrayList<>())  // ✅ Ensuring chambres is initialized
                .build();

        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);

        Bloc result = blocService.addOrUpdate(bloc);

        assertNotNull(result);
        assertEquals("Bloc Test", result.getNomBloc());  // ✅ Changed expected value
        assertEquals(30, result.getCapaciteBloc());  // ✅ Ensuring correct comparison

        verify(blocRepository, times(1)).save(any(Bloc.class));
    }


    @Test
    @Order(2)
    void testRecupererTousLesBlocs() {
        Bloc bloc1 = Bloc.builder().idBloc(1L).nomBloc("Bloc A").capaciteBloc(100).build();
        Bloc bloc2 = Bloc.builder().idBloc(2L).nomBloc("Bloc B").capaciteBloc(50).build();

        when(blocRepository.findAll()).thenReturn(Arrays.asList(bloc1, bloc2));

        List<Bloc> blocs = blocService.findAll();

        assertNotNull(blocs);
        assertEquals(2, blocs.size());
        assertEquals("Bloc A", blocs.get(0).getNomBloc());

        verify(blocRepository, times(1)).findAll();
    }

    @Test
    @Order(3)
    void testRecupererBlocParId() {
        Bloc bloc = Bloc.builder().idBloc(1L).nomBloc("Bloc A").capaciteBloc(100).build();

        when(blocRepository.findById(1L)).thenReturn(Optional.of(bloc));

        Bloc result = blocService.findById(1L);

        assertNotNull(result);
        assertEquals("Bloc A", result.getNomBloc());

        verify(blocRepository, times(1)).findById(1L);
    }

    @Test
    @Order(4)
    void testSupprimerBloc() {
        long idBloc = 1L;

        doNothing().when(blocRepository).deleteById(idBloc);

        blocService.deleteById(idBloc);

        verify(blocRepository, times(1)).deleteById(idBloc);
    }

    @RepeatedTest(3)
    @Order(5)
    void testRepetition() {
        Bloc bloc = Bloc.builder()
                .nomBloc("Bloc Test")
                .capaciteBloc(30)
                .chambres(new ArrayList<>())  // ✅ Ensure chambres is initialized
                .build();

        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);

        Bloc result = blocService.addOrUpdate(bloc);

        assertNotNull(result);
        assertNotNull(result.getChambres());  // ✅ Ensure chambres is never null
        assertEquals(0, result.getChambres().size());  // ✅ Verify it's empty initially
        assertEquals("Bloc Test", result.getNomBloc());
        assertEquals(30, result.getCapaciteBloc());

        verify(blocRepository, times(1)).save(any(Bloc.class));
    }


    @AfterEach
    void tearDown() {
        System.out.println("Test terminé !");
    }
}
