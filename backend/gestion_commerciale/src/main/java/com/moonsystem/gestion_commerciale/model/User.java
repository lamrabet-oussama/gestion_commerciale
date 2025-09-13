package com.moonsystem.gestion_commerciale.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moonsystem.gestion_commerciale.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Cod")
    private Integer cod;

    @Column(name = "Login", length = 33, unique = true)
    private String login;

    @Column(name = "Pass")
    private String pass;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "Dépot")
    private Integer depot;

    @Column(name = "Gsm", length = 20)
    private String gsm;

    @Column(name = "Note1", length = 20)
    private String note1;

    @Column(name = "Note2", length = 20)
    private String note2;

    @Column(name = "Note3", length = 20)
    private String note3;

    @Column(name = "état")
    private boolean etat=true;

    @OneToMany(mappedBy = "user")
    private List<Bonsorti> bonsSortis;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reglement> reglements;


    // getters and setters


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
    }

    @Override
    public String getPassword() {
        return pass;
    }

    @Override
    public String getUsername() {
        return  login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.etat;
    }
}
