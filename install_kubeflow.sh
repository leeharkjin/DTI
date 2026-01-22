#!/bin/bash
# 1. 전제 조건: cert-manager 설치 (인증서 관리)
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml
sleep 30 # 인증서 파드가 준비될 때까지 대기

# 2. Kubeflow Manifests 클론 및 설치 (v1.8 기준)
git clone https://github.com/kubeflow/manifests.git
cd manifests

# 3. 한 번에 모든 컴포넌트 설치 (Dex, Istio, Knative, KFP, Katib 등)
# 주의: 이 과정은 약 10~20분 정도 소요됩니다.
while ! kustomize build example | kubectl apply -f -; do 
  echo "중간 오류 발생 시 재시도 중..."
  sleep 10
done

echo "=== Kubeflow 설치 프로세스가 시작되었습니다. 모든 파드가 Running이 될 때까지 기다려주세요. ==="
