package br.unitau.inf.student_jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.unitau.inf.student_jwt.model.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

}