PROJECT_ID=$1
REGION=$2
REPO_NAME=$3
IMAGE_NAME=$4
IMAGE_SHA=$5

mapfile -t SECRETS < <(gcloud secrets list --project=$PROJECT_ID --format='value(name)')

UPDATE_SECRETS=()
for SECRET in "${SECRETS[@]}"; do
    echo "Adding secret: $SECRET"
    UPDATE_SECRETS+=(--update-secrets "${SECRET}=projects/${PROJECT_ID}/secrets/${SECRET}/versions/latest")
done

gcloud run deploy "${IMAGE_NAME}" \
  --image "${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:${IMAGE_SHA}" \
  --region "${REGION}" \
  --platform managed \
  --memory 1Gi \
  --cpu 2 \
  "${UPDATE_SECRETS[@]}"