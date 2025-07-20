#!/bin/bash
set -euo pipefail

PROJECT_ID=$1
REGION=$2
REPO_NAME=$3
IMAGE_NAME=$4
CLOUD_SQL=$5
PROFILE=$6
IMAGE_SHA=$7

echo "Project ID: $PROJECT_ID"
echo "Region: $REGION"
echo "Repo Name: $REPO_NAME"
echo "Image Name: $IMAGE_NAME"
echo "PROFILE: $PROFILE"
echo "Image SHA: $IMAGE_SHA"

mapfile -t SECRETS < <(gcloud secrets list --project=$PROJECT_ID --format='value(name)')

echo "== Secrets to bind =="
printf '%s\n' "${SECRETS[@]}"

UPDATE_SECRETS=()
for SECRET in "${SECRETS[@]}"; do
    SECRET=$(echo "$SECRET" | xargs)  # 공백 제거 (trim)
    if [ -n "$SECRET" ]; then
        echo "Adding secret: $SECRET"
        UPDATE_SECRETS+=(--update-secrets "${SECRET}=${SECRET}:latest")
    fi
done

echo "== Final UPDATE_SECRETS =="
printf '%s\n' "${UPDATE_SECRETS[@]}"

echo "== Deploy Command =="

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

gcloud run services describe ${IMAGE_NAME} \
  --region asia-northeast3 \
  --format="yaml(spec.template.spec.volumes)"