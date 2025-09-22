package knu.fest.knu.fest.global.security;

import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.domain.user.repository.UserRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return CustomUserDetails.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .password(user.getPassword())
            .authorities(
                Collections.singletonList(
                    new SimpleGrantedAuthority(
                        user.getRole().getAuthority()
                    )
                )
            )
            .build();
    }
}
