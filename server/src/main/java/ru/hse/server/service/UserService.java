package ru.hse.server.service;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.hse.server.proto.EntitiesProto;
import ru.hse.server.proto.EntitiesProto.UserInfo;
import ru.hse.database.entities.User;
import ru.hse.server.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(@Qualifier("userDatabaseRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registration(EntitiesProto.UserInfo userInfo) throws EntityExistsException, InvalidProtocolBufferException {
        if (!userInfo.hasLogin() || !userInfo.hasPassword()){
            throw new InvalidProtocolBufferException("user info should have field login and password");
        }
        if (userRepository.findByUserLogin(userInfo.getLogin()) == null) {
            var user = new User(userInfo.getLogin(), userInfo.getPassword());
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
