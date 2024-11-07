package edu.kudago.service;

import edu.kudago.exceptions.RoleNotFoundException;
import edu.kudago.model.Role;
import edu.kudago.repository.entity.RoleEntity;
import edu.kudago.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @Transactional
    public RoleEntity save(RoleEntity role) {
        return roleRepository.save(role);
    }

    public RoleEntity getRoleByName(Role role) throws RoleNotFoundException {
        return roleRepository.findByName(role).orElseThrow(() -> new RoleNotFoundException(role));
    }

    public RoleEntity getRoleById(Long id) throws RoleNotFoundException {
        return roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(id));
    }

    @Transactional
    public boolean roleExists(Role role) {
        return roleRepository.existsByName(role);
    }


}
