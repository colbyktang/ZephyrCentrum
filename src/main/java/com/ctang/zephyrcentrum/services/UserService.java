package com.ctang.zephyrcentrum.services;

import java.util.List;
import java.util.Map;

import com.ctang.zephyrcentrum.exceptions.UsernameAlreadyExistsException;
import com.ctang.zephyrcentrum.models.User;

public interface UserService {
    public User getUserById(Long id);
    public User getUserByUsername(String username);
    public List<User> getAllUsers();
    public User createUser(User user) throws UsernameAlreadyExistsException;
    public User updateUser(Long id, Map<String, Object> fields);
    public void deleteUser(Long id);
}
