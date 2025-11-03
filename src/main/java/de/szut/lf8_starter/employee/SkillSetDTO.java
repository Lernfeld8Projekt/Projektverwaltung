package de.szut.lf8_starter.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillSetDTO {
    private List<SkillDTO> skillSet;
}
