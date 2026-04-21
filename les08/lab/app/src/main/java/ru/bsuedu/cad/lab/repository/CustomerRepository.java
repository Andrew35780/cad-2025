package ru.bsuedu.cad.lab.repository;

import ru.bsuedu.cad.lab.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
}

