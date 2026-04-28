Enlaces de interés:
- https://ovicastello.org/
- https://docs.google.com/forms/d/e/1FAIpQLSelYRAnQE9fS0C3xpDTZJlK2HaI8BtbXQ1x3g4GEI60EKAYrQ/viewform

## Instrucciones de acceso a la BBDD
Para el acceso a la base de datos de db-aules usamos el siguiente comando →  psql -h db-aules.uji.es -U ei102725avn ei102725avn

El nombre del grupo es →  ei102725avn

La contraseña para el acceso a la base de datos es → vivaMessi1010

# SgOVI - Sistema de Gestió de l'Oficina Vida Independent

Aplicació web desenvolupada amb Spring Boot MVC + Thymeleaf + PostgreSQL per a la gestió de l'Oficina Vida Independent de Castelló.

## Tecnologies utilitzades

- Java 21
- Spring Boot 3.4.1
- Thymeleaf 3.1 + Layout Dialect
- PostgreSQL
- JASYPT (encriptació de contrasenyes)
- Bootstrap / CSS propi

## Comptes de prova

| Rol | Usuari | Contrasenya | Estat |
|-----|--------|-------------|-------|
| Administrador | admin0 | 1234 | Actiu |
| OVI User (amb contracte) | juan.perez | 1234 | Actiu |
| OVI User (negociant) | maria.rod | 1234 | Actiu |
| OVI User (sol·licitud en revisió) | luis.gomez | 1234 | Actiu |
| OVI User (pendent activació) | sara.leon | 1234 | Pendent |
| OVI User (rebutjat) | pedro.martin | 1234 | Rebutjat |
| PAP/PATI (amb contracte) | ana.garcia | 1234 | Actiu |
| PAP/PATI (negociant) | carlos.ruiz | 1234 | Actiu |
| PAP/PATI (recomanat sense negociació) | elena.b | 1234 | Actiu |
| PAP/PATI (sense horaris) | marcos.sanz | 1234 | Actiu |
| PAP/PATI (pendent activació) | lucia.mendez | 1234 | Pendent |
| PAP/PATI (rebutjat) | roberto.f | 1234 | Rebutjat |

## Funcionalitats implementades

### 1. Gestió d'OVI Users
- Registre mitjançant formulari amb validació completa: format d'email, telèfon, edat mínima de 3 anys i acceptació LOPD
- Les credencials es guarden amb el compte desactivat (`activated = false`) i estat `approvalPending` fins que el tècnic la valide
- Les contrasenyes s'encripten amb JASYPT abans de guardar-se a la BD
- El tècnic OVI pot veure el llistat d'usuaris pendents i activar el seu compte des del seu portal
- Una vegada activat, l'usuari accedeix al seu portal complet. Si intenta accedir abans de ser validat, veu una pàgina informativa amb l'estat del seu compte i el motiu de rebuig si escau
- Els usuaris poden modificar les seues dades personals i canviar la contrasenya des del seu portal
- Gestió del tutor legal: l'OVI User pot afegir, editar i eliminar el seu tutor legal

### 2. Gestió de candidats PAP/PATI
- Registre mitjançant formulari amb validació completa: formació acadèmica, experiència professional, àrees d'especialització, documents adjunts, edat mínima de 18 anys i acceptació LOPD
- Al igual que els OVI Users, les credencials es guarden desactivades fins a la validació del tècnic
- Les contrasenyes s'encripten amb JASYPT
- El tècnic OVI pot veure el llistat de PAP/PATIs pendents i activar el seu compte
- Una vegada activat, el PAP/PATI accedeix al seu portal complet
- Els PAP/PATIs poden modificar les seues dades personals i canviar la contrasenya
- Gestió d'horaris de disponibilitat: el PAP/PATI pot afegir i eliminar franges horàries per dia de la setmana. Si no té horaris registrats, apareix una alerta al portal indicant-ho
- Consulta de sol·licituds assignades i accés a les negociacions actives
- Consulta de l'historial de contractes

### 3. Sol·licituds d'Assistència Personal
- Els OVI Users poden crear sol·licituds d'assistència indicant localització, tipus d'assistència i horaris específics
- Les sol·licituds sense horaris no es poden finalitzar
- Els horaris han de ser dates futures
- El tècnic OVI gestiona totes les sol·licituds des del seu portal:
  - Veu la informació completa de la persona sol·licitant i els horaris
  - El sistema selecciona automàticament els PAP/PATIs compatibles segons disponibilitat horària
  - El tècnic afegeix PAP/PATIs recomanats i accepta o rebutja la sol·licitud
- Una vegada acceptada, l'OVI User veu els PAP/PATIs recomanats i pot iniciar negociacions
- Les dues parts (OVI User i PAP/PATI) poden intercanviar missatges dins de la negociació
- Quan ambdues parts confirmen l'acord, la negociació es tanca com a `finished` i les altres negociacions actives de la mateixa sol·licitud es tanquen automàticament com a `noAgreement`
- El tècnic genera el contracte a partir de la negociació finalitzada, indicant dates d'inici i fi del servei i l'URL del document PDF
- Els OVI Users i PAP/PATIs poden consultar els seus contractes des del seu portal

### 4. Activitats de Formació i Divulgació
- En construcció

### 5. Gestió d'Instructors
- En construcció

## Casos de prova disponibles (psswd = 1234)

### OVI Users
- **juan.perez**: té una sol·licitud tancada amb contracte actiu amb Ana García
- **maria.rod**: té una sol·licitud acceptada amb dues negociacions simultànies en curs (Ana García i Carlos Ruiz)
- **luis.gomez**: té una sol·licitud en revisió sense recomanats
- **sara.leon**: compte pendent d'activació (per provar pàgina pending)
- **pedro.martin**: compte rebutjat amb motiu (per provar pàgina pending amb rebuig)

### PAP/PATIs
- **ana.garcia**: té horaris, un contracte actiu amb Juan i una negociació en curs amb María
- **carlos.ruiz**: té horaris i una negociació en curs amb María
- **elena.b**: té horaris i és recomanat per a una sol·licitud sense negociació iniciada
- **marcos.sanz**: té el compte actiu però NO té horaris (per provar l'alerta al portal)
- **lucia.mendez**: compte pendent d'activació
- **roberto.f**: compte rebutjat amb motiu

### Activitats
- **Taller Autonomia Personal** (formació): quasi plena, 3 inscrits de 5 places
- **Xerrada Drets Socials** (divulgació): àmplia capacitat, 2 inscrits
- **Curs Llengua de Signes** (formació): buida, per probar el procés d'inscripció complet

## ------------------------

4. Actividades de formación y divulgación --> El técnico OVI será el encargado de gestionar la creación de las distintas actividades. Esta gestión incluirá la creación de las actividades, la asignación de los instructores que impartan la actividad. Habrán dos tipos de actividades: actividades de formación con un número limitado de participantes, y luego actividades de divulgación en las que no se requerirá inscripción, pero también estará disponible. En el caso de las actividades de formación, la aplicación deberá proporcionar un proceso de inscripción a esa actividades con los datos personales de la persona que participe. Y una vez acabe la formación, el instructor definido deberá poder registrar la asistencia de cada participante a la actividad, para que luego la aplicación pueda emitir a esos asistentes un certificado de asistencia en formato PDF. En las actividades de divulgación, el aforo se controlará in situ, será el instructor el que compruebe la asistencia de los participantes inscritos y también el que registrará el nombre de las personas participantes, la aplicación debe permitir esto.

## Diseño del SiteMap
A continuación se muestra el SiteMap del proyecto que sirve como guia para el flujo y diseño de la experiencia de usuario en nuestro sistema de información:

![Sitemap del Proyecto](./documentacion/SitemapSgOVI.png)

A CORREGIR --> Está bien, pero falta funcionalidad.
P.e. el PAP/PATI dónde puede ver el contrato, el OVIuser dónde puede enviar un mensaje, o registrar un contrato...
Del técnico OVI, lo mismo: dónde acepta/rechaza la petición de un candidato a PAP/PATI, dónde acepta una solicitud de asistencia personal...

## Diseño Conceptual (Diagrama UML)
A continuación se muestra el diagrama de clases UML que sirve como punto de partida para el diseño de nuestro sistema de información:

![Diagrama UML del Proyecto](./documentacion/DIAGRAMA_DE_CLASES.png)

### Cambios realizados sobre el diseño
- Cambios sobre atributos
- Contract ahora está relacionada únicamente con Negotiation
- Assistance Request puede tener 1 o muchos Resquest Schedule, igual que PAP/PATI con SCHEDULE



