package com.app.service;

import com.app.persistence.entity.UserEntity;
import com.app.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService{


    //Con la sigueinte DI, accedemos a los usuarios en la base de datos
    private final UserRepository userRepository;

    @Autowired
    public UserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));


        //List de autoridades o roles asignados a un user
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        //Ese código recorre la lista de roles del usuario (userEntity.getRoles()) y, por cada rol, crea una autoridad de
        // Spring Security (SimpleGrantedAuthority) con el nombre del rol anteponiendo ROLE_ (por ejemplo, ROLE_ADMIN).
        // Luego, agrega esa autoridad a la lista authorityList. Así, cada rol del usuario se convierte en una autoridad
        // reconocida por Spring Security para controlar el acceso según los roles.
        userEntity.getRoles()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        //Ese código recorre todos los roles del usuario, obtiene la lista de permisos de cada rol y agrega cada permiso
        // como una autoridad (SimpleGrantedAuthority) a la lista authorityList. Así, el usuario tendrá tanto los roles
        // como los permisos individuales reconocidos por Spring Security para la autorización.
        userEntity.getRoles()
                .stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));


        //Resumen: Estamos tomando los roles del usuario y los convertimos en SimpleGrantedAuthority para que springsecurity
        //lo entienda (1er userEntity.getRole()) y despues tomamos los permisos de esos roles y los estamos autorizando
        //para que springSecurity tambien los tenga (2do userEntity.getRoles())

        //User() es de una clase de springSecurity, no la que creamos
        //Aqui le decimos a springsecurity que busque los usuarios en la base de datos, tomando los roles y los permisos
        //para convertirlo en objetos que entienda sprignsecurity y devolvemos el usuario
        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList);
    }
}
