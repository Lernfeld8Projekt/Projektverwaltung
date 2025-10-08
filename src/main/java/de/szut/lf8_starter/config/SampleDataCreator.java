package de.szut.lf8_starter.config;


import de.szut.lf8_starter.examples.hello.HelloEntity;
import de.szut.lf8_starter.examples.hello.HelloRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SampleDataCreator implements ApplicationRunner {

    private HelloRepository repository;

    public SampleDataCreator(HelloRepository repository) {
        this.repository = repository;
    }

    public void run(ApplicationArguments args) {
        repository.save(new HelloEntity("Hallo Welt!"));
        repository.save(new HelloEntity("Schöner Tag heute"));
        repository.save(new HelloEntity("FooBar"));

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
