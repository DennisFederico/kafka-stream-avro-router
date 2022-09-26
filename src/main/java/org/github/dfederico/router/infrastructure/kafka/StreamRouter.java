package org.github.dfederico.router.infrastructure.kafka;

import java.util.List;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.processor.RecordContext;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.github.dfederico.router.config.AppConfig;
import org.github.dfederico.router.config.AppConfig.RouteConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamRouter {

    public StreamsBuilder createAvroStreamRouter(AppConfig appConfig) {
        final StreamsBuilder builder = new StreamsBuilder();
        // final Serde<GenericRecord> valueAvroSerde = new GenericAvroSerde();
        // var stream = builder.stream(appConfig.getInputTopic(),
        // Consumed.with(Serdes.String(), valueAvroSerde));

        KStream<Object, GenericRecord> stream = builder.stream(appConfig.getInputTopic());
        stream.print(Printed.toSysOut());
        stream.to(new GenerationTopicExtractor(appConfig));
        return builder;
    }

    class GenerationTopicExtractor implements TopicNameExtractor<Object, GenericRecord> {

        List<AppConfig.RouteConfig> routes;
        String defaultTopic;

        public GenerationTopicExtractor(AppConfig appConfig) {
            this.routes = appConfig.getRoutes();
            this.defaultTopic = appConfig.getDefaultRouteTopic();
        }

        @Override
        public String extract(Object key, GenericRecord value, RecordContext recordContext) {

            final int birthYear = (Integer) value.get("BirthYear");
            String topic = routes.stream()
                    .filter(rule -> birthYear >= rule.getLowerBound() && birthYear <= rule.getUpperBound())
                    .findFirst()
                    .map(RouteConfig::getTopic)
                    .orElse(defaultTopic);

            log.info("FOR YEAR {} -> TOPIC:'{}'", birthYear, topic);

            return topic;
        }

    }
}
