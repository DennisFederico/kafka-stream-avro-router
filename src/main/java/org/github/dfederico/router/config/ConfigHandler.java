package org.github.dfederico.router.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfigHandler {

  public static Properties getKafkaProperties(String configFileName) throws IOException {

    Properties appProperties = new Properties();
    try (BufferedReader reader = Files.newBufferedReader(Path.of(configFileName))) {
      appProperties.load(reader);
    }
    return appProperties;
  }

  public static AppConfig getAppConfig(String configFileName) throws IOException {

    Constructor constructor = new Constructor(AppConfig.class);
    Yaml yaml = new Yaml(constructor);
    try (BufferedReader reader = Files.newBufferedReader(Path.of(configFileName))) {
      return yaml.load(reader);
    }
  }

  public static void setAdditionalAppProperties(Properties properties, AppConfig appConfig) {
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, appConfig.getApplicationId());
    properties.put(StreamsConfig.STATE_DIR_CONFIG, appConfig.getTempStateDir());

  }

  public static void setStringSerdesProperties(Properties properties, AppConfig appConfig) {
    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
  }

  public static void setAvroSerdesProperties(Properties properties, AppConfig appConfig) {
    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
  }
}
