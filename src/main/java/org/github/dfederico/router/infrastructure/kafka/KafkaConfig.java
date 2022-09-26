package org.github.dfederico.router.infrastructure.kafka;

import java.util.Properties;

import org.apache.kafka.streams.StreamsConfig;
import org.github.dfederico.router.config.AppConfig;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.serialization.Serdes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KafkaConfig {

    public static Properties createStreamsConfigProperties(String applicationId, AppConfig appConfig) {

        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
    
        return streamsConfiguration;
      }
}
