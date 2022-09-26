package org.github.dfederico.router;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.github.dfederico.router.config.AppConfig;
import org.github.dfederico.router.config.ConfigHandler;
import org.github.dfederico.router.infrastructure.kafka.StreamRouter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main(String[] args) throws IOException {

        Properties kafkaProperties = ConfigHandler.getKafkaProperties(args[0]);
        AppConfig appConfig = ConfigHandler.getAppConfig(args[1]);            
        ConfigHandler.setAdditionalAppProperties(kafkaProperties, appConfig);

        //SERDES AS STRING
        //ConfigHandler.setStringSerdesProperties(kafkaProperties, appConfig);
        //VALUE SERDES AS AVRO
        ConfigHandler.setAvroSerdesProperties(kafkaProperties, appConfig);



        StreamsBuilder builder = new StreamRouter().createAvroStreamRouter(appConfig);

        Topology topology = builder.build();
        log.info(topology.describe().toString());

        KafkaStreams kafkaStreams = new KafkaStreams(topology, kafkaProperties);
        kafkaStreams.setUncaughtExceptionHandler((e) -> {
            log.error(null, e);
            return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_APPLICATION;
        });

        kafkaStreams.cleanUp();
        kafkaStreams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }
}
