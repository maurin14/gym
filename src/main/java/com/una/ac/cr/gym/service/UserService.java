package com.una.ac.cr.gym.service;






import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository uData;

    public List<User> getUsers(){
        return uData.findAll();
    }
    
    public List<User> getAdministrators(){
        return uData.findByRole("administrator");
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

    public Page<User> getUsersByPage(int page, int size){
        return uData.findAll(PageRequest.of(page - 1, size));
    }

    public User getUserById(int id){
        return uData.findById(id).orElse(null);
    }

    public String validate(User u){
        if(u == null){
            return "Datos inválidos";
        }

        if(isEmpty(u.getFullName()) || isEmpty(u.getIdCard()) || isEmpty(u.getEmail())
                || isEmpty(u.getPhone()) || isEmpty(u.getUsername())
                || isEmpty(u.getPassword()) || isEmpty(u.getRole())
                || isEmpty(u.getStatus()) || isEmpty(u.getRecordDate())){
            return "Debe completar todos los campos";
        }

        User userByIdCard = uData.findByIdCard(u.getIdCard());
        if(userByIdCard != null && !userByIdCard.getUserId().equals(u.getUserId())){
            return "La cédula ya está registrada";
        }

        User userByEmail = uData.findByEmail(u.getEmail());
        if(userByEmail != null && !userByEmail.getUserId().equals(u.getUserId())){
            return "El correo ya está registrado";
        }

        User userByUsername = uData.findByUsername(u.getUsername());
        if(userByUsername != null && !userByUsername.getUserId().equals(u.getUserId())){
            return "El nombre de usuario ya está registrado";
        }
        
        User userByPhone = uData.findByPhone(u.getPhone());
        if(userByPhone != null && !userByPhone.getUserId().equals(u.getUserId())){
            return "El teléfono ya está registrado";
        }

        if(!u.getFullName().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")){
            return "El nombre solo debe contener letras";
        }
        if(!u.getIdCard().matches("\\d+")){
            return "La cédula solo debe contener números";
        }
        if(!u.getPhone().matches("\\d{4}-\\d{4}")){
            return "El teléfono debe tener el formato 8888-8888";
        }
        if(!u.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
            return "El formato del correo no es válido";
        }
        if(!u.getRole().equals("client") && !u.getRole().equals("trainer") 
                && !u.getRole().equals("administrator")){
            return "El rol debe ser cliente, entrenador o administrador";
        }
        if(!u.getStatus().equals("active") && !u.getStatus().equals("inactive")){
            return "El estado debe ser activo o inactivo";
        }
        return null;
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
            return "La contraseña actual es incorrecta";
        }

        if(newPassword.length() < 4){
            return "La nueva contraseña debe tener al menos 4 caracteres";
        }

        if(!newPassword.equals(confirmPassword)){
            return "La nueva contraseña y la confirmación no coinciden";
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