#!/bin/bash

DIR="/content/resources"

LIMIT=15

get_folder_size_gb() {
    du -s "$DIR" | awk '{print $1/1024/1024}'  # du возвращает размер в Кб, переводим в Гб
}

while true; do
    FOLDER_SIZE=$(get_folder_size_gb)
    
    if (( $(echo "$FOLDER_SIZE >= $LIMIT" | bc -l) )); then
        echo "Размер папки $FOLDER_SIZE ГБ. Превышен лимит в $LIMIT ГБ."
        echo "Удаляем все файлы в папке $DIR..."
        
        rm -rf "$DIR"/*
        
        echo "Все файлы удалены."
    else
        echo "Размер папки $FOLDER_SIZE ГБ. Лимит не превышен."
    fi
    
    sleep 60
done
