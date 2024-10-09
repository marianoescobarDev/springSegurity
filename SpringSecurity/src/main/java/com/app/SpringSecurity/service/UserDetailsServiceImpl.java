package com.app.SpringSecurity.service;

import com.app.SpringSecurity.persistencia.entidades.usuarios;
import com.app.SpringSecurity.repository.usuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private usuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {
        usuarios usuario = usuarioRepository.findUsuarioByNombre(nombre)
                .orElseThrow(()-> new UsernameNotFoundException("El usuario " + nombre + " no se encontro"));

        List<SimpleGrantedAuthority> authorityList = new LinkedList<>();
        usuario.getNiveles()
                .forEach(niveles -> authorityList.add(new SimpleGrantedAuthority("ROLE_" .concat(niveles.getRoleEnum().name()))));
        usuario.getNiveles().stream()
                .flatMap(niveles ->  niveles.getPermisos().stream())
                .forEach(permisos -> authorityList.add(new SimpleGrantedAuthority(permisos.getNombre())));
        ;

        return new User(usuario.getNombre(),
                usuario.getClave(),
                usuario.isEnable(),
                usuario.isAccountNoExpired(),
                usuario.isAccountNoLocked(),
                usuario.isCredentialNoExpired(),
                authorityList
                );
    }
}
