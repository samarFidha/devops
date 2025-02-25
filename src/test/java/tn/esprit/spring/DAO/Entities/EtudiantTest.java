package tn.esprit.spring.DAO.Entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EtudiantTest {

    private Etudiant etudiant;

    @Mock
    private Reservation mockReservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        etudiant = Etudiant.builder()
                .idEtudiant(1L)
                .nomEt("Naouali")
                .prenomEt("Chaima")
                .cin(12345678L)
                .ecole("ESPRIT")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .reservations(new ArrayList<>())
                .build();
    }

    @Test
    void testEtudiantFields() {
        assertEquals(1L, etudiant.getIdEtudiant());
        assertEquals("Naouali", etudiant.getNomEt());
        assertEquals("Chaima", etudiant.getPrenomEt());
        assertEquals(12345678L, etudiant.getCin());
        assertEquals("ESPRIT", etudiant.getEcole());
        assertEquals(LocalDate.of(2000, 1, 1), etudiant.getDateNaissance());
    }

    @Test
    void testReservationsList() {
        assertTrue(etudiant.getReservations().isEmpty());

        etudiant.getReservations().add(mockReservation);
        assertEquals(1, etudiant.getReservations().size());
    }

    @Test
    void testMockReservationAdded() {
        etudiant.setReservations(new ArrayList<>());

        etudiant.getReservations().add(mockReservation);


        assertTrue(etudiant.getReservations().isEmpty());


        assertEquals(1, etudiant.getReservations().size());
        assertSame(mockReservation, etudiant.getReservations().get(0));
    }


    @Test
    void testSetters() {
        etudiant.setNomEt("UpdatedName");
        assertEquals("UpdatedName", etudiant.getNomEt());

        etudiant.setCin(87654321L);
        assertEquals(87654321L, etudiant.getCin());
    }

    @Test
    void testNoArgsConstructor() {
        Etudiant emptyEtudiant = new Etudiant();
        assertNotNull(emptyEtudiant);
    }

    @Test
    void testAllArgsConstructor() {
        List<Reservation> reservations = new ArrayList<>();
        Etudiant newEtudiant = new Etudiant(2L, "Test", "User", 98765432L, "TestSchool", LocalDate.of(1999, 12, 31), reservations);
        assertEquals(2L, newEtudiant.getIdEtudiant());
        assertEquals("Test", newEtudiant.getNomEt());
        assertEquals("User", newEtudiant.getPrenomEt());
    }
}
