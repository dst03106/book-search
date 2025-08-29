package me.yunhui.catalog.infrastructure.runner;

import me.yunhui.catalog.application.DataSeedService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("seed")
public class DataSeedRunner implements CommandLineRunner {
    
    private final DataSeedService dataSeedService;
    
    public DataSeedRunner(DataSeedService dataSeedService) {
        this.dataSeedService = dataSeedService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting data seed process...");
        dataSeedService.seedBooksFromItBookstore();
        System.out.println("Data seed process completed.");
    }
}