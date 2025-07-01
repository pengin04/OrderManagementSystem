package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Mail;

public interface MailRepository extends JpaRepository<Mail, Integer> {
	@Query("SELECT a FROM Mail a WHERE a.email = :email ORDER BY a.createdAt DESC")
    List<Mail> findByEmail(@Param("email") String email, Pageable pageable);

    default Optional<Mail> findLatestByEmail(String email) {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"));
        return findByEmail(email, pageable).stream().findFirst();
    }
}
