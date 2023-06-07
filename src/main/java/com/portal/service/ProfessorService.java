package com.portal.service;

import com.portal.model.Professor;
import com.portal.repo.ProfessorRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfessorService {
    public ProfessorService() {
    }

    ProfessorRepository professorRepository = new ProfessorRepository();

    public List<Professor> getProfessors() {
        List<Professor> professors = new ArrayList<>();
        try {
            professors = professorRepository.getProfessors();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return professors;
    };
    public void createProfessor(Professor professor) {
        try {
            professorRepository.createProfessor(professor);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };
    public void updateProfessor(Professor professor) {
        try {
            professorRepository.updateProfessor(professor);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };
    public void deleteProfessor(Professor professor) {
        try {
            professorRepository.deleteProfessor(professor);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };
}
