# Sistema de Senhas de Atendimento

Simulação de um sistema de senhas de atendimento em **Java puro**, com fila prioritária (regra **2N:1P**), pilha de senhas atendidas e postos de atendimento que ligam e desligam. As estruturas de dados (fila e pilha) foram implementadas do zero, sem coleções do Java.

O projeto tem duas saídas para a mesma lógica:
- **Console:** a simulação roda inteira no terminal.
- **Web:** um painel em Vue 3 + Tailwind, servido por um servidor HTTP nativo do Java (sem Spring e sem bibliotecas externas).

---

## Como rodar

Na pasta do projeto (onde ficam `src/` e `index.html`):

```bash
javac -d out src/*.java
```

**Modo console**
```bash
java -cp out App        # pausa de 800 ms entre rodadas
java -cp out App 0      # sem pausa
```

**Modo web**
```bash
java -cp out Servidor
```
Depois abra **http://localhost:8080** no navegador.

> Rode o `Servidor` a partir da pasta do projeto, para ele encontrar o `index.html`, o `estilo.css` e o `app.js`.

---

## Estrutura

```
App ──────────┐
              ├──> Simulacao
Servidor ─────┘      ├── FilaPrioritaria ── 2 x Fila<Senha>   (uma de N, uma de P)
   │                 ├── GerenciadorPostos ── 5 x PostoAtendimento
   │                 └── Pilha<Senha>                         (senhas atendidas)
   │
   └── index.html + estilo.css + app.js   (painel web)

Fila / Pilha ── usam Node<E>
Senha ── usa TipoSenha (N ou P)
```

| Classe | O que faz |
|---|---|
| `Node<E>` | Nó com o elemento e a referência para o próximo (`next`). |
| `Fila<E>` | FIFO: `enfileirar` no fim e `desenfileirar` do início, em O(1). Tem `remover` para desistências. |
| `Pilha<E>` | LIFO: `empilhar` e `desempilhar` no topo, em O(1). `paraVetor()` lê sem desempilhar. |
| `TipoSenha` | Enum com os dois tipos: `NORMAL` (N) e `PRIORITARIA` (P). |
| `Senha` | Tipo + número. Exibida como `N007`, `P003`. |
| `FilaPrioritaria` | Duas filas e um contador. `chamar()` aplica a regra 2N:1P; `previsao()` mostra as próximas sem tirar ninguém. |
| `PostoAtendimento` | Um guichê: ativo/inativo, livre/ocupado e tempo restante de atendimento. |
| `GerenciadorPostos` | Os 5 postos: 1 a 3 fixos, 4 e 5 podem ser ativados e desativados. |
| `Simulacao` | O "relógio": executa as rodadas, faz os sorteios, monta o painel e o estado em JSON. |
| `App` | Entrada do modo console. |
| `Servidor` | Entrada do modo web, com `com.sun.net.httpserver.HttpServer`. |

---

## Regras implementadas

- **Tipos de senha:** normal (N) e prioritária (P).
- **Regra 2N:1P:** a cada 2 senhas normais chamadas, 1 prioritária. Se uma das filas estiver vazia, a outra é chamada normalmente. Se houver P esperando no início, ela é a primeira.
- **Postos:** até 5. Os postos 1, 2 e 3 estão sempre ativos; o 4 e o 5 ligam e desligam durante a simulação. Um posto só é desligado se estiver livre.
- **Senhas atendidas:** empilhadas no momento em que são chamadas a um posto, mantendo a ordem de atendimento.
- **Desistência:** a cada rodada há uma chance de um cliente que está esperando ir embora. Ele é removido da fila.
- **Encerramento:** após 20 rodadas param as chegadas; a simulação termina quando a fila e os postos esvaziam. As senhas atendidas são exibidas **da última para a primeira**.

### Como o tempo funciona
Cada **rodada** representa uma unidade de tempo (pense em 1 minuto). Em cada rodada:
1. os postos descontam 1 do tempo de atendimento e liberam quem terminou;
2. chegam de 0 a 3 clientes (30% de chance de cada um ser prioritário);
3. pode haver uma desistência (15% de chance);
4. o posto 4 ou 5 pode ligar ou desligar (10% de chance);
5. os postos livres chamam as próximas senhas (cada atendimento dura de 2 a 6 rodadas);
6. o painel é exibido.

Esses valores são constantes no topo da classe `Simulacao` e podem ser ajustados.

---

## Painel exibido a cada rodada

- Número total de pessoas na fila (com quantas N e quantas P)
- As próximas duas senhas a serem chamadas
- Para qual posto a próxima senha deve se dirigir
- Status de cada posto (Livre / Ocupado / Inativo)
- Número de senhas atendidas e de desistências

---

## Painel web

| Rota | O que faz |
|---|---|
| `GET /` | Página do painel (`index.html`, `estilo.css`, `app.js`) |
| `GET /estado` | Estado atual da simulação em JSON |
| `POST /rodada` | Executa uma rodada |
| `POST /postos/{n}/alternar` | Liga ou desliga o posto 4 ou 5 |
| `POST /reiniciar` | Começa uma simulação nova |

Na página: **Próxima rodada**, **Play/Pause** (uma rodada por segundo), **Reiniciar** e ligar/desligar os postos 4 e 5. Ao final, a grade com as senhas atendidas da última para a primeira.

O front usa **Vue 3** e **Tailwind CSS** via CDN, sem npm e sem build. O JSON é montado à mão no Java, sem bibliotecas.

---

## Tecnologias
- Java (sem frameworks e sem bibliotecas externas)
- Vue 3 e Tailwind CSS (via CDN)
