package com.brainzzle.brainzzle_api.services.auth;

import com.brainzzle.brainzzle_api.dto.PasswordUpdateDTO;
import com.brainzzle.brainzzle_api.dto.ReqRes;
import com.brainzzle.brainzzle_api.dto.UserUpdateDTO;
import com.brainzzle.brainzzle_api.entities.User;
import com.brainzzle.brainzzle_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9.%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ReqRes register(ReqRes registrationRequest) {
        ReqRes response = new ReqRes();

        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            response.setMessage("An user with this email already exists.");
            response.setStatusCode(400);
            return response;
        }

        if (!isValidEmail(registrationRequest.getEmail())) {
            response.setMessage("Email is not valid.");
            response.setStatusCode(400);
            return response;
        }

        try {
            User ourUser = new User();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setFirstName(registrationRequest.getFirstName());
            ourUser.setLastName(registrationRequest.getLastName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            User userResult = userRepository.save(ourUser);

            if (userResult.getUserId() > 0) {
                response.setUser((userResult));
                response.setMessage("User saved successfully.");
                response.setStatusCode(200);
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }

        return response;
    }

    public ReqRes login(ReqRes loginRequest){
        ReqRes response = new ReqRes();

        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();

            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public ReqRes refreshToken(ReqRes refreshTokenRequest){
        ReqRes response = new ReqRes();

        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            User users = userRepository.findByEmail(ourEmail).orElseThrow();

            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }

            response.setStatusCode(200);
            return response;
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());

            return response;
        }
    }

    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<User> result = userRepository.findAll();

            if (!result.isEmpty()) {
                reqRes.setUserList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }

            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());

            return reqRes;
        }
    }

    public ReqRes getUsersById(Long id) {
        ReqRes reqRes = new ReqRes();

        try {
            User usersById = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setUser(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }

        return reqRes;
    }

    public ReqRes deleteUser(Long userId) {
        ReqRes reqRes = new ReqRes();

        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                userRepository.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }

        return reqRes;
    }

    public ReqRes deleteMyUser(Long userId) {
        ReqRes reqRes = new ReqRes();

        try {
            Optional<User> userOptional = userRepository.findById(userId);
            Optional<User> currentUserOptional = getCurrentUser();

            if (userOptional.isPresent() && currentUserOptional.isPresent()) {
                User userToDelete = userOptional.get();
                User currentUser = currentUserOptional.get();

                if (userToDelete.getUserId().equals(currentUser.getUserId())) {
                    userRepository.deleteById(userId);
                    reqRes.setStatusCode(200);
                    reqRes.setMessage("User deleted successfully");
                } else {
                    reqRes.setStatusCode(403);
                    reqRes.setMessage("User not authorized to delete this account.");
                }
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }

        return reqRes;
    }


    public ReqRes updateUser(Long userId, User updatedUser) {
        ReqRes reqRes = new ReqRes();

        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setFirstName(updatedUser.getFirstName());
                existingUser.setLastName(updatedUser.getLastName());
                existingUser.setRole(updatedUser.getRole());

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                User savedUser = userRepository.save(existingUser);
                reqRes.setUser(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }

        return reqRes;
    }

    public ReqRes updatePassword(Long userId, PasswordUpdateDTO passwordUpdateDTO) {
        ReqRes reqRes = new ReqRes();

        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();

                if (passwordEncoder.matches(passwordUpdateDTO.getCurrentPassword(), existingUser.getPassword())) {
                    if (!passwordUpdateDTO.getNewPassword().equals(passwordUpdateDTO.getCurrentPassword())) {
                        existingUser.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
                        userRepository.save(existingUser);
                        reqRes.setStatusCode(200);
                        reqRes.setMessage("Password updated successfully");
                    } else {
                        reqRes.setStatusCode(400);
                        reqRes.setMessage("New password cannot be the same as the current password");
                    }
                } else {
                    reqRes.setStatusCode(400);
                    reqRes.setMessage("Current password is incorrect");
                }
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating the password: " + e.getMessage());
        }

        return reqRes;
    }

    public ReqRes getMyInfo(String email){
        ReqRes reqRes = new ReqRes();

        try {
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                reqRes.setUser(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }

        return reqRes;
    }

    public Optional<User> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();

                return userRepository.findByEmail(email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public ReqRes updateDetails(UserUpdateDTO userUpdateDTO) {
        ReqRes reqRes = new ReqRes();

        try {
            Optional<User> currentUserOptional = getCurrentUser();

            if (currentUserOptional.isPresent()) {
                User currentUser = currentUserOptional.get();

                if (userUpdateDTO.getFirstName() != null) {
                    currentUser.setFirstName(userUpdateDTO.getFirstName());
                }
                if (userUpdateDTO.getLastName() != null) {
                    currentUser.setLastName(userUpdateDTO.getLastName());
                }

                if (userUpdateDTO.getEmail() != null) {
                    if (isValidEmail(userUpdateDTO.getEmail())) {
                        Optional<User> emailOwner = userRepository.findByEmail(userUpdateDTO.getEmail());
                        if (emailOwner.isPresent() && !emailOwner.get().getUserId().equals(currentUser.getUserId())) {
                            reqRes.setStatusCode(400);
                            reqRes.setMessage("Email is already taken by another user.");
                            return reqRes;
                        }
                        currentUser.setEmail(userUpdateDTO.getEmail());
                    } else {
                        reqRes.setStatusCode(400);
                        reqRes.setMessage("Invalid email format.");
                        return reqRes;
                    }
                }

                userRepository.save(currentUser);

                reqRes.setUser(currentUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User details updated successfully.");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found.");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user details: " + e.getMessage());
        }

        return reqRes;
    }
}

