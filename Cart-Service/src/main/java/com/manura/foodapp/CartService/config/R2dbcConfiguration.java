package com.manura.foodapp.CartService.config;


import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.lang.NonNull;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;

@Configuration
@Profile(value = "!test")
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {

    @Value("${database.name}")
    private String database;

    @Value("${database.host}")
    private String host;

    @Value("${database.port:5432}")
    private int port;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Override
    @Bean
    @NonNull
    public ConnectionFactory connectionFactory() {

    	return ConnectionFactories.get(
                ConnectionFactoryOptions.builder()
                        .option(DRIVER, "postgresql")
                        .option(HOST, "localhost")
                        .option(PORT, port)
                        .option(USER, username)
                        .option(PASSWORD, password)
                        .option(DATABASE, database)
                        .option(MAX_SIZE, 40)
                        .build());
    	
    }

}


