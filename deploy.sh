#!/bin/bash
# GitHub Pages 部署脚本

echo "开始部署学生选课管理系统到 GitHub Pages..."

# 检查是否已安装 Git
if ! command -v git &> /dev/null; then
    echo "错误: 未找到 Git，请先安装 Git"
    exit 1
fi

echo "1. 检查 Git 仓库状态..."
if [ -d ".git" ]; then
    echo "本地 Git 仓库已存在"
else
    echo "初始化 Git 仓库..."
    git init
    if [ $? -ne 0 ]; then
        echo "无法初始化 Git 仓库"
        exit 1
    fi
fi

echo "2. 添加所有文件到暂存区..."
git add .

echo "3. 检查远程仓库配置..."
REMOTE_URL=$(git remote get-url origin 2>/dev/null)
if [ "$REMOTE_URL" = "https://github.com/LIAMLDH/LIAMLDH.github.io.git" ]; then
    echo "远程仓库配置正确"
else
    echo "配置远程仓库..."
    git remote set-url origin https://github.com/LIAMLDH/LIAMLDH.github.io.git
fi

echo "4. 设置主分支名称..."
git branch -M main

echo "5. 尝试推送代码..."
echo "注意: 您可能需要输入 GitHub 用户名和密码(个人访问令牌)"
echo "如果这是第一次推送，请确保您已在 GitHub 上创建了 LIAMLDH.github.io 仓库"

git push -u origin main

if [ $? -eq 0 ]; then
    echo ""
    echo "==================================="
    echo "✅ 代码推送成功!"
    echo ""
    echo "接下来请执行以下步骤:"
    echo "1. 访问 https://github.com/LIAMLDH/LIAMLDH.github.io"
    echo "2. 转到 Settings > Pages"
    echo "3. 在 Source 部分选择 'Deploy from a branch'"
    echo "4. 选择 'gh-pages' 分支和 '/ (root)' 文件夹"
    echo "5. 点击 'Save'"
    echo ""
    echo "部署完成后，您的网站将在 https://LIAMLDH.github.io 访问"
    echo "==================================="
else
    echo ""
    echo "❌ 推送失败，请检查错误信息"
    echo ""
    echo "可能的原因及解决方案:"
    echo "- 确保您已在 GitHub 上创建了名为 'LIAMLDH.github.io' 的仓库"
    echo "- 确保您有对该仓库的写入权限"
    echo "- 检查您的 GitHub 凭据是否正确"
    echo "- 如果使用个人访问令牌，请确保令牌具有足够的权限"
    echo ""
fi