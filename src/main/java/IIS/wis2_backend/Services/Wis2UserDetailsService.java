package IIS.wis2_backend.Services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.User.UserRepository;

/**
 * The UserDetailsService implementation of this application.
 */
@Service
public class Wis2UserDetailsService implements UserDetailsService {
    /**
     * User repository for loadUserByUsername.
     */
    private final UserRepository userRepository;

    /**
     * Constructor for Wis2UserDetailsService.
     * 
     * @param userRepository The user repository.
     */
    public Wis2UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * loadUserByUsername implementation of this application.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Wis2User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String[] authorities = user.getRole() == Roles.ADMIN ? new String[] { "ROLE_ADMIN", "ROLE_USER" }
                : new String[] { "ROLE_USER" };

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isActivated())
                .authorities(authorities)
                .build();
    }
}