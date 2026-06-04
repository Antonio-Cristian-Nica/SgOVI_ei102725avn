# SgOVI - Sistema de Gestió de l'Oficina Vida Independent

Aplicació web desenvolupada amb Spring Boot MVC, Thymeleaf i PostgreSQL per a la gestió de l'**Oficina Vida Independent (OVI) Sole Arnau Ripollés** de Castelló de la Plana. El sistema digitalitza la gestió de l'assistència personal a persones amb diversitat funcional, des de la sol·licitud inicial fins a la formalització del contracte.

---

## Accés a la base de dades

Servidor: `db-aules.uji.es`
Grup: `ei102725avn`
Contrasenya BBDD: `vivaMessi1010`

```bash
psql -h db-aules.uji.es -U ei102725avn ei102725avn
```

---

## Tecnologies utilitzades

- **Java 21**
- **Spring Boot 3.4.1** (Spring MVC, JdbcTemplate)
- **Thymeleaf 3.1** amb Layout Dialect
- **PostgreSQL**
- **Jasypt** per a l'encriptació de contrasenyes
- **CSS propi** centralitzat amb variables CSS

---

## Perfils del sistema

L'aplicació distingeix quatre perfils amb àrees d'accés diferenciades:

- **Públic** (sense sessió): pàgines informatives, registre i login.
- **Usuari OVI**: persones demandants d'assistència personal.
- **PAP/PATI**: personal d'assistència personal o terapèutica individual.
- **Tècnic OVI (administrador)**: back-office de gestió.

---

## Funcionalitats implementades

### 1. Gestió d'Usuaris OVI
- Registre amb validació completa (format de correu, telèfon, edat mínima, acceptació LOPD).
- Compte inicialment desactivat fins a la validació del tècnic.
- Contrasenyes encriptades amb Jasypt.
- Edició de dades personals i canvi de contrasenya.
- **Gestió opcional del tutor legal** (millora addicional): alta, edició i eliminació.

### 2. Gestió de candidats PAP/PATI
- Registre amb perfil professional (formació, experiència, especialització, documents).
- Compte inicialment desactivat fins a la validació del tècnic.
- Edició de dades personals i canvi de contrasenya.
- Gestió de la disponibilitat horària setmanal.
- Consulta de sol·licituds assignades i contractes propis.

### 3. Sol·licituds d'Assistència Personal
- **Dues modalitats** de sol·licitud:
  - **Flexible**: rang de dates; els detalls horaris s'acorden durant la negociació.
  - **Puntual** (rígida): dies i franges horàries concretes, com a millora addicional.
- Pantalla prèvia de selecció del tipus al crear una nova sol·licitud.
- Filtratge automàtic de PAP/PATIs compatibles per disponibilitat horària (sol·licituds rígides).
- **Exclusió automàtica de PAP/PATIs amb contractes actius solapats** (sol·licituds rígides).
- Negociació amb conversa bidireccional i confirmació explícita d'ambdues parts.
- Tancament automàtic de la resta de negociacions quan una arriba a acord.

### 4. Contractes
- Generació pel tècnic a partir d'una negociació amb acord.
- **Modificació posterior amb versionat**: el camp `version` s'incrementa cada vegada que s'edita.
- Finalització natural o cancel·lació, amb actualització coherent de l'estat de la sol·licitud associada.
- Consulta des dels portals d'Usuari OVI, PAP/PATI i tècnic.

---

## Comptes de prova

Totes les contrasenyes són **`patata`** excepte la de l'administrador (`1234`).

### Administrador

| Usuari | Contrasenya | Què permet demostrar |
|---|---|---|
| `admin0` | `1234` | Back-office complet: validació d'usuaris, gestió de sol·licituds, generació de contractes, etc. |

### Usuaris OVI

| Usuari | Contrasenya | Estat | Què permet demostrar |
|---|---|---|---|
| `maria.rodriguez` | `patata` | Actiu | Té **2 sol·licituds puntuals**: una pendent de gestionar pel tècnic i una altra que demostra l'**exclusió automàtica de PAP/PATIs amb contractes solapats**. Diversitat auditiva, sense tutor. |
| `pascual.beltran` | `patata` | Actiu | Sol·licitud puntual **acceptada amb 2 negociacions en curs** (amb Mireia i Empar). Demostra el flux de negociació en mitjà del procés. |
| `adelaida.marco` | `patata` | Actiu | Sol·licitud puntual amb **acord assolit pendent de generar contracte** (negociació `finished` amb Ferran). Demostra l'estat intermedi entre acord i contracte. |
| `sergi.orti` | `patata` | Actiu | Sol·licitud puntual amb **contracte actiu** (amb Núria). A més, té un **tutor legal associat** (Roser, la seua mare). Demostra el cicle complet fins a contracte + la millora del tutor legal. |
| `carles.fabregat` | `patata` | Actiu | Sol·licitud **flexible** pendent de gestionar. Demostra la diferència visual entre puntuals i flexibles i la **vista compacta del tècnic** per a flexibles. |
| `dani.villar` | `patata` | **Pendent d'aprovació** | Demostra la pàgina d'estat del compte quan encara no ha estat validat pel tècnic. |
| `willy.rex` | `patata` | **Rebutjat amb motiu** | Demostra el cas de compte rebutjat i la visualització del motiu en intentar iniciar sessió. |

### PAP/PATIs

#### Actius amb participació activa al sistema (apareixen en negociacions o contractes existents)

| Usuari | Contrasenya | Perfil professional · Localitat | Disponibilitat | Participació |
|---|---|---|---|---|
| `mireia.aparici` | `patata` | Psicòloga (LSE) · Vila-real | Dl/Dm/Dj vesprada (16–20) | Negociació activa amb Pascual |
| `empar.roig` | `patata` | Pedagoga terapèutica · Vall d'Uixó | Dl/Dc/Dv vesprada (17–20) | Negociació activa amb Pascual |
| `ferran.esteve` | `patata` | Educador social · Borriana | Dm/Dj matí (10–13) + Ds matí (09–14) | **Acord tancat amb Adelaida**, pendent de contracte |
| `nuria.castillo` | `patata` | Cuidadora generalista · Castelló capital | Dl–Dv matí (09–13) | **Contracte actiu amb Sergi** (Dc 8/7/2026 09:00–13:00) |

#### Actius disponibles per a recomanació en futures sol·licituds

| Usuari | Contrasenya | Perfil professional · Localitat | Disponibilitat |
|---|---|---|---|
| `lluis.sanchis` | `patata` | Fisioterapeuta neurològic · Castelló capital | Dl/Dc/Dv matí (09–14) |
| `jordi.tena` | `patata` | Enfermer · Almassora | Dl/Dc/Dv matí (08–12) + Ds matí (10–14) |
| `lourdes.pitarch` | `patata` | Intèrpret de LSE · Castelló capital | Ds matí (09–14) + Dg matí (10–14) |
| `vicent.solsona` | `patata` | Treballador social · Benicàssim | Dm/Dj vesprada-nit (18–22) + Dv vesprada-nit (19–23) |
| `laia.bellmunt` | `patata` | Logopeda · Nules | Dc i Dv jornada partida (09–13 + 16–19) |
| `salva.ortiz` | `patata` | Auxiliar de geriatria · Onda | Ds i Dg jornada partida (09–14 + 16–20) |
| `carmen.valles` | `patata` | Cuidadora · Castelló capital | Dl/Dc/Dv matí (10–14) |
| `joaquim.renau` | `patata` | Psicopedagog · Vila-real | Dl/Dm/Dc/Dj vesprada (17–20) |
| `roser.tomas` | `patata` | Fisioterapeuta esportiva · Almassora | Dm/Dj matí (09–13) + Ds matí (09–12) |

#### Comptes amb estats especials

| Usuari | Contrasenya | Estat | Què permet demostrar |
|---|---|---|---|
| `andres.iniesta` | `patata` | **Pendent d'aprovació** | Cas de candidat PAP/PATI pendent de validació pel tècnic. |
| `joan.garcia` | `patata` | **Rebutjat amb motiu** | Cas de candidat rebutjat i visualització del motiu. |

---

## Cobertura horària dels PAP/PATIs actius

| Franja | Cobertura |
|---|---|
| Matí entre setmana (Dl–Dv) | 7 PAP/PATIs |
| Vesprada entre setmana (Dl–Dv) | 3 PAP/PATIs |
| Vesprada-nit (>18h) | 1 PAP/PATI (Vicent) |
| Dissabte matí | 4 PAP/PATIs |
| Dissabte vesprada | 1 PAP/PATI (Salvador) |
| Diumenge matí | 2 PAP/PATIs (Lourdes, Salvador) |
| Diumenge vesprada | 1 PAP/PATI (Salvador) |
| Jornada partida en un mateix dia | 2 PAP/PATIs (Laia, Salvador) |

---

## Flux de prova recomanat per a la demostració

Recomanem seguir aquest ordre per veure totes les funcionalitats del sistema en acció:

### 1) Estructura general i àrea pública
- Accedir a `/` (sense sessió) per veure la pàgina d'inici, les notícies i l'estructura comuna del portal.
- Veure les opcions de registre (`/registro`).

### 2) Gestió de comptes pel tècnic OVI
Entrar com `admin0` / `1234`:
- **"Validar Usuaris OVI"** → veure Dani Villar pendent. Es pot activar o rebutjar amb motiu.
- **"Validar PAP/PATIs"** → veure Andrés Iniesta pendent. Es pot activar o rebutjar amb motiu.
- **"Gestionar Usuaris OVI"** → veure tots els actius i rebutjats. Entrar a la fitxa de Willy Rex per veure el motiu del rebuig.
- **"Gestionar PAP/PATIs"** → idem per als PAP/PATIs. Veure Joan García com a candidat rebutjat.

### 3) Estats especials dels comptes
- Entrar com `dani.villar` / `patata` → veure la pàgina informativa de compte pendent.
- Entrar com `willy.rex` / `patata` → veure la pàgina informativa de compte rebutjat amb el motiu.

### 4) Front-office: creació i seguiment de sol·licituds
Entrar com `maria.rodriguez` / `patata`:
- **"Les meues sol·licituds"** → veure les dues sol·licituds existents.
- **"Nova sol·licitud"** → veure la pantalla prèvia de selecció del tipus (puntual / flexible).
- Provar a crear una sol·licitud nova (puntual o flexible) per veure les diferències de flux.

Entrar com `carles.fabregat` / `patata`:
- Veure la sol·licitud flexible existent (rang de dates en lloc d'horaris).

### 5) Gestió de la sol·licitud pel tècnic + filtratge automàtic
Entrar com `admin0`:
- **"Sol·licituds d'assistència"** → veure totes les sol·licituds del sistema.
- Gestionar la **sol·licitud de María (dilluns 6/7/2026 09:30–11:30)**: veure els PAP/PATIs compatibles automàtics.
- Gestionar la **sol·licitud flexible de Carles**: veure la **vista compacta amb tots els PAP/PATIs actius** i el missatge informatiu corresponent.
- Gestionar la **segona sol·licitud de María (dimecres 8/7/2026 10:00–12:00)**: veure que **Núria NO apareix com a compatible** tot i que el seu horari encaixaria, perquè té un contracte actiu solapat amb Sergi.

### 6) Negociacions i contractes
Entrar com `pascual.beltran` / `patata`:
- Sol·licitud acceptada → entrar al detall → veure les dues negociacions en curs amb Mireia i Empar.
- Obrir qualsevol de les dues per veure el sistema de xat amb missatges.

Entrar com `adelaida.marco` / `patata`:
- Sol·licitud acceptada amb **acord assolit** (etiqueta "Acord assolit" al costat de Ferran).

Entrar com `sergi.orti` / `patata`:
- Sol·licitud amb **contracte actiu** (amb Núria). Es pot consultar el contracte des de la sol·licitud o des de **"Els meus contractes"**.
- Anar a **"El meu tutor legal"** per veure les dades de Roser Roca (la seua mare, com a millora del projecte).

### 7) Gestió del contracte pel tècnic
Entrar com `admin0`:
- **"Contractes"** → veure el contracte actiu entre Sergi i Núria.
- Provar a editar-lo per veure el **versionat** (cada modificació incrementa el `version`).
- Provar la finalització natural o la cancel·lació per veure com l'estat de la sol·licitud canvia coherentment.
- També es pot generar el contracte de la negociació pendent entre Adelaida i Ferran per demostrar el flux complet.

### 8) PAP/PATI consultant les seues sol·licituds i contractes
Entrar com `nuria.castillo` / `patata`:
- **"Les meues sol·licituds"** → veure la sol·licitud de Sergi amb estat "Contracte actiu".
- **"Els meus contractes"** → veure el contracte amb Sergi.

---

## Estats demostrables al sistema

| Estat | Quina entitat el té |
|---|---|
| Compte pendent (OviUser) | dani.villar |
| Compte rebutjat amb motiu (OviUser) | willy.rex |
| Compte pendent (PAP/PATI) | andres.iniesta |
| Compte rebutjat amb motiu (PAP/PATI) | joan.garcia |
| Sol·licitud puntual en revisió | María #1, María #2 |
| Sol·licitud flexible en revisió | Carles |
| Sol·licitud acceptada amb negociacions actives | Pascual |
| Sol·licitud acceptada amb acord pendent de contracte | Adelaida |
| Sol·licitud amb contracte actiu | Sergi |
| Negociació en curs | Pascual ↔ Mireia, Pascual ↔ Empar |
| Negociació finalitzada amb acord | Adelaida ↔ Ferran, Sergi ↔ Núria |
| Contracte actiu | Sergi ↔ Núria |
| Tutor legal associat | Sergi (tutor: Roser Roca) |

---

## Equip de desenvolupament

- **Edgar Adell Chabrera**
- **Antonio Cristian Nica Dida**
- **Daniel Villar Montón**

Grup: `ei102725avn` · Curs 2025/2026 · EI1027 — Disseny i Implementació de Sistemes d'Informació · Universitat Jaume I.
