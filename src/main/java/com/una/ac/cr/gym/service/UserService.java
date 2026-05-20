package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository uData;

    public List<User> getUsers(){
        return uData.findAll();
    }

    public List<User> filterUsers(String fullName, String role){
        boolean hasName = fullName != null && !fullName.trim().isEmpty();
        boolean hasRole = role != null && !role.trim().isEmpty();

        if(hasName && hasRole){
            return uData.findByFullNameContainingIgnoreCaseAndRole(fullName, role);
        }

        if(hasName){
            return uData.findByFullNameContainingIgnoreCase(fullName);
        }

        if(hasRole){
            return uData.findByRole(role);
        }

        return uData.findAll();
    }

    public List<User> getClients(){
        return uData.findByRole("client");
    }

    public Page<User> getUsersByPage(int page, int size){
        return uData.findAll(PageRequest.of(page, size));
    }

    public Page<User> filterUsersByPage(String fullName, String role, int page, int size){
        boolean hasName = fullName != null && !fullName.trim().isEmpty();
        boolean hasRole = role != null && !role.trim().isEmpty();
        Pageable pageable = PageRequest.of(page, size);

        if(hasName && hasRole){
            return uData.findByFullNameContainingIgnoreCaseAndRole(fullName, role, pageable);
        }

        if(hasName){
            return uData.findByFullNameContainingIgnoreCase(fullName, pageable);
        }

        if(hasRole){
            return uData.findByRole(role, pageable);
        }

        return uData.findAll(pageable);
    }

    public User getUserById(int id){
        return uData.findById(id).orElse(null);
    }

    public String validate(User u){
        Map<String, String> errors = validateFields(u);
        return errors.isEmpty() ? null : errors.values().iterator().next();
    }

    public Map<String, String> validateFields(User u){
        Map<String, String> errors = new LinkedHashMap<>();

        if(u == null){
            errors.put("form", "No se pudo guardar. Revise los campos marcados.");
            return errors;
        }

        if(isEmpty(u.getFullName())){
            errors.put("fullName", "Este campo es obligatorio.");
        }else if(!u.getFullName().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")){
            errors.put("fullName", "Ingrese un valor válido.");
        }

        if(isEmpty(u.getIdCard())){
            errors.put("idCard", "Este campo es obligatorio.");
        }else if(!u.getIdCard().matches("\\d{9}")){
            errors.put("idCard", "Ingrese un valor válido.");
        }else{
            User userByIdCard = uData.findByIdCard(u.getIdCard());
            if(userByIdCard != null && !userByIdCard.getUserId().equals(u.getUserId())){
                errors.put("idCard", "La cédula ya esta registrada.");
            }
        }

        if(isEmpty(u.getEmail())){
            errors.put("email", "Este campo es obligatorio.");
        }else if(!u.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
            errors.put("email", "Ingrese un correo electrónico válido.");
        }else{
            User userByEmail = uData.findByEmail(u.getEmail());
            if(userByEmail != null && !userByEmail.getUserId().equals(u.getUserId())){
                errors.put("email", "El correo ya esta registrado.");
            }
        }

        if(isEmpty(u.getPhone())){
            errors.put("phone", "Este campo es obligatorio.");
        }else if(!u.getPhone().matches("\\d{4}-\\d{4}")){
            errors.put("phone", "Ingrese un valor válido.");
        }else{
            User userByPhone = uData.findByPhone(u.getPhone());
            if(userByPhone != null && !userByPhone.getUserId().equals(u.getUserId())){
                errors.put("phone", "El teléfono ya esta registrado.");
            }
        }

        if(isEmpty(u.getUsername())){
            errors.put("username", "Este campo es obligatorio.");
        }else{
            User userByUsername = uData.findByUsername(u.getUsername());
            if(userByUsername != null && !userByUsername.getUserId().equals(u.getUserId())){
                errors.put("username", "El nombre de usuario ya esta registrado.");
            }
        }

        if(isEmpty(u.getPassword())){
            errors.put("password", "Este campo es obligatorio.");
        }else if(u.getPassword().length() < 4){
            errors.put("password", "Ingrese un valor válido.");
        }

        if(isEmpty(u.getRole())){
            errors.put("role", "Seleccione una opción.");
        }else if(!u.getRole().equals("client") && !u.getRole().equals("trainer")
                && !u.getRole().equals("administrator")){
            errors.put("role", "Seleccione una opción.");
        }

        if(isEmpty(u.getStatus())){
            errors.put("status", "Seleccione una opción.");
        }else if(!u.getStatus().equals("active") && !u.getStatus().equals("inactive")){
            errors.put("status", "Seleccione una opción.");
        }

        if(isEmpty(u.getRecordDate())){
            errors.put("recordDate", "La fecha es obligatoria.");
        }else{
            try {
                LocalDate.parse(u.getRecordDate());
            } catch (Exception ex) {
                errors.put("recordDate", "Ingrese una fecha válida.");
            }
        }

        return errors;
    }

    public boolean save(User u){
        String validation = validate(u);
        if(validation != null){
            return false;
        }
        uData.save(u);
        return true;
    }

    public boolean delete(int id){
        User user = getUserById(id);

        if(user == null){
            return false;
        }

        uData.deleteById(id);
        return true;
    }

    public User login(String username, String password){
        if(isEmpty(username) || isEmpty(password)){
            return null;
        }
        return uData.findByUsernameAndPasswordAndStatus(username, password, "active");
    }

    public boolean canManageUsers(HttpSession session){
        User u = (User) session.getAttribute("user");
        return u != null && ("administrator".equals(u.getRole()) || "trainer".equals(u.getRole()));
    }

    public String validateUserAccess(HttpSession session){
        if(!canManageUsers(session)){
            return "Solo administradores y entrenadores pueden administrar usuarios";
        }
        return null;
    }

    private boolean isEmpty(String text){
        return text == null || text.trim().isEmpty();
    }

    public boolean existsIdCard(String idCard){
        return uData.findByIdCard(idCard) != null;
    }

    public boolean existsEmail(String email){
        return uData.findByEmail(email) != null;
    }

    public boolean existsUsername(String username){
        return uData.findByUsername(username) != null;
    }

    public User findByIdCard(String idCard){
        return uData.findByIdCard(idCard);
    }

    public User findByEmail(String email){
        return uData.findByEmail(email);
    }

    public User findByUsername(String username){
        return uData.findByUsername(username);
    }

    public String changePassword(HttpSession session, String currentPassword, String newPassword, String confirmPassword){
        User userSession = (User) session.getAttribute("user");

        if(userSession == null){
            return "Debe iniciar sesión primero";
        }

        if(isEmpty(currentPassword) || isEmpty(newPassword) || isEmpty(confirmPassword)){
            return "Debe completar todos los campos";
        }

        if(!userSession.getPassword().equals(currentPassword)){
            return "La contrasena actual es incorrecta";
        }

        if(newPassword.length() < 4){
            return "La nueva contrasena debe tener al menos 4 caracteres";
        }

        if(!newPassword.equals(confirmPassword)){
            return "La nueva contrasena y la confirmacion no coinciden";
        }

        User user = getUserById(userSession.getUserId());

        if(user == null){
            return "Usuario no encontrado";
        }

        user.setPassword(newPassword);
        uData.save(user);

        session.setAttribute("user", user);

        return null;
    }
}
