package com.example.knowledgegraph;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.knowledgegraph.mapper")
@SpringBootApplication
public class KnowledgeGraphApplication {
    public static void main(String[] args) {
        SpringApplication.run(KnowledgeGraphApplication.class, args);
    }
}
