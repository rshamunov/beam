# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

variable "project_id" {
  description = "The ID of the Google Cloud project within which resources are provisioned"
}

variable "pg_infra_trigger_name" {
  description = "Trigger name to deploy Playground infrastructure"
  default     = "Initialize-Playground-Environment"
}

variable "pg_gke_trigger_name" {
  description = "Trigger name to deploy GKE for Playground"
  default     = "Deploy-Playground-Environment"
}

variable "pg_helm_upd_trigger_name" {
  description = "Trigger name to update Playground environment"
  default     = "Update-Playground-Environment"
}

variable "pg_ci_trigger_name" {
  description = "Trigger name to run CI validation"
  default = "Validate-Examples-CI"
}

variable "pg_cd_trigger_name" {
  description = "Trigger name to run CD checks"
  default = "Deploy-Examples-CD"
}

variable "playground_deploy_sa" {
  description = "Service Account ID responsible for deploying the Playground"
  default     = "playground-sa-deploy-env06"
}

variable "playground_helm_upd_sa" {
  description = "Service Account ID responsible for Playground environment updates"
  default     = "playground-sa-update-env06"
}

variable "playground_ci_sa" {
  description = "Service Account ID responsible for running CI (examples validation)"
  default     = "playground-sa-ci-env06"
}


variable "playground_cd_sa" {
  description = "Service Account ID responsible for running CD (examples integration)"
  default     = "playground-sa-cd-env06"
}

variable "playground_environment_name" {
  description = "Playground environment name. Multiple environments can be deployed to the same GCP project."
}

variable "playground_dns_name" {
  description = "The DNS A-record (FQDN) for Playground website"
  default     = "env06.pgtestsandbox.com"
}

variable "playground_network_name" {
  description = "The Google Cloud Platform VPC Name for Playground deployment"
  default     = "playground-network-env06"
}

variable "playground_subnetwork_name" {
  description = "The Google Cloud Platform VPC Subnetwork Name for Playground deployment"
  default     = "playground-subnetwork-env06"
}

variable "playground_gke_name" {
  description = "The Google Cloud Platform GKE Cluster name for Playground deployment"
  default     = "playground-backend-env06"
}

variable "state_bucket" {
  description = "The Google Cloud Platform GCS bucket name for Playground Terraform state file"
  default     =  "tfstate-pg-sandbox-0011-env06"
}


variable "image_tag" {
  description = "The docker images tag for Playground images"
  default     =  "mytag"
}

variable "docker_repository_root" {
  description = "The name of Google Cloud Platform (GCP) Artifact Registry Repository where Playground images will be saved to"
}

variable "playground_region" {
  description = "The Google Cloud Platform (GCP) region (For example: us-central1) where playground infrastructure will be deployed to"
  default     =  "us-east1"
}


variable "playground_zone" {
  description = "The Google Cloud Platform (GCP) zone (For example: us-central1-b) where playground infrastructure will be deployed to"
  default     =  "us-east1-b"
}

variable "sdk_tag" {
  description = <<EOF
Apache Beam Golang and Python images SDK tag. (For example current latest: 2.44.0)
See more: https://hub.docker.com/r/apache/beam_python3.7_sdk/tags and https://hub.docker.com/r/apache/beam_go_sdk"
  EOF
  default = "2.44.0"
}

variable "appengine_flag" {
  description = "Boolean. If AppEngine and Datastore need to be installed. Put 'false' if AppEngine and Datastore already installed"
  default     =  "false"
}


variable "gke_machine_type" {
  description = "Machine type for GKE Nodes. Default: e2-standard-8"
  default = "e2-standard-8"
}

variable "ipaddress_name" {
  description = "The GCP Static IP Address name for Playground deployment"
  default = "playground-static-ip-env06"
}

variable "max_count" {
  description = "Max node count for GKE cluster. Default: 4"
  default = 4
}

variable "min_count" {
  description = "Min node count for GKE cluster. Default: 2"
  default = 2
}

variable "redis_name" {
  description = "The Google Cloud Platform redis instance name for Playground"
  default = "playground-redis-env06"
}

variable "redis_tier" {
  description = "The tier of the GCP redis instance (BASIC, STANDARD)"
  default = "BASIC"
}

variable "playground_service_account" {
  description = "GCP service account name for Playground GKE"
  default = "playground-gke-account-env06"
}

variable "datastore_namespace" {
  description = "The name of Playground Datastore namespace"
  default = "Playground2"
}

variable "webhook_trigger_secret_id" {
  description = "The name of the secret for webhook config cloud build trigger (CI/CD)"
  default = "webhook-ci-env06"
}

variable "gh_pat_secret" {
  description = "The name of the secret for GitHub Personal Access Token. Required for cloud build trigger (CI/CD)"
  default = "patsecret"
}

variable "data_for_cicd_webhook_secret" {
  description = "Secret value for Cloud Build WebHook trigger"
  default = "cisecret"
}

variable "data_for_github_pat_secret" {
  description = "The GitHub account Personal Access Token"
  default = "patsecretdata"
}

variable "private_logs_bucket" {
  description = "The GCS bucket name to store triggers logs"
  default = "tfstate-pg-sandbox-0011-cilogs-private-env06"
}
