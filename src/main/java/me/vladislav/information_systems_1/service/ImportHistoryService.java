package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.cache.LogCacheStats;
import me.vladislav.information_systems_1.dto.ImportHistoryDTO;
import me.vladislav.information_systems_1.dto.PageResponse;
import me.vladislav.information_systems_1.mapper.EntityMapper;
import me.vladislav.information_systems_1.model.ImportHistory;
import me.vladislav.information_systems_1.model.User;
import me.vladislav.information_systems_1.repository.ImportHistoryRepository;
import me.vladislav.information_systems_1.repository.UserRepository;

import java.util.List;

import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;

@ApplicationScoped
@LogCacheStats
public class ImportHistoryService {

    @Inject
    private ImportHistoryRepository importHistoryRepository;

    @Inject
    private UserRepository userRepository;

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

    @Transactional(REQUIRES_NEW)
    public ImportHistory add(ImportHistoryDTO dto) {
        User user = userRepository.getByLogin(dto.getLogin())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getLogin()));

        ImportHistory history = new ImportHistory();
        history.setUser(user);
        history.setStatus(dto.getStatus());
        history.setImportedCount(dto.getImportedCount());

        importHistoryRepository.save(history);
        return history;
    }

}
