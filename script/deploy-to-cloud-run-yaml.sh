#!/bin/bash
set -euo pipefail

PROJECT_ID=$1
REGION=$2
CLOUD_RUN_REGION=$3
REPO_NAME=$4
IMAGE_NAME=$5
CLOUD_SQL_INSTANCE=$6
PROFILE=$7
IMAGE_SHA=$8
SERVICE_ACCOUNT_EMAIL=${9:-"${PROJECT_ID}@appspot.gserviceaccount.com"}

echo "============================================"
echo "Cloud Run YAML-based Deployment Starting..."
echo "============================================"
echo "Project ID: ${PROJECT_ID}"
echo "Region: ${REGION}"
echo "Cloud Run Region: ${CLOUD_RUN_REGION}"
echo "Image: ${IMAGE_NAME}:${IMAGE_SHA}"
echo "Profile: ${PROFILE}"
echo "============================================"

# Fetch all secrets from Google Cloud Secret Manager
echo "Fetching secrets from Google Cloud Secret Manager..."
mapfile -t SECRETS < <(gcloud secrets list --project="${PROJECT_ID}" --format='value(name)')

# Build secrets environment variables for YAML
SECRETS_ENV=""
for SECRET in "${SECRETS[@]}"; do
    SECRET=$(echo "$SECRET" | xargs)  # Trim whitespace
    if [ -n "$SECRET" ]; then
        echo "Adding secret: $SECRET"
        # Add each secret as an environment variable reference
        SECRETS_ENV+="        - name: ${SECRET}"$'\n'
        SECRETS_ENV+="          valueFrom:"$'\n'
        SECRETS_ENV+="            secretKeyRef:"$'\n'
        SECRETS_ENV+="              name: ${SECRET}"$'\n'
        SECRETS_ENV+="              key: latest"$'\n'
    fi
done

# Export variables for envsubst
export PROJECT_ID
export REGION
export CLOUD_RUN_REGION
export REPO_NAME
export IMAGE_NAME
export CLOUD_SQL_INSTANCE
export PROFILE
export IMAGE_SHA
export SERVICE_ACCOUNT_EMAIL
export SECRETS_ENV

# Generate the final service YAML from template
TEMPLATE_PATH="infra/cloudrun/service-prod.yaml"
OUTPUT_PATH="/tmp/cloud-run-service-${IMAGE_SHA}.yaml"

echo ""
echo "Generating Cloud Run service YAML from template..."
envsubst < "${TEMPLATE_PATH}" > "${OUTPUT_PATH}"

echo "Generated YAML saved to: ${OUTPUT_PATH}"
echo ""
echo "============================================"
echo "YAML Content Preview:"
echo "============================================"
head -n 30 "${OUTPUT_PATH}"
echo "..."
echo "============================================"

# Deploy to Cloud Run using YAML
echo ""
echo "Deploying to Cloud Run using YAML configuration..."
gcloud run services replace "${OUTPUT_PATH}" \
  --region="${CLOUD_RUN_REGION}" \
  --platform=managed \
  --verbosity=debug

echo ""
echo "============================================"
echo "Deployment completed successfully!"
echo "============================================"

# Clean up temporary file
rm -f "${OUTPUT_PATH}"
