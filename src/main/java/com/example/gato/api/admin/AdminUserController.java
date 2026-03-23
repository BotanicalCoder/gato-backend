package com.example.gato.api.admin;

import com.example.gato.api.admin.dto.AdminUserDto;
import com.example.gato.api.admin.dto.UpdateAdminStatusDto;
import com.example.gato.api.admin.dto.UpdateUserDto;
import com.example.gato.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserController {

    private final AdminUserService users;

    @GetMapping
    public List<AdminUserDto> list() {
        return users.list();
    }

    @GetMapping("/{id}")
    public AdminUserDto get(@PathVariable UUID id) {
        return users.get(id);
    }

    @PutMapping("/{id}")
    public AdminUserDto update(@PathVariable UUID id, @Valid @RequestBody UpdateUserDto dto) {
        return users.update(id, dto);
    }

    @PatchMapping("/{id}/admin")
    public AdminUserDto updateAdminStatus(@PathVariable UUID id, @RequestBody UpdateAdminStatusDto dto) {
        return users.updateAdminStatus(id, dto.admin());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        users.delete(id);
    }
}
