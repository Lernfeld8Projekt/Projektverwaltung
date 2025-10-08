package de.szut.lf8_starter.examples.hello;


import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.examples.hello.dto.HelloCreateDto;
import de.szut.lf8_starter.examples.hello.dto.HelloGetDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/hello")
public class HelloController implements HelloControllerOpenAPI{
    private final HelloService service;
    private final HelloMapper helloMapper;

    public HelloController(HelloService service, HelloMapper mappingService) {
        this.service = service;
        this.helloMapper = mappingService;
    }


    @PostMapping
    public HelloGetDto create(@RequestBody @Valid HelloCreateDto helloCreateDto) {
        HelloEntity helloEntity = this.helloMapper.mapCreateDtoToEntity(helloCreateDto);
        helloEntity = this.service.create(helloEntity);
        return this.helloMapper.mapToGetDto(helloEntity);
    }


    @GetMapping
    public List<HelloGetDto> findAll() {
        return this.service
                .readAll()
                .stream()
                .map(e -> this.helloMapper.mapToGetDto(e))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void deleteHelloById(@PathVariable long id) {
        var entity = this.service.readById(id);
        if (entity == null) {
            throw new ResourceNotFoundException("HelloEntity not found on id = " + id);
        } else {
            this.service.delete(entity);
        }
    }


    @GetMapping("/findByMessage")
    public List<HelloGetDto> findAllEmployeesByQualification(@RequestParam String message) {
        return this.service
                .findByMessage(message)
                .stream()
                .map(e -> this.helloMapper.mapToGetDto(e))
                .collect(Collectors.toList());
    }
}
