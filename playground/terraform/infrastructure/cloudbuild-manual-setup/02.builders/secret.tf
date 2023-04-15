# Secret for Cloud Build WebHook trigger
resource "google_secret_manager_secret" "secret_webhook_cloudbuild_trigger_cicd" {
  secret_id = var.webhook_trigger_secret_id

  replication {
    automatic = true
  }
}

# PAT for GitHub account used to write commit messages for CI checks
resource "google_secret_manager_secret" "secret_gh_pat_cloudbuild" {
  secret_id = var.gh_pat_secret

  replication {
    automatic = true
  }
}

resource "google_secret_manager_secret_version" "secret_webhook_cloudbuild_trigger_cicd_data" {
  secret = google_secret_manager_secret.secret_webhook_cloudbuild_trigger_cicd.id
  secret_data = var.data_for_cicd_webhook_secret
}

resource "google_secret_manager_secret_version" "secret_gh_pat_cloudbuild_data" {
  secret = google_secret_manager_secret.secret_gh_pat_cloudbuild.id
  secret_data = var.data_for_github_pat_secret
}

resource "google_project_iam_binding" "secret_webhook_cloudbuild_trigger_cicd_access" {
  project = var.project_id
  secret_id = google_secret_manager_secret.secret_webhook_cloudbuild_trigger_cicd.secret_id
  role    = "roles/secretmanager.secretAccessor"

  members = [
    "serviceAccount:${google_service_account.playground_ci_sa.email}",
    "serviceAccount:${google_service_account.playground_cd_sa.email}"
  ]
}