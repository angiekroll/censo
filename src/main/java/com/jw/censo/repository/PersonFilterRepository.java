package com.jw.censo.repository;

import com.jw.censo.model.PersonFilter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonFilterRepository extends JpaRepository<PersonFilter, Long> {
}
