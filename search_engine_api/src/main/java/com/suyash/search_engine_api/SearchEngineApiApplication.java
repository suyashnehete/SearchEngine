package com.suyash.search_engine_api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.suyash.search_engine_api.index.IndexerService;

import lombok.AllArgsConstructor;

@SpringBootApplication
@AllArgsConstructor
@EnableJpaAuditing
public class SearchEngineApiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SearchEngineApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }

}
