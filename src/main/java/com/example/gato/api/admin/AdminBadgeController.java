package com.example.gato.api.admin;

import com.example.gato.api.admin.dto.AdminBadgeDto;
import com.example.gato.api.admin.dto.BadgeUpsertDto;
import com.example.gato.service.AdminBadgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/badges")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminBadgeController {

    private final AdminBadgeService badges;

    @GetMapping
    public List<AdminBadgeDto> list() {
        return badges.list();
    }

    @GetMapping("/{id}")
    public AdminBadgeDto get(@PathVariable UUID id) {
        return badges.get(id);
    }

    @PostMapping
    public AdminBadgeDto create(@Valid @RequestBody BadgeUpsertDto dto) {
        return badges.create(dto);
    }

    @PutMapping("/{id}")
    public AdminBadgeDto update(@PathVariable UUID id, @Valid @RequestBody BadgeUpsertDto dto) {
        return badges.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        badges.delete(id);
    }
}
