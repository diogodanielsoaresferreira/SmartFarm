package pt.ua.es.smartfarm.ServiceLayer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * User entity.
 */
@Entity
@Data
public class Sfusers implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(length=256)
    private String password;

    @NotBlank
    @Column(unique=true)
    private String email;

    @NotNull
    private Boolean isActive;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateJoined;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date lastLogin;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name ="sfusers_pgroups", 
               joinColumns = @JoinColumn(name = "sfuser_id"), 
               inverseJoinColumns = @JoinColumn(name = "permissionGroup_id"))
    private Set<PermissionGroups> permissionGroupSet = new HashSet<>();

    /*
    public Sfusers(String email, String password, Boolean isActive, 
            String dj, String ll, Boolean alternative) {
        this.email = email;
        this.password = password;
        this.isActive = isActive;

        System.out.println(dj);
        try {
            this.dateJoined = new SimpleDateFormat("yyyy-MM-dd").parse(dj);
            this.lastLogin = new SimpleDateFormat("yyyy-MM-dd").parse(ll);
        } catch (ParseException e) {
        }
    }*/

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.length() == 0)
            return;
        this.password = password;
    }

    public Set<PermissionGroups> getPermissionGroupSet() {
        return permissionGroupSet;
    }
}
