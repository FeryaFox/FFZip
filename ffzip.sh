
#!/bin/bash

# Путь до JAR-файла
JAR_PATH="/путь/до/jar"

# Обработка аргументов командной строки
while [[ $# -gt 0 ]]; do
  case "$1" in
    -c)
      MODE="c"
      shift
      ;;
    -bs)
      BUFFER_SIZE="$2"
      shift 2
      ;;
    -ds)
      DICTIONARY_SIZE="$2"
      shift 2
      ;;
    -d)
      MODE="d"
      shift
      ;;
    *)
      if [[ -z "$INPUT_FILE" ]]; then
        INPUT_FILE="$1"
      else
        OUTPUT_FILE="$1"
      fi
      shift
      ;;
  esac
done

# Проверка наличия обязательных аргументов
if [[ -z "$INPUT_FILE" ]] || [[ -z "$OUTPUT_FILE" ]]; then
  echo "Ошибка: Не указаны входной и/или выходной файлы."
  exit 1
fi

# Формирование команды Java
JAVA_CMD="java -jar \"$JAR_PATH\""

# Добавление аргументов в зависимости от режима
if [[ "$MODE" == "c" ]]; then
  JAVA_CMD="$JAVA_CMD -c"
  [[ -n "$BUFFER_SIZE" ]] && JAVA_CMD="$JAVA_CMD -bs $BUFFER_SIZE"
  [[ -n "$DICTIONARY_SIZE" ]] && JAVA_CMD="$JAVA_CMD -ds $DICTIONARY_SIZE"
elif [[ "$MODE" == "d" ]]; then
  JAVA_CMD="$JAVA_CMD -d"
fi

# Добавление входного и выходного файлов
JAVA_CMD="$JAVA_CMD \"$INPUT_FILE\" \"$OUTPUT_FILE\""

# Запуск команды
eval "$JAVA_CMD"
