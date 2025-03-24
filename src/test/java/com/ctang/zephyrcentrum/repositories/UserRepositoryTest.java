package com.ctang.zephyrcentrum.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.ctang.zephyrcentrum.models.User;
import com.ctang.zephyrcentrum.repositories.UserRepository;
import com.ctang.zephyrcentrum.utils.PasswordUtils;


@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_shouldReturnUser_whenUsernameExists() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByUsername("testuser");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    public void findByUsername_shouldReturnEmpty_whenUsernameDoesNotExist() {
        // when
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    public void findByEmail_shouldReturnUser_whenEmailExists() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        // when
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(found).isEmpty();
    }
}