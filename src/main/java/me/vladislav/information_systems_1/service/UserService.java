package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.dto.UserDTO;
import me.vladislav.information_systems_1.exception.UserAlreadyExistException;
import me.vladislav.information_systems_1.exception.UserNotFoundException;
import me.vladislav.information_systems_1.model.User;
import me.vladislav.information_systems_1.repository.UserRepository;
import me.vladislav.information_systems_1.utils.PasswordUtil;

@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Transactional
    public UserDTO registerNewUserAccount(UserDTO dto) {
        if (userRepository.getByLogin(dto.getLogin()).isPresent()) {
            throw new UserAlreadyExistException("Пользователь с логином \"" + dto.getLogin() + "\" уже существует");
        }
        User user = new User();
        user.setLogin(dto.getLogin());
        user.setPassword(PasswordUtil.hashPassword(dto.getPassword()));

        userRepository.save(user);
        return new UserDTO(user.getId(), user.getLogin(), null);
    }

    public UserDTO getUserByLogin(String login) {
        User user = userRepository.getByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином \"" + login + "\" не найден"));
        return new UserDTO(user.getId(), user.getLogin(), user.getPassword());
    }

    public boolean confirmUserPassword(String password, String hashedPassword) {
        return PasswordUtil.checkPassword(password, hashedPassword);
    }

}
