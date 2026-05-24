package az.myapp.retroride.service;

import az.myapp.retroride.dao.repository.UserRepository;
import az.myapp.retroride.dao.entity.User;
import az.myapp.retroride.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserResponseDto getMyProfile() {
        User user = getCurrentUser();
        return toResponse(user);
    }


    public UserResponseDto updateMyProfile(String name, String phone) {
        User user = getCurrentUser();
        user.setName(name);
        user.setPhone(phone);
        userRepository.save(user);
        return toResponse(user);
    }


    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }


    private UserResponseDto toResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt()
        );
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}