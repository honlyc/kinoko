#!/bin/bash
# ============================================================================
# 脚本热更新编译工具
# 用法: ./compile-scripts.sh [脚本文件路径...]
#
# 示例:
#   编译所有脚本:
#     ./compile-scripts.sh
#
#   编译单个脚本:
#     ./compile-scripts.sh src/main/java/kinoko/script/Consume.java
#
#   编译多个脚本:
#     ./compile-scripts.sh src/main/java/kinoko/script/Consume.java src/main/java/kinoko/script/FreeMarket.java
#
# 编译后的 .class 文件会输出到 scripts/ 目录，保持包结构。
# 然后在游戏内使用 @reloadscripts 或 @rs 命令即可热更新。
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
OUTPUT_DIR="$PROJECT_DIR/scripts"
SOURCE_DIR="$PROJECT_DIR/src/main/java"

# 构建 classpath：target/classes + Maven 依赖
CLASSPATH="$PROJECT_DIR/target/classes"

# 尝试使用 Maven 获取完整的依赖 classpath
MAVEN_CP_FILE="$PROJECT_DIR/target/.maven-classpath"
if [ ! -f "$MAVEN_CP_FILE" ] || [ "$1" = "--refresh-cp" ]; then
    echo "正在通过 Maven 解析依赖 classpath（首次运行或使用 --refresh-cp）..."
    if [ "$1" = "--refresh-cp" ]; then shift; fi
    mvn -q -f "$PROJECT_DIR/pom.xml" dependency:build-classpath \
        -Dmdep.outputFile="$MAVEN_CP_FILE" 2>/dev/null
    if [ $? -ne 0 ]; then
        echo "⚠️  Maven 解析依赖失败，尝试仅使用 target/classes 编译..."
    fi
fi

if [ -f "$MAVEN_CP_FILE" ]; then
    MAVEN_CP=$(cat "$MAVEN_CP_FILE")
    CLASSPATH="$CLASSPATH:$MAVEN_CP"
fi

# 确保输出目录存在
mkdir -p "$OUTPUT_DIR"

if [ $# -eq 0 ]; then
    echo "编译 kinoko.script 包下所有脚本文件..."
    # 查找所有脚本文件（排除 common 包）
    SCRIPT_FILES=$(find "$SOURCE_DIR/kinoko/script" -name "*.java" \
        ! -path "*/common/*" \
        2>/dev/null)
else
    SCRIPT_FILES="$@"
fi

if [ -z "$SCRIPT_FILES" ]; then
    echo "未找到需要编译的脚本文件"
    exit 1
fi

echo "输出目录: $OUTPUT_DIR"
echo ""

# 编译
echo "正在编译..."
javac -cp "$CLASSPATH" \
      -sourcepath "$SOURCE_DIR" \
      -d "$OUTPUT_DIR" \
      --release 21 \
      $SCRIPT_FILES

if [ $? -eq 0 ]; then
    COMPILED_COUNT=$(find "$OUTPUT_DIR" -name "*.class" | wc -l | tr -d ' ')
    echo ""
    echo "✅ 编译成功！共 $COMPILED_COUNT 个 class 文件"
    echo ""
    echo "请在游戏内执行 @reloadscripts 或 @rs 命令来加载更新的脚本。"
else
    echo ""
    echo "❌ 编译失败，请检查错误信息。"
    exit 1
fi
