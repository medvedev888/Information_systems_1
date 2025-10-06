package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.dto.UserDTO;
import me.vladislav.information_systems_1.exception.UserAlreadyExistException;
import me.vladislav.information_systems_1.exception.UserNotFoundException;
import me.vladislav.information_systems_1.model.User;
import me.vladislav.information_systems_1.repository.UserRepository;

@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Transactional
    public void registerNewUserAccount(UserDTO dto) {
        if (userRepository.getByLogin(dto.getLogin()).isPresent()) {
            throw new UserAlreadyExistException("Пользователь с логином \"" + dto.getLogin() + "\" уже существует");
        }

        User user = new User();
        user.setLogin(dto.getLogin());
        user.setPassword(dto.getPassword());

        userRepository.save(user);
        dto.setId(user.getId());
    }

    public User getUserByLogin(String login) {
        return userRepository.getByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином \"" + login + "\" не найден"));
    }

    public boolean checkPassword(String login, String password) {
        User user = getUserByLogin(login);
        return user.getPassword().equals(password);
    }
}
