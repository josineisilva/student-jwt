package br.unitau.inf.student_jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.unitau.inf.student_jwt.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByResetpassToken(String token);
    Optional<Usuario> findByVerifyToken(String token);

}