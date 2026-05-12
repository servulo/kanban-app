#!/bin/bash
echo "[kanban-db-init] Aguardando SQL Server..."
for i in $(seq 1 30); do
  /opt/mssql-tools18/bin/sqlcmd -S host.docker.internal,1433 -U sa -P "SprjAuth@2025" -No -Q "SELECT 1" 2>/dev/null && break
  echo "[kanban-db-init] Tentativa $i/30..."
  sleep 2
done

echo "[kanban-db-init] Criando banco sprj_kanban (se não existir)..."
/opt/mssql-tools18/bin/sqlcmd -S host.docker.internal,1433 -U sa -P "SprjAuth@2025" -No \
  -Q "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'sprj_kanban') CREATE DATABASE sprj_kanban;"

echo "[kanban-db-init] Concluído."
