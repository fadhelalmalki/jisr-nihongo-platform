package org.fadhel.jisrnihongoplatform.service;


import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.dto.InstructorLicenseResponse;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.fadhel.jisrnihongoplatform.model.Instructor;
import org.fadhel.jisrnihongoplatform.model.User;
import org.fadhel.jisrnihongoplatform.repository.InstructorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;

    // to get all instructors
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    // to get an instructor by id
    public Instructor getInstructorById(Integer id) {

        Instructor existing = instructorRepository.findInstructorById(id);
        if (existing == null) {
            throw new ApiException("Instructor not found");
        }

        return existing;
    }

    // to add an instructor
    public void addInstructor(Instructor instructor) {
        instructorRepository.save(instructor);
    }

    // to update an instructor
    public void updateInstructor(Integer id, Instructor instructor) {
        Instructor existing = instructorRepository.findInstructorById(id);
        if (existing == null) {
            throw new ApiException("Instructor not found");
        }
        existing.setName(instructor.getName());
        existing.setEmail(instructor.getEmail());
        existing.setPassword(instructor.getPassword());
        existing.setBio(instructor.getBio());
        existing.setFreelanceCertNumber(instructor.getFreelanceCertNumber());
        instructorRepository.save(existing);
    }

    // to delete an instructor
    public void deleteInstructor(Integer id) {
        Instructor instructor = instructorRepository.findInstructorById(id);
        if (instructor == null) {
            throw new ApiException("Instructor not found");
        }
        instructorRepository.delete(instructor);
    }

    // 4 outOf 15 to retrieve the verified freelance certificate details for a specific instructor
    public InstructorLicenseResponse getInstructorFreelanceLicense(Integer id) {
        Instructor instructor = instructorRepository.findInstructorById(id);
        if (instructor == null) {
            throw new ApiException("Instructor not found");
        }

        return new InstructorLicenseResponse(
                instructor.getName(),
                instructor.getFreelanceCertNumber()
        );
    }


}
