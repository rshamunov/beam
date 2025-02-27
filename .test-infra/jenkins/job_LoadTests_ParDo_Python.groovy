/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import CommonJobProperties as commonJobProperties
import LoadTestsBuilder as loadTestsBuilder
import PhraseTriggeringPostCommitBuilder
import InfluxDBCredentialsHelper

def now = new Date().format("MMddHHmmss", TimeZone.getTimeZone('UTC'))

def loadTestConfigurations = { mode, datasetName ->
  [
    [
      title          : 'ParDo Python Load test: 2GB 100 byte records 10 times',
      test           : 'apache_beam.testing.load_tests.pardo_test',
      runner         : CommonTestProperties.Runner.DATAFLOW,
      pipelineOptions: [
        job_name             : "load-tests-python-dataflow-${mode}-pardo-1-${now}",
        project              : 'apache-beam-testing',
        region               : 'us-central1',
        temp_location        : 'gs://temp-storage-for-perf-tests/loadtests',
        publish_to_big_query : true,
        metrics_dataset      : datasetName,
        metrics_table        : "python_dataflow_${mode}_pardo_1",
        influx_measurement   : "python_${mode}_pardo_1",
        input_options        : '\'{' +
        '"num_records": 20000000,' +
        '"key_size": 10,' +
        '"value_size": 90}\'',
        iterations           : 10,
        number_of_counter_operations: 0,
        number_of_counters   : 0,
        num_workers          : 5,
        autoscaling_algorithm: 'NONE',
      ]
    ],
    [
      title          : 'ParDo Python Load test: 2GB 100 byte records 200 times',
      test           : 'apache_beam.testing.load_tests.pardo_test',
      runner         : CommonTestProperties.Runner.DATAFLOW,
      pipelineOptions: [
        job_name             : "load-tests-python-dataflow-${mode}-pardo-2-${now}",
        project              : 'apache-beam-testing',
        region               : 'us-central1',
        temp_location        : 'gs://temp-storage-for-perf-tests/loadtests',
        publish_to_big_query : true,
        metrics_dataset      : datasetName,
        metrics_table        : "python_dataflow_${mode}_pardo_2",
        influx_measurement   : "python_${mode}_pardo_2",
        input_options        : '\'{' +
        '"num_records": 20000000,' +
        '"key_size": 10,' +
        '"value_size": 90}\'',
        iterations           : 200,
        number_of_counter_operations: 0,
        number_of_counters   : 0,
        num_workers          : 5,
        autoscaling_algorithm: 'NONE',
      ]
    ],
    [
      title          : 'ParDo Python Load test: 2GB 100 byte records 10 counters',
      test           : 'apache_beam.testing.load_tests.pardo_test',
      runner         : CommonTestProperties.Runner.DATAFLOW,
      pipelineOptions: [
        job_name             : "load-tests-python-dataflow-${mode}-pardo-3-${now}",
        project              : 'apache-beam-testing',
        region               : 'us-central1',
        temp_location        : 'gs://temp-storage-for-perf-tests/loadtests',
        publish_to_big_query : true,
        metrics_dataset      : datasetName,
        metrics_table        : "python_dataflow_${mode}_pardo_3",
        influx_measurement   : "python_${mode}_pardo_3",
        input_options        : '\'{' +
        '"num_records": 20000000,' +
        '"key_size": 10,' +
        '"value_size": 90}\'',
        iterations           : 1,
        number_of_counter_operations: 10,
        number_of_counters   : 1,
        num_workers          : 5,
        autoscaling_algorithm: 'NONE',
      ]
    ],
    [
      title          : 'ParDo Python Load test: 2GB 100 byte records 100 counters',
      test           : 'apache_beam.testing.load_tests.pardo_test',
      runner         : CommonTestProperties.Runner.DATAFLOW,
      pipelineOptions: [
        job_name             : "load-tests-python-dataflow-${mode}-pardo-4-${now}",
        project              : 'apache-beam-testing',
        region               : 'us-central1',
        temp_location        : 'gs://temp-storage-for-perf-tests/loadtests',
        publish_to_big_query : true,
        metrics_dataset      : datasetName,
        metrics_table        : "python_dataflow_${mode}_pardo_4",
        influx_measurement   : "python_${mode}_pardo_4",
        input_options        : '\'{' +
        '"num_records": 20000000,' +
        '"key_size": 10,' +
        '"value_size": 90}\'',
        iterations           : 1,
        number_of_counter_operations: 100,
        number_of_counters   : 1,
        num_workers          : 5,
        autoscaling_algorithm: 'NONE',
      ]
    ],
  ].each { test -> test.pipelineOptions.putAll(additionalPipelineArgs) }
  .each{ test -> (mode != 'streaming') ?: addStreamingOptions(test) }
}

def addStreamingOptions(test) {
  test.pipelineOptions << [
    streaming: null,
    // Use the new Dataflow runner, which offers improved efficiency of Dataflow jobs.
    // See https://cloud.google.com/dataflow/docs/guides/deploying-a-pipeline#dataflow-runner-v2
    // for more details.
    experiments: 'use_runner_v2',
  ]
}

def loadTestJob = { scope, triggeringContext, jobType ->
  scope.description("Runs Python ParDo load tests on Dataflow runner in ${jobType} mode")
  commonJobProperties.setTopLevelMainJobProperties(scope, 'master', 200)

  def datasetName = loadTestsBuilder.getBigQueryDataset('load_test', triggeringContext)
  for (testConfiguration in loadTestConfigurations(jobType, datasetName)) {
    loadTestsBuilder.loadTest(scope, testConfiguration.title, testConfiguration.runner, CommonTestProperties.SDK.PYTHON, testConfiguration.pipelineOptions, testConfiguration.test)
  }
}

PhraseTriggeringPostCommitBuilder.postCommitJob(
    'beam_LoadTests_Python_ParDo_Dataflow_Batch',
    'Run Load Tests Python ParDo Dataflow Batch',
    'Load Tests Python ParDo Dataflow Batch suite',
    this
    ) {
      additionalPipelineArgs = [:]
      loadTestJob(delegate, CommonTestProperties.TriggeringContext.PR, "batch")
    }

CronJobBuilder.cronJob('beam_LoadTests_Python_ParDo_Dataflow_Batch', 'H H * * *', this) {
  additionalPipelineArgs = [
    influx_db_name: InfluxDBCredentialsHelper.InfluxDBDatabaseName,
    influx_hostname: InfluxDBCredentialsHelper.InfluxDBHostUrl,
  ]
  loadTestJob(delegate, CommonTestProperties.TriggeringContext.POST_COMMIT, "batch")
}

PhraseTriggeringPostCommitBuilder.postCommitJob(
    'beam_LoadTests_Python_ParDo_Dataflow_Streaming',
    'Run Python Load Tests ParDo Dataflow Streaming',
    'Load Tests Python ParDo Dataflow Streaming suite',
    this
    ) {
      additionalPipelineArgs = [:]
      loadTestJob(delegate, CommonTestProperties.TriggeringContext.PR, "streaming")
    }

CronJobBuilder.cronJob('beam_LoadTests_Python_ParDo_Dataflow_Streaming', 'H H * * *', this) {
  additionalPipelineArgs = [
    influx_db_name: InfluxDBCredentialsHelper.InfluxDBDatabaseName,
    influx_hostname: InfluxDBCredentialsHelper.InfluxDBHostUrl,
  ]
  loadTestJob(delegate, CommonTestProperties.TriggeringContext.POST_COMMIT, "streaming")
}
