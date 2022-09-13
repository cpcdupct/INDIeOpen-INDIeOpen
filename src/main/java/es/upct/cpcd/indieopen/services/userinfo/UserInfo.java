package es.upct.cpcd.indieopen.services.userinfo;

import com.cpcd.microservices.app.servicescommons.models.entity.Usuario;
import lombok.Data;

@Data
public class UserInfo {
    private String id;
    private String nombre;
    private String apellidos;
    private String email;
    private String avatar;

    public UserInfo(String id, String nombre, String apellidos, String email, String avatar) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.avatar = avatar;
    }

    public static UserInfo from(Usuario usuario) {
        return new UserInfo(usuario.getId(), usuario.getNombre(), usuario.getApellido(), usuario.getEmail(), usuario.getAvatar());
    }

    public String getCompleteName() {
        return nombre + " " + apellidos;
    }


}
