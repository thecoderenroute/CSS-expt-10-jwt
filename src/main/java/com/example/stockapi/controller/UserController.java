package com.example.stockapi.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.stockapi.dao.UserDao;
import com.example.stockapi.model.User;
import com.example.stockapi.model.role.ERole;
import com.example.stockapi.model.role.Role;
import com.example.stockapi.security.jwt.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    UserDao userDao;

    JwtUtils jwtUtils;

    @Autowired
    public UserController(UserDao userDao, JwtUtils jwtUtils) {
        this.userDao = userDao;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/checkRole")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    private ResponseEntity<Boolean> hasAuthority(HttpServletRequest httpServletRequest) {

        User u = this.userDao
                .getUserByEmail(
                        this.jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(httpServletRequest)))
                .orElseThrow(IllegalArgumentException::new);

        return ResponseEntity.ok(u.getRoles().contains(new Role(ERole.ROLE_USER)));

    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return this.userDao.findAll();
    }

    @GetMapping("/info")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public User getUserInfo(HttpServletRequest httpServletRequest) {

        User user = this.userDao
                .getUserByEmail(
                        this.jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(httpServletRequest)))
                .orElseThrow(IllegalArgumentException::new);

        return user;

    }

}
