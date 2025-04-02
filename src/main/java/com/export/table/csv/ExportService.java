package com.export.table.csv;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ExportService {

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final List<String> EXPORT_TABLES = Arrays.asList(
            "clientes",
            "produtos",
            "pedidos"
    );

    public void exportTablesToS3(String jobId) {
        logger.info("Iniciando job de exportação {}", jobId);

        try {
            // Exportar cada tabela em paralelo
            CompletableFuture<?>[] futures = new CompletableFuture[10];

            for (int i = 0; i < 10; i++) {
                String table = EXPORT_TABLES.get(i);

                futures[i] = CompletableFuture.runAsync(() -> {
                    try {
                        processTable(table, jobId);
                    } catch (Exception e) {
                        logger.error("Erro ao processar tabela {}: {}", table, e.getMessage(), e);
                    }
                }, executor);
            }

            // Aguardar a conclusão de todas as exportações
            CompletableFuture.allOf(futures).thenRun(() -> {
                logger.info("Job de exportação {} concluído", jobId);
            }).exceptionally(ex -> {
                logger.error("Erro fatal no job de exportação {}: {}", jobId, ex.getMessage(), ex);
                return null;
            });

        } catch (Exception e) {
            logger.error("Erro ao iniciar o job de exportação {}: {}", jobId, e.getMessage(), e);
        }
    }


    private void processTable(String table, String jobId) throws SQLException, IOException {
        Path csvFile = null;
        try {
            logger.info("Iniciando exportação da tabela: {} (Job: {})", table, jobId);

            // Gerar nome do arquivo CSV com timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            csvFile = Paths.get(TEMP_DIR, table + "_" + timestamp + ".csv");

            // Exportar dados para CSV
            long rowCount = exportTableToCSV(table, csvFile);

            // Fazer upload do CSV para S3
//            String s3Key = uploadToS3(csvFile, table);
//
            // Registrar informações no log
//            logger.info("Tabela {} exportada: {} linhas, S3 path: s3://{}/{}",
//                    table, rowCount, bucketName, s3Key);

        } finally {
            // Limpar arquivo temporário
            if (csvFile != null) {
                try {
                    Files.deleteIfExists(csvFile);
                } catch (IOException e) {
                    logger.warn("Não foi possível excluir o arquivo temporário: {}", csvFile, e);
                }
            }
        }
    }

    private long exportTableToCSV(String table, Path csvFile) throws SQLException, IOException {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             BufferedOutputStream outputStream = new BufferedOutputStream(
                     new FileOutputStream(csvFile.toFile()), 1024 * 1024)) {

            BaseConnection pgConnection = conn.unwrap(BaseConnection.class);
            CopyManager copyManager = new CopyManager(pgConnection);

            // Configurações de COPY para melhor performance
            String copySQL = "COPY " + table + " TO STDOUT (FORMAT CSV, HEADER, DELIMITER ',')";

            // Executar o COPY
            return copyManager.copyOut(copySQL, outputStream);
        }
    }

//    private String uploadToS3(Path file, String table) throws IOException {
//        // Nome do objeto no S3 (mantendo o nome do arquivo)
//        String key = prefix + file.getFileName().toString();
//
//        // Configurar cliente S3
//        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//                .withRegion(region)
//                .withCredentials(new DefaultAWSCredentialsProviderChain())
//                .build();
//
//        // Configurar metadados
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(Files.size(file));
//        metadata.setContentType("text/csv");
//
//        // Fazer upload do arquivo
//        try (InputStream inputStream = new BufferedInputStream(
//                new FileInputStream(file.toFile()), 1024 * 1024)) {
//
//            PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream, metadata);
//            s3Client.putObject(request);
//        }
//
//        return key;
//    }
}
