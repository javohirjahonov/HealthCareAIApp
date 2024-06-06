package com.example.HealthCare.repository;

import com.example.HealthCare.domain.dto.request.response.StandardResponse;
import com.example.HealthCare.domain.dto.request.response.Status;
import com.example.HealthCare.domain.dto.request.role.RoleAssignDto;
import com.example.HealthCare.domain.dto.request.role.RoleDto;
import com.example.HealthCare.domain.entity.role.PermissionEntity;
import com.example.HealthCare.domain.entity.role.RoleEntity;
import com.example.HealthCare.domain.entity.user.UserEntity;
import com.example.HealthCare.exception.DataNotFoundException;
import com.example.HealthCare.exception.UniqueObjectException;
import com.example.HealthCare.exception.UserBadRequestException;
import com.example.HealthCare.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;


    public StandardResponse<RoleEntity> save(RoleDto roleDto) {
        RoleEntity roleEntityByName = roleRepository.findRoleEntitiesByName(roleDto.getName());
        // If role already exists
        if (roleEntityByName != null) throw new UniqueObjectException("Role already exists");
        // If role doesn't exists in db
        List<String> permissions = roleDto.getPermissions();
        List<PermissionEntity> rolePermission = new ArrayList<>();
        // Checking if db has its permissions
        for (String permission : permissions) {
            PermissionEntity permissionEntitiesByPermission = permissionRepository.findPermissionEntitiesByPermission(permission);
            // If not we create a new permission
            if (permissionEntitiesByPermission == null) {
                permissionEntitiesByPermission = PermissionEntity.builder().permission(permission).build();
                rolePermission.add(permissionRepository.save(permissionEntitiesByPermission));
            }else {
                rolePermission.add(permissionEntitiesByPermission);
            }
        }
        RoleEntity roleEntity = RoleEntity.builder().name(roleDto.getName()).permissions(rolePermission).build();
        roleEntity = roleRepository.save(roleEntity);
        return StandardResponse.<RoleEntity>builder().status(Status.SUCCESS).message("Role successfully created").data(roleEntity).build();
    }

    public StandardResponse<RoleEntity> getRole(String name) {
        RoleEntity roleEntity = roleRepository.findRoleEntityByName(name).orElseThrow(() -> new DataNotFoundException("Role not found"));
        return StandardResponse.<RoleEntity>builder().status(Status.SUCCESS).message("Role successfully sent").data(roleEntity).build();
    }

    public StandardResponse<RoleEntity> update(RoleDto roleDto) {
        RoleEntity roleEntityByName = roleRepository.findRoleEntityByName(roleDto.getName()).orElseThrow(() -> new DataNotFoundException("Role not found"));

        if(roleDto.getPermissions() != null) {
            List<PermissionEntity> updatedPermissions = new ArrayList<>();
            for (String roleDtoPermission : roleDto.getPermissions()) {
                PermissionEntity permission = permissionRepository.findPermissionEntitiesByPermission(roleDtoPermission);
                if (permission != null) {
                    updatedPermissions.add(permission);
                } else {
                    PermissionEntity build = PermissionEntity.builder().permission(roleDtoPermission).build();
                    updatedPermissions.add(permissionRepository.save(build));
                }
            }
            roleEntityByName.setPermissions(updatedPermissions);
        }
        roleEntityByName.setUpdatedDate(LocalDateTime.now());
        return StandardResponse.<RoleEntity>builder().status(Status.SUCCESS)
                .message("Permissions successfully added to the role")
                .data(roleRepository.save(roleEntityByName))
                .build();
    }

    public StandardResponse<String> assignRoleToUser(RoleAssignDto roleAssignDto, Principal principal) {
        if(Objects.equals(roleAssignDto.getName(), "OWNER")) throw new AccessDeniedException("Unacceptable role name");
        RoleEntity roleEntity = roleRepository.findRoleEntityByName(roleAssignDto.getName())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        UserEntity user = userRepository.findByEmail(roleAssignDto.getEmail())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        UserEntity userEntity = userRepository.findByEmail(principal.getName()).orElseThrow();

        List<RoleEntity> roles = user.getRoles();
        for (RoleEntity role : roles) {
            if(role.equals(roleEntity)) throw new UserBadRequestException("User already has "+role.getName()+" role");
        }
        List<String> permissions = roleAssignDto.getPermissions();
        List<PermissionEntity> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            for (PermissionEntity roleEntityPermission : roleEntity.getPermissions()) {
                if(permission.equals(roleEntityPermission.getPermission())){
                    permissionList.add(roleEntityPermission);
                }
            }
        }
        roles.add(roleEntity);
        user.setRoles(roles);
        user.setPermissions(permissionList);
        userRepository.save(user);
        return StandardResponse.<String>builder().status(Status.SUCCESS).message("Role successfully assigned to " + user.getEmail()).build();
    }

    public StandardResponse<String> addPermissionsToUser(RoleAssignDto roleAssignDto) {
        RoleEntity roleEntity = roleRepository.findRoleEntityByName(roleAssignDto.getName())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        UserEntity user = userRepository.findByEmail(roleAssignDto.getEmail())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        List<PermissionEntity> permissionList = user.getPermissions();

        List<RoleEntity> roles = user.getRoles();
        for (RoleEntity role : roles) {
            if(role.equals(roleEntity)){
                List<String> permissions = roleAssignDto.getPermissions();
                for (String permission : permissions) {
                    for (PermissionEntity roleEntityPermission : roleEntity.getPermissions()) {
                        if(permission.equals(roleEntityPermission.getPermission())){
                            permissionList.add(roleEntityPermission);
                        }
                    }
                }
                break;
            }
        }

        user.setPermissions(permissionList);
        userRepository.save(user);
        return StandardResponse.<String>builder().status(Status.SUCCESS).message("Permissions successfully added to "+user.getEmail()).build();
    }


//    public StandardResponse<DoctorSpecialty> saveDoctorSpecialty(DoctorSpecialtyDto doctorSpecialtyDto){
//        DoctorSpecialty specialty = modelMapper.map(doctorSpecialtyDto, DoctorSpecialty.class);
//        return StandardResponse.<DoctorSpecialty>builder().status(Status.SUCCESS)
//                .message("Doctor specialty created successfully")
//                .data(doctorSpecialtyRepository.save(specialty))
//                .build();
//    }


}
