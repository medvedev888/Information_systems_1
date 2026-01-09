package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.cache.LogCacheStats;
import me.vladislav.information_systems_1.dto.ImportHistoryDTO;
import me.vladislav.information_systems_1.dto.PageResponse;
import me.vladislav.information_systems_1.exception.ImportFileNotAvailableException;
import me.vladislav.information_systems_1.exception.ImportHistoryNotFoundException;
import me.vladislav.information_systems_1.mapper.EntityMapper;
import me.vladislav.information_systems_1.model.ImportHistory;
import me.vladislav.information_systems_1.model.Status;
import me.vladislav.information_systems_1.model.User;
import me.vladislav.information_systems_1.repository.ImportHistoryRepository;
import me.vladislav.information_systems_1.repository.UserRepository;

import java.io.InputStream;
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

    @Inject
    private MinioService minioService;

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
    public ImportHistoryDTO add(ImportHistoryDTO dto) {
        User user = userRepository.getByLogin(dto.getLogin())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getLogin()));

        ImportHistory history = new ImportHistory();
        history.setUser(user);
        history.setStatus(dto.getStatus());
        history.setImportedCount(dto.getImportedCount());

        history = importHistoryRepository.save(history);
        return entityMapper.toDTO(history);
    }

    @Transactional(REQUIRES_NEW)
    public void update(ImportHistoryDTO dto) {
        ImportHistory importHistory = importHistoryRepository.getById(dto.getId())
                .orElseThrow(() -> new ImportHistoryNotFoundException("Import history not found"));

        if (dto.getStatus() != null) {
            importHistory.setStatus(dto.getStatus());
        }

        if (dto.getImportedCount() != null) {
            importHistory.setImportedCount(dto.getImportedCount());
        }
    }

    @Transactional
    public InputStream downloadImportFile(Long importId) {

        ImportHistory history = importHistoryRepository.getById(importId)
                .orElseThrow(() ->
                        new ImportHistoryNotFoundException("Import not found: " + importId)
                );

        if (history.getStatus() != Status.SUCCESS) {
            throw new ImportFileNotAvailableException(
                    "Import " + importId + " is not successful, file is not available"
            );
        }

        return minioService.download("import_" + history.getId().toString() + ".txt");
    }

}
