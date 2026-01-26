package org.example.student.repository;

import org.example.student.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {
    Optional<Major> findByMajorCode(String majorCode);
    
    @Query("SELECT MAX(s.sequenceNumber) FROM Student s WHERE s.major.id = :majorId AND YEAR(s.enrollmentDate) = :year")
    Integer findMaxSequenceNumberByMajorIdAndYear(Long majorId, int year);
}