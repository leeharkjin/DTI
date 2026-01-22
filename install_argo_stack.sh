#!/bin/bash
# Argo 전용 네임스페이스 생성
kubectl create namespace argocd
kubectl create namespace argo

# 1. Argo CD 설치
echo ">> Argo CD 설치 중..."
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# 2. Argo Workflows 설치 (Emissary Executor 사용 - 속도 및 안정성 최적화)
echo ">> Argo Workflows 설치 중..."
kubectl apply -n argo -f https://github.com/argoproj/argo-workflows/releases/latest/download/install.yaml

kubectl create clusterrolebinding argo-admin-binding \
  --clusterrole=admin \
  --serviceaccount=argo:default
  
# 3. Argo Workflows 설정 최적화 (하드웨어 성능 반영)
kubectl patch deployment workflow-controller -n argo --type json -p='[
  {"op": "replace", "path": "/spec/template/spec/containers/0/args", "value": [
    "--executor-image", "quay.io/argoproj/argoexec:latest",
    "--workflow-workers", "50"
  ]}
]'

echo "=== Argo Stack 설치 완료! ==="
echo "Argo CD 초기 비밀번호 확인: kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath='{.data.password}' | base64 -d"
