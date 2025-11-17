package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.dto.ImportHistoryDTO;
import me.vladislav.information_systems_1.dto.PageResponse;
import me.vladislav.information_systems_1.mapper.EntityMapper;
import me.vladislav.information_systems_1.model.User;
import me.vladislav.information_systems_1.repository.ImportHistoryRepository;

import java.util.List;

@ApplicationScoped
public class ImportHistoryService {

    @Inject
    private ImportHistoryRepository importHistoryRepository;
    @Inject
    private EntityMapper entityMapper;

    @Transactional
    public PageResponse<ImportHistoryDTO> getPage(Integer page,
                                                  Integer size,
                                                  String filterField,
                                                  String filterValue,
                                                  String sortField,
                                                  String sortOrder,
                                                  String login) {
        List<ImportHistoryDTO> imports = importHistoryRepository.getPage(page, size, filterField, filterValue, sortField, sortOrder, login)
                .stream()
                .map(entityMapper::toDTO)
                .toList();
        long totalCount = importHistoryRepository.countWithFilter(filterField, filterValue, login);
        Integer totalPages = (int) Math.ceil((double) totalCount / size);

        return new PageResponse<>(imports, totalPages);
    }
}
