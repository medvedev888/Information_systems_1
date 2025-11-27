package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import me.vladislav.information_systems_1.dto.PageResponse;
import me.vladislav.information_systems_1.dto.RoleUpdateDTO;
import me.vladislav.information_systems_1.dto.UserDTO;
import me.vladislav.information_systems_1.exception.UserAlreadyExistException;
import me.vladislav.information_systems_1.exception.UserNotFoundException;
import me.vladislav.information_systems_1.mapper.EntityMapper;
import me.vladislav.information_systems_1.model.Role;
import me.vladislav.information_systems_1.model.User;
import me.vladislav.information_systems_1.repository.UserRepository;
import me.vladislav.information_systems_1.utils.PasswordUtil;

import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private EntityMapper entityMapper;

    @Transactional
    public UserDTO registerNewUserAccount(UserDTO dto) {
        if (userRepository.getByLogin(dto.getLogin()).isPresent()) {
            throw new UserAlreadyExistException("Пользователь с логином \"" + dto.getLogin() + "\" уже существует");
        }
        User user = new User();
        user.setLogin(dto.getLogin());
        user.setPassword(PasswordUtil.hashPassword(dto.getPassword()));
        user.setRole(dto.getRole());

        userRepository.save(user);
        return new UserDTO(user.getId(), user.getLogin(), null, user.getRole());
    }

    @Transactional
    public UserDTO updateRole(RoleUpdateDTO roleUpdateDTO) {
        User target;
        target = userRepository.getByLogin(roleUpdateDTO.getLogin())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newRole = roleUpdateDTO.getRole().trim().toUpperCase();

        if (target.getRole().toString().equals("ADMIN")) {
            if (!newRole.equals("ADMIN")) {
                throw new WebApplicationException("Admins cannot downgrade other admins", 403);
            }
            return new UserDTO(target.getId(), target.getLogin(), null, target.getRole());
        }

        target.setRole(Role.valueOf(newRole));
        userRepository.save(target);
        return new UserDTO(target.getId(), target.getLogin(), null, target.getRole());
    }

    public UserDTO getUserByLogin(String login, boolean includePassword) {
        User user = userRepository.getByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином \"" + login + "\" не найден"));

        String password = includePassword ? user.getPassword() : null;

        return new UserDTO(user.getId(), user.getLogin(), password, user.getRole());
    }

    public boolean confirmUserPassword(String password, String hashedPassword) {
        return PasswordUtil.checkPassword(password, hashedPassword);
    }

    @Transactional
    public PageResponse<UserDTO> getPage(Integer page,
                                         Integer size,
                                         String filterField,
                                         String filterValue,
                                         String sortField,
                                         String sortOrder) {
        List<UserDTO> organizations = userRepository.getPage(page, size, filterField, filterValue, sortField, sortOrder)
                .stream()
                .map(entityMapper::toDTO)
                .toList();
        long totalCount = userRepository.countWithFilter(filterField, filterValue);
        Integer totalPages = (int) Math.ceil((double) totalCount / size);

        return new PageResponse<>(organizations, totalPages);
    }

}
