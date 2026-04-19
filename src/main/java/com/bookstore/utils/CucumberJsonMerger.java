package com.bookstore.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CucumberJsonMerger {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private CucumberJsonMerger() {
    }

    public static void main(String[] args) throws IOException {
        Path inputPath = args.length > 0 ? Path.of(args[0]) : Path.of("target", "cucumber", "raw");
        Path outputPath = args.length > 1 ? Path.of(args[1]) : Path.of("target", "cucumber", "cucumber.json");

        Files.createDirectories(outputPath.getParent());

        List<Path> jsonFiles = collectJsonFiles(inputPath);
        if (jsonFiles.isEmpty()) {
            Files.writeString(outputPath, "[]");
            System.out.println("No raw cucumber JSON files found. Created empty merged file at " + outputPath);
            return;
        }

        if (jsonFiles.size() == 1) {
            Files.copy(jsonFiles.get(0), outputPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied raw cucumber JSON to " + outputPath);
            return;
        }

        ArrayNode mergedResults = OBJECT_MAPPER.createArrayNode();
        for (Path jsonFile : jsonFiles) {
            JsonNode rootNode = OBJECT_MAPPER.readTree(jsonFile.toFile());
            if (rootNode == null || rootNode.isNull()) {
                continue;
            }
            if (rootNode.isArray()) {
                mergedResults.addAll((ArrayNode) rootNode);
            } else {
                mergedResults.add(rootNode);
            }
        }

        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), mergedResults);
        System.out.println("Merged " + jsonFiles.size() + " cucumber JSON files into " + outputPath);
    }

    private static List<Path> collectJsonFiles(Path inputPath) throws IOException {
        if (Files.isRegularFile(inputPath) && inputPath.toString().endsWith(".json")) {
            return List.of(inputPath);
        }

        if (!Files.exists(inputPath)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(inputPath)) {
            return stream
                    .filter(path -> path.toString().endsWith(".json"))
                    .sorted(Comparator.comparing(Path::toString))
                    .collect(Collectors.toList());
        }
    }
}
