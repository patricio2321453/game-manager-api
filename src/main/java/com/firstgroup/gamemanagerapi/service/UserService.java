package com.firstgroup.gamemanagerapi.service;

import com.firstgroup.gamemanagerapi.dto.UserDTO;
import com.firstgroup.gamemanagerapi.entity.User;
import com.firstgroup.gamemanagerapi.mapper.UserMapper;
import com.firstgroup.gamemanagerapi.repository.UserRepository;
import com.firstgroup.gamemanagerapi.request.UserRO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDTO createUser(@Valid UserRO ro) {
        if (userRepository.existsByDisplayName(ro.displayName())) {
            throw new IllegalArgumentException("Username already exists!");
        }

        if (userRepository.existsByEmail(ro.email())) {
            throw new IllegalArgumentException("Email already exists!");
        }

        User user = userMapper.toEntity(ro);
        user.setPassword(ro.password());

        return userMapper.toDto(userRepository.save(user));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDTO getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found!"));
        return userMapper.toDto(user);
    }

    public UserDTO updateUser(Long id, @Valid UserRO ro) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        if (!user.getEmail().equals(ro.email()) && userRepository.existsByEmail(ro.email())) {
            throw new IllegalArgumentException("Email already exists!");
        }

        if (!user.getDisplayName().equals(ro.displayName()) && userRepository.existsByDisplayName(ro.displayName())) {
            throw new IllegalArgumentException("Username already exists!");
        }

        /*userMapper.updateUserFromRO(ro, user);*/

        if (ro.password() != null && !ro.password().isBlank()) {
            user.setPassword(ro.password());
        }

        return userMapper.toDto(userRepository.save(user));
    }

    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }
}
