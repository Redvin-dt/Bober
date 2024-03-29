package ru.hse.server.service;

import ru.hse.server.proto.EntitiesProto.UserModel;
import ru.hse.database.entities.User;
import ru.hse.server.repository.UserRepository;
import ru.hse.server.utils.ProtoSerializer;

import com.google.protobuf.InvalidProtocolBufferException;

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

    public UserModel registration(UserModel userInfo) throws EntityExistsException,
            InvalidProtocolBufferException {
        if (!userInfo.hasLogin() || !userInfo.hasPasswordHash()){
            throw new InvalidProtocolBufferException("user info should have field login and password");
        }
        if (userRepository.findByUserLogin(userInfo.getLogin()) == null) {
            var user = new User(userInfo.getLogin(), userInfo.getPasswordHash());
            userRepository.save(user);
            return ProtoSerializer.getUserInfo(user);
        }
        throw new EntityExistsException("user with that login already exist");
    }

    public UserModel getUserByID(Long id) throws EntityNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("user with that id not found");
        }

        return ProtoSerializer.getProtoFromUser(user.get());
    }

    public UserModel getUserByLogin(String login) throws EntityNotFoundException {
        User user = userRepository.findByUserLogin(login);
        if (user == null) {
            throw new EntityNotFoundException("user with that login not found");
        }
        return ProtoSerializer.getProtoFromUser(user);
    }

    public void deleteUserById(Long id) throws EntityNotFoundException {
        if (userRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("user with that id not found");
        }
        userRepository.deleteById(id);
    }
}
