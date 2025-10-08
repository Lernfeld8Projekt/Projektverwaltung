package de.szut.lf8_starter.examples.hello;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HelloService {
    private final HelloRepository repository;

    public HelloService(HelloRepository repository) {
        this.repository = repository;
    }

    public HelloEntity create(HelloEntity entity) {
        return this.repository.save(entity);
    }

    public List<HelloEntity> readAll() {
        return this.repository.findAll();
    }

    public HelloEntity readById(long id) {
        Optional<HelloEntity> optionalQualification = this.repository.findById(id);
        if (optionalQualification.isEmpty()) {
            return null;
        }
        return optionalQualification.get();
    }


    public void delete(HelloEntity entity) {
        this.repository.delete(entity);
    }

    public List<HelloEntity> findByMessage(String message) {
        return this.repository.findByMessage(message);
    }
}
