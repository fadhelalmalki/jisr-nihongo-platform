package org.fadhel.jisrnihongoplatform.repository;


import org.fadhel.jisrnihongoplatform.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Integer> {

    Instructor findInstructorById(Integer id);
}
