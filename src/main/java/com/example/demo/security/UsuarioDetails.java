package com.example.demo.security;

import com.example.demo.model.TipoUsuario;
import com.example.demo.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class UsuarioDetails implements UserDetails {
    private final Usuario usuario;

    public UsuarioDetails(Usuario usuario) { this.usuario = usuario; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = usuario.getTipoUsuario() == TipoUsuario.ADMIN ? "ROLE_ADMIN" : "ROLE_COMUM";
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override public String getPassword() { return usuario.getSenha(); }
    @Override public String getUsername() { return usuario.getEmail(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
    public Usuario getUsuario() { return usuario; }
}
