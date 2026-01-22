#!/bin/bash
# GPU가 있는 노드에 자동으로 라벨 부여 (Argo/Kubeflow가 똑똑하게 배포되도록)
for node in $(kubectl get nodes -no-headers | awk '{print $1}'); do
    if kubectl describe node $node | grep -i "nvidia.com/gpu" > /dev/null; then
        kubectl label node $node accelerator=nvidia-gpu
        echo "Node $node labeled as GPU node"
    fi
done
