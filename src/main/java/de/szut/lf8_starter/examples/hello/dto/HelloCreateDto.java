package de.szut.lf8_starter.examples.hello.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HelloCreateDto {

    @Size(min = 3, message = "at least length of 3")
    private String message;

    @JsonCreator
    public HelloCreateDto(String message) {
        this.message = message;
    }
}
