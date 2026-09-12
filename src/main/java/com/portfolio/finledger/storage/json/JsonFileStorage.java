package com.portfolio.finledger.storage.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.portfolio.finledger.exception.StorageException;
import com.portfolio.finledger.storage.dto.DataSnapshot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Reads and writes a DataSnapshot as JSON.
 */
public class JsonFileStorage {

    private final Path file;
    private final JsonMapper objectMapper;

    public JsonFileStorage(Path file) {
        this.file = Objects.requireNonNull(file, "file must not be null");

        JsonMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        // Avoid scientific notation for BigDecimal values.
        mapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

        this.objectMapper = mapper;
    }

    /**
     * Saves the snapshot to the configured JSON file.
     *
     * @param snapshot data snapshot
     */
    public void save(DataSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot must not be null");

        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            objectMapper.writeValue(file.toFile(), snapshot);
        } catch (IOException | RuntimeException exception) {
            throw new StorageException("Failed to save data to " + file, exception);
        }
    }

    /**
     * Loads the snapshot from the configured JSON file.
     *
     * If the file does not exist, returns an empty snapshot.
     *
     * @return loaded data snapshot
     */
    public DataSnapshot load() {
        if (!Files.exists(file)) {
            return DataSnapshot.empty();
        }

        try {
            return objectMapper.readValue(file.toFile(), DataSnapshot.class);
        } catch (IOException | RuntimeException exception) {
            throw new StorageException("Failed to load data from " + file, exception);
        }
    }
}