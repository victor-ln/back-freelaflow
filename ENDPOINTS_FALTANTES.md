# Endpoints Faltantes no Backend

## Services
- ❌ `GET /api/v1/services/{id}` - Buscar serviço por ID
- ❌ `PUT /api/v1/services/{id}` - Atualizar serviço (completo)
- ❌ `PATCH /api/v1/services/{id}/status` - Alterar status do serviço

## Categories
- ❌ `PUT /api/v1/categories/{id}` - Atualizar categoria (completo) - Tem PATCH
- ❌ `PATCH /api/v1/categories/{id}/status` - Alterar status da categoria

## Roles
- ❌ `PUT /api/v1/roles/{id}` - Atualizar role (completo) - Tem PATCH
- ❌ `PATCH /api/v1/roles/{id}/status` - Alterar status da role
- ❌ `POST /api/v1/roles/assign` - Atribuir roles a freelancer
- ❌ `DELETE /api/v1/roles/freelancer/{freelancerId}/role/{roleId}` - Remover role de freelancer
- ❌ `GET /api/v1/roles/freelancer/{freelancerId}` - Listar roles do freelancer

## Clients
- ❌ `PUT /api/v1/clients/{id}` - Atualizar cliente (completo) - Tem PATCH

## Templates
- ❌ `PATCH /api/v1/templates/{id}/approve` - Aprovar template
- ❌ `GET /api/v1/templates/{id}/download` - Download template

## Kanbans - Tasks
- ❌ `PATCH /api/v1/kanbans/{kanbanId}/deactivate` - Desativar kanban
- ❌ `PATCH /api/v1/kanbans/{kanbanId}/tasks/{taskId}/block` - Bloquear task
- ❌ `PATCH /api/v1/kanbans/{kanbanId}/tasks/{taskId}/unblock` - Desbloquear task
- ❌ `PATCH /api/v1/kanbans/{kanbanId}/tasks/{taskId}/complete` - Completar task
- ❌ `PATCH /api/v1/kanbans/{kanbanId}/tasks/{taskId}/add-time` - Adicionar tempo à task
- ❌ `GET /api/v1/kanbans/{id}/metrics` - Métricas do kanban
- ❌ `GET /api/v1/kanbans/{id}/progress` - Progresso das tasks
- ❌ `POST /api/v1/kanbans/{kanbanId}/tasks/bulk` - Criar tasks em lote
- ❌ `PATCH /api/v1/kanbans/{kanbanId}/tasks/bulk-order` - Atualizar ordem em lote
- ❌ `POST /api/v1/kanbans/from-template` - Criar kanban de template
- ❌ `POST /api/v1/kanbans/{id}/duplicate` - Duplicar kanban
- ❌ `GET /api/v1/kanbans/{id}/export` - Exportar dados do kanban
- ❌ `GET /api/v1/kanbans/{kanbanId}/time-tracking` - Rastreamento de tempo
- ❌ `GET /api/v1/kanbans/{kanbanId}/productivity` - Métricas de produtividade

## Proposals
- ❌ `GET /api/v1/proposals/{id}/contract/download` - Download contrato
- ❌ `POST /api/v1/proposals/{id}/upload-edited-contract` - Upload contrato editado
- ❌ `POST /api/v1/proposals/{id}/send-email` - Enviar email do contrato
- ❌ `POST /api/v1/proposals/{id}/attach-signed-contract` - Anexar contrato assinado

## Prioridade de Implementação
1. **Alta**: Services (GET/{id}, PUT/{id}, PATCH/status)
2. **Alta**: Categories, Roles (PATCH/status)
3. **Média**: Clients (PUT)
4. **Média**: Templates (approve, download)
5. **Baixa**: Kanbans (métricas e operações avançadas)
6. **Baixa**: Proposals (operações de contrato)
