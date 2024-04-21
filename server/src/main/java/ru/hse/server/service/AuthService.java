package ru.hse.server.service;

import jakarta.annotation.Nonnull;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.hse.database.entities.User;
import ru.hse.server.proto.EntitiesProto;
import ru.hse.server.repository.UserRepository;
import ru.hse.server.utils.ProtoSerializer;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JWTProviderService jwtProviderService;

    public AuthService(@Qualifier("userDatabaseRepository") UserRepository userRepository,
                       JWTProviderService jwtProviderService) {
        this.jwtProviderService = jwtProviderService;
        this.userRepository = userRepository;
    }

    public EntitiesProto.UserModel login(@Nonnull EntitiesProto.UserModel userModel) throws AuthException {
        final User user = userRepository.findByUserLogin(userModel.getLogin());

        if (user == null) {
            throw new AuthException("no such user");
        }

        if (user.getPasswordHash().equals(userModel.getPasswordHash())) { // TODO: add hashing for password
            return ProtoSerializer.getUserInfo(user).toBuilder().setAccessToken(jwtProviderService.generateAccessToken(user)).build();
        } else {
            throw new AuthException("wrong password");
        }
    }

    public String getAccessToken(@Nonnull EntitiesProto.UserModel userModel) throws AuthException {
        final User user = userRepository.findByUserLogin(userModel.getLogin());

        if (user == null) {
            throw new AuthException("no such user");
        }

        return jwtProviderService.generateAccessToken(user);
    }
}
