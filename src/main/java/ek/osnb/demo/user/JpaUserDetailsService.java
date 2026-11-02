package ek.osnb.demo.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public JpaUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));


        return User
                .withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(appUser.getRole())
                .build();
    }

}


/*
 * 1. What HTTP status does user1 receive?
 *
 *    403 Forbidden. user1 is authenticated but only has the authority
 *    ROLE_USER. The rule for /api/admin/** requires ROLE_ADMIN, so the request
 *    is rejected before it reaches the controller.
 *
 * 2. Why can admin access the endpoint?
 *
 *    The admin row in the database has the role ADMIN. .roles("ADMIN") in
 *    JpaUserDetailsService turns it into the authority ROLE_ADMIN, which is
 *    stored in the session at login. hasRole("ADMIN") checks for ROLE_ADMIN,
 *    so the check passes. Access depends on the authority, not the username.
 *
 * 3. What is the difference between hasRole("ADMIN") and hasAuthority("ROLE_ADMIN")?
 *
 *    In this application they behave the same: both check for ROLE_ADMIN.
 *    hasRole("ADMIN") adds the ROLE_ prefix automatically, while hasAuthority
 *    checks the exact string given. A role is just an authority with the ROLE_
 *    prefix. hasAuthority is also used for fine-grained permissions without
 *    that prefix, such as notes:delete.
 */