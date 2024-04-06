#!/bin/bash

# Docker Hub 用户名和密码
DOCKER_USERNAME="nobugboy"
DOCKER_PASSWORD="********"

# 登录 Docker Hub
echo "Logging in to Docker Hub..."
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

# 检查登录是否成功
if [ $? -eq 0 ]; then
    echo "Login successful"

    # 要推送的本地镜像名称和标签
    LOCAL_IMAGE="nobugboy/neo-chat"
    TAG="latest"
    REMOTE_REPO="nobugboy/neo-chat"

    # 标记本地镜像
    docker tag $LOCAL_IMAGE:$TAG $REMOTE_REPO:$TAG

    # 推送镜像到远程仓库
    echo "Pushing image to remote repository..."
    docker push $REMOTE_REPO:$TAG

    # 检查推送是否成功
    if [ $? -eq 0 ]; then
        echo "Image pushed successfully to remote repository"
    else
        echo "Failed to push image to remote repository"
        exit 1
    fi

else
    echo "Login failed"
    exit 1
fi