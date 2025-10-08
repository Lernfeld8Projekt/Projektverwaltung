package de.szut.lf8_starter.examples.hello;

import de.szut.lf8_starter.examples.hello.dto.HelloCreateDto;
import de.szut.lf8_starter.examples.hello.dto.HelloGetDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

public interface HelloControllerOpenAPI {

    @Operation(summary = "creates a new hello with its id and message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created hello",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HelloGetDto.class))}),
            @ApiResponse(responseCode = "400", description = "invalid JSON posted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content)})
    HelloGetDto create(HelloCreateDto helloCreateDto);

    @Operation(summary = "delivers a list of hello objects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of hellos",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = HelloGetDto.class)))}),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content)})
    List<HelloGetDto> findAll();

    @Operation(summary = "deletes a Hello by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "delete successful"),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "resource not found",
                    content = @Content)})
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void deleteHelloById(long id);


    @Operation(summary = "find hellos by message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of hellos who have the given message",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HelloGetDto.class))}),
            @ApiResponse(responseCode = "404", description = "qualification description does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content)})
    List<HelloGetDto> findAllEmployeesByQualification(String message);
}
