package ru.hse.server.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import ru.hse.server.entity.UserEntity;
import ru.hse.server.repository.UserLocalRepository;
import ru.hse.server.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    UserRepository userRepository = new UserLocalRepository(); // TODO: connect with db and use authowired

    public UserEntity registration(UserEntity user) throws EntityExistsException {
        if (userRepository.findByLogin(user.getLogin()) == null) {
            return userRepository.save(user);
        }
        throw new EntityExistsException("User with that login already exist");
    }

    public UserEntity getUserByID(Long id) throws EntityNotFoundException {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with that id not found");
        }
        return user.get();
    }

    public UserEntity getUserByLogin(String login) throws EntityNotFoundException {
        UserEntity user = userRepository.findByLogin(login);
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
