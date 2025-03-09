package tn.esprit.spring.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.dao.entities.Universite;
import tn.esprit.spring.dao.repositories.UniversiteRepository;
import tn.esprit.spring.Services.Universite.UniversiteService;
import tn.esprit.spring.dao.entities.Foyer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class UniversiteServiceImplTest {

    @Mock
    private UniversiteRepository universiteRepository;

    @InjectMocks
    private UniversiteService universiteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    void testAjouterUniversite() {
        Universite universite = Universite.builder()
                .nomUniversite("Universite A")
                .adresse("100")
                .foyer(Foyer.builder().build()) // ✅ Initialize foyer
                .build();

        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        Universite result = universiteService.addOrUpdate(universite);

        assertNotNull(result, "Universite should not be null");
        assertEquals("Universite A", result.getNomUniversite());
        assertEquals("100", result.getAdresse()); // ✅ Fixed incorrect assertion

        verify(universiteRepository, times(1)).save(any(Universite.class));
    }

    @Test
    @Order(2)
    void testRecupererTousLesUniversites() {
        Universite universite1 = Universite.builder().idUniversite(1L).nomUniversite("Universite A").adresse("100").build();
        Universite universite2 = Universite.builder().idUniversite(2L).nomUniversite("Universite B").adresse("50").build();

        when(universiteRepository.findAll()).thenReturn(Arrays.asList(universite1, universite2));

        List<Universite> universites = universiteService.findAll();

        assertNotNull(universites, "Universite list should not be null");
        assertEquals(2, universites.size(), "Universite list size should be 2");
        assertEquals("Universite A", universites.get(0).getNomUniversite());

        verify(universiteRepository, times(1)).findAll();
    }

    @Test
    @Order(3)
    void testRecupererUniversiteParId() {
        Universite universite = Universite.builder().idUniversite(1L).nomUniversite("Universite A").adresse("100").build();

        when(universiteRepository.findById(1L)).thenReturn(Optional.of(universite));

        Universite result = universiteService.findById(1L);

        assertNotNull(result, "Universite should not be null");
        assertEquals("Universite A", result.getNomUniversite());

        verify(universiteRepository, times(1)).findById(1L);
    }

    @Test
    @Order(4)
    void testSupprimerUniversite() {
        long idUniversite = 1L;

        doNothing().when(universiteRepository).deleteById(idUniversite);

        universiteService.deleteById(idUniversite);

        verify(universiteRepository, times(1)).deleteById(idUniversite);
    }

    @RepeatedTest(3)
    @Order(5)
    void testRepetition() {
        Universite universite = Universite.builder().nomUniversite("Universite Test").adresse("30").build();

        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        Universite result = universiteService.addOrUpdate(universite);

        assertNotNull(result, "Universite should not be null");
        assertEquals("Universite Test", result.getNomUniversite());
        assertEquals("30", result.getAdresse()); // ✅ Fixed incorrect assertion

        verify(universiteRepository, times(1)).save(any(Universite.class));
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test terminé !");
    }
}
