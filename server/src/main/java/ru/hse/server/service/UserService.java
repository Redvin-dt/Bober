package ru.hse.server.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.hse.database.entities.User;
import ru.hse.server.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(@Qualifier("userDatabaseRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registration(User user) throws EntityExistsException {
        if (userRepository.findByUserLogin(user.getUserLogin()) == null) {
            return userRepository.save(user);
        }
        throw new EntityExistsException("User with that login already exist");
    }

    public User getUserByID(Long id) throws EntityNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with that id not found");
        }
        return user.get();
    }

    public User getUserByLogin(String login) throws EntityNotFoundException {
        User user = userRepository.findByUserLogin(login);
        if (user == null) {
            throw new EntityNotFoundException("User with that login not found");
        }
        return user;
    }

    public void deleteUserById(Long id) throws EntityNotFoundException {
        if (userRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("User with that id not found");
        }
        userRepository.deleteById(id);
    }
}
