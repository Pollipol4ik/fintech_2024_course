package edu.kudago.exceptions;


import edu.kudago.model.Role;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(Role role) {
        super("Роль " + role.getDescription() + " не была найдена");
    }

    public RoleNotFoundException(Long id) {
        super("Роль с id=" + id + " не была найдена");
    }
}
