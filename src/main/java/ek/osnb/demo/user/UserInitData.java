package ek.osnb.demo.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
class UserInitData implements CommandLineRunner {

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    UserInitData(AppUserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // The password is pw for all three users, stored as a BCrypt hash
        users.save(new AppUser("user1", passwordEncoder.encode("pw"), "USER"));
        users.save(new AppUser("user2", passwordEncoder.encode("pw"), "USER"));
        users.save(new AppUser("admin", passwordEncoder.encode("pw"), "ADMIN"));

        // Print what is stored in the database
        for (AppUser user : users.findAll()) {
            System.out.println(user.getUsername() + " " + user.getPassword() + " " + user.getRole());
        }
    }
}
