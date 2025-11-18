package me.vladislav.information_systems_1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.vladislav.information_systems_1.model.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportHistoryDTO {
    private Long id;
    private String login;
    private Status status;
    private Integer importedCount;
}
