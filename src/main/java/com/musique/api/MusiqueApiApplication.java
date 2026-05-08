package com.musique.api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MusiqueApiApplication {

    public static void main(String[] args) {
        loadEnvFile();
        SpringApplication.run(MusiqueApiApplication.class, args);
    }

    /**
     * Loads {@code .env} from the process working directory (raiz do projeto ao rodar {@code mvn spring-boot:run}
     * ou a run configuration da IDE). Variaveis ja definidas no sistema operacional tem prioridade.
     */
    private static void loadEnvFile() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv
                .entries()
                .forEach(entry -> {
                    String key = entry.getKey();
                    if (System.getenv(key) == null && System.getProperty(key) == null) {
                        System.setProperty(key, entry.getValue());
                    }
                });
    }
}
