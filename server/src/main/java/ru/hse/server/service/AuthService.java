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
    private final JWTProvider jwtProvider;
    private final PasswordEncoderService passwordEncoderService;

    public AuthService(@Qualifier("userDatabaseRepository") UserRepository userRepository,
                       JWTProvider jwtProvider, PasswordEncoderService passwordEncoderService) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    public EntitiesProto.UserModel login(@Nonnull EntitiesProto.UserModel userModel) throws AuthException {
        final User user = userRepository.findByUserLogin(userModel.getLogin());

        if (user == null) {
            throw new AuthException("no such user");
        }

        if (passwordEncoderService.matchPassword(userModel.getPasswordHash(), user.getPasswordHash())) {
            return ProtoSerializer.getUserInfo(user).toBuilder().setAccessToken(jwtProvider.generateAccessToken(user)).build();
        } else {
            throw new AuthException("wrong password");
        }
    }

    public String getAccessToken(@Nonnull EntitiesProto.UserModel userModel) throws AuthException {
        final User user = userRepository.findByUserLogin(userModel.getLogin());

        if (user == null) {
            throw new AuthException("no such user");
        }

        return jwtProvider.generateAccessToken(user);
    }
}
