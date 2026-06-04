package com.daniel.pfm;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {

    private static final String[] KEYS = {
        "DB_USERNAME", "DB_PASSWORD", "DB_HOST", "DB_PORT",
        "DB_DATABASE", "DB_NAME",
        "JWT_SECRET", "JWT_EXPIRATION", "JWT_REFRESH_EXPIRATION",
        "SPRING_PROFILES_ACTIVE"
    };

    public static void load() {
        // ignoreIfMissing: em produção (Render, Docker) não há .env — as vars já estão no ambiente
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        for (String key : KEYS) {
            // prioridade: .env > variável de ambiente já presente no processo
            String value = dotenv.get(key, System.getenv(key));
            if (value != null) {
                System.setProperty(key, value);
            }
        }
    }
}
