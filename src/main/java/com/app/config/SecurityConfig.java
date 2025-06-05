package com.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    //httpSecurity es un objeto que pasa por todos los filtros del "Security Filter Chain" y cada filtro va
    //modificando ese objeto. En pocas palabras, con httpSecurity podemos crear nuestro
    //comportamiento personalizado

    /*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{

        return httpSecurity
                // Cross-site Request Forgery: Es una vulnerabilidad web donde un atacante toma dominio de un usuario logeado
                //y con lo siguiente, spring security nos protege.  Esto se recomienda en MVC pero no esn poryectos REST,
                // asi que por ello lo desabilidatermos
                .csrf(csrf -> csrf.disable())
                //withDefault indica que nos pedira usuario y contraseña
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
                    //El siguiente endpoint será publico
                    http.requestMatchers(HttpMethod.GET, "/auth/hello").permitAll();
                    //Eñ soguiente endpoint metemos seguridad, a todos usuarios que tengan el authorities "READ"
                    http.requestMatchers(HttpMethod.GET, "/auth/hello-secured").hasAuthority("CREATE");

                    //Configurar el resto de endpoints - NO ESPECIFICADOS
                    http.anyRequest().denyAll();
                })
                .build();
    }
    */

    //SecurityFilterChain con anotaciones de @EnableMethodSecurity como "@PreAuthorize"
    // en los controllers para definir reglas de autorizacion a nivel metodo
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{

        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }


    //Este autenticationManager se genera a partir de un objeto que ya existe en spring security llamado
    //AuthenticationConfiguration
    @Bean
    public AuthenticationManager authenticationManager( AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //Un AuthenticationProvider en Spring Security es un componente responsable de procesar la autenticación de
    // un usuario
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        //Este nos ayuda a encriptar y validar passwords
        provider.setPasswordEncoder((passwordEncoder()));
        //Este componente nos permite hacer el llamado a la base de datos
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        //Spring security valida los usuarios con userDetails y cuando implementemos la base de datos,
        //traemos los usuarios y lo convertimos a un userDetails
        /*
        UserDetails userDetails = User.withUsername("fernando")
                .password("101010")
                .roles("ADMIN")
                .authorities("READ", "CREATE")
                .build();

        */

        List<UserDetails> userDetailsList = new ArrayList<>();

        userDetailsList.add(User.withUsername("fernando")
                .password("101010")
                .roles("ADMIN")
                .authorities("READ", "CREATE")
                .build());

        userDetailsList.add(User.withUsername("armando")
                .password("hola")
                .roles("USER")
                .authorities("READ")
                .build());

        return new InMemoryUserDetailsManager(userDetailsList);
    }


    //NoOpPasswordEncoder esta obsoleto asi que solo se usan para pruebas
    //Usa BCryptPasswordEncoder para produccion ya que nosr permite encriptar
    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

}