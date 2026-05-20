package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{
    
    public User findByUsernameAndPasswordAndStatus(String username, String password, String status);
    public User findFirstByRole(String role);
    public User findByIdCard(String idCard);
    public User findByEmail(String email);
    public User findByUsername(String username);
    public List<User> findByFullNameContainingIgnoreCase(String fullName);
    public List<User> findByRole(String role);
    public List<User> findByFullNameContainingIgnoreCaseAndRole(String fullName, String role);
    public Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
    public Page<User> findByRole(String role, Pageable pageable);
    public Page<User> findByFullNameContainingIgnoreCaseAndRole(String fullName, String role, Pageable pageable);
    public User findByPhone(String phone);
}
