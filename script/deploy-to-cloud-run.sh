#!/bin/bash
set -euo pipefail

PROJECT_ID=$1
REGION=$2
REPO_NAME=$3
IMAGE_NAME=$4
CLOUD_SQL=$5
PROFILE=$6
IMAGE_SHA=$7

mapfile -t SECRETS < <(gcloud secrets list --project=$PROJECT_ID --format='value(name)')

UPDATE_SECRETS=()
for SECRET in "${SECRETS[@]}"; do
    SECRET=$(echo "$SECRET" | xargs)  # 공백 제거 (trim)
    if [ -n "$SECRET" ]; then
        echo "Adding secret: $SECRET"
        UPDATE_SECRETS+=(--update-secrets "${SECRET}=${SECRET}:latest")
    fi
done

gcloud run deploy "${IMAGE_NAME}" \
  --image "${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:${IMAGE_SHA}" \
  --region "${REGION}" \
  --set-env-vars "SPRING_PROFILES_ACTIVE=${PROFILE}" \
  --add-cloudsql-instances="${PROJECT_ID}:${REGION}:${CLOUD_SQL}" \
  --platform managed \
  --port 8080 \
  --memory 1Gi \
  --cpu 2 \
  --verbosity debug \
  "${UPDATE_SECRETS[@]}"
