package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.User;

import com.example.CMCmp3.entity.UserStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;



import java.util.List;

import java.util.Optional;



@Repository

public interface UserRepository extends JpaRepository<User, Long> {



    // Tìm user theo email (dùng để đăng nhập hoặc kiểm tra tồn tại)

    Optional<User> findByEmail(String email);



    Optional<User> findByUsername(String username);



    boolean existsByEmailAndStatus(String email, UserStatus status);



    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);


    // Tìm user theo email bất kể trạng thái (để khôi phục tài khoản)

    Optional<User> findByEmailIgnoreCase(String email);



    List<User> findAllByDisplayNameContainingIgnoreCase(String query);

}
