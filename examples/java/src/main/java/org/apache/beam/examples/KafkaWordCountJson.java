
// beam-playground:
//   name: KafkaWordCountJson
//   description: Test example with Apache Kafka
//   multifile: false
//   context_line: 55
//   categories:
//     - Filtering
//     - Options
//     - Quickstart
//   complexity: MEDIUM
//   tags:
//     - filter
//     - strings
//     - emulator
//   emulators:
//      kafka:
//          topic:
//              id: dataset
//              dataset: dataset
//   datasets:
//      dataset:
//          location: local
//          format: json

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.values.KV;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaWordCountJson {
  static final String TOKENIZER_PATTERN = "[^\\p{L}]+";

  public static void main(String[] args) {
    final PipelineOptions options = PipelineOptionsFactory.create();
    final Pipeline p = Pipeline.create(options);

    final Map<String, Object> consumerConfig = new HashMap<>();
    consumerConfig.put("auto.offset.reset", "earliest");

    p.apply(
            KafkaIO.<Long, String>read()
                .withBootstrapServers(
                    "kafka_server:9092") // The argument is predefined to a correct value. Do not
                // change it manually.
                .withTopicPartitions(
                    Collections.singletonList(
                        new TopicPartition(
                            "dataset",
                            0))) // The argument is predefined to a correct value. Do not change it
                // manually.
                .withKeyDeserializer(LongDeserializer.class)
                .withValueDeserializer(StringDeserializer.class)
                .withConsumerConfigUpdates(consumerConfig)
                .withMaxNumRecords(5)
                .withoutMetadata())
        .apply(Values.create())
        .apply(
            "ExtractWords",
            ParDo.of(
                new DoFn<String, String>() {
                  @ProcessElement
                  public void processElement(ProcessContext c) {
                    for (String word : c.element().split(TOKENIZER_PATTERN, 0)) {
                      if (!word.isEmpty()) {
                        c.output(word);
                      }
                    }
                  }
                }))
        .apply(Count.perElement())
        .apply(
            "FormatResults",
            MapElements.via(
                new SimpleFunction<KV<String, Long>, String>() {
                  @Override
                  public String apply(KV<String, Long> input) {
                    System.out.printf("key: %s, value: %d%n", input.getKey(), input.getValue());
                    return input.getKey() + ": " + input.getValue();
                  }
                }))
        .apply(TextIO.write().to("word-counts"));

    p.run().waitUntilFinish();
  }
}
