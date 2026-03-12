# SgOVI_ei102725avn
SgOVI - Sistema de Gestión de Oficina de Vida Independiente. Proyecto académico diseñado para administrar usuarios con diversidad funcional, gestionar el catálogo de actividades sociales y coordinar a los profesionales de asistencia personal (PAP/PATI).

## Instrucciones de acceso a la BBDD
Para el acceso a la base de datos de db-aules usamos el siguiente comando →  psql -h db-aules.uji.es -U ei102725avn ei102725avn

El nombre del grupo es →  ei102725avn

La contraseña para el acceso a la base de datos es → vivaMessi1010

Con ello, es posible acceder a la base de datos de forma exitosa :)

## Diseño Conceptual (Diagrama UML)
A continuación se muestra el diagrama de clases UML que sirve como punto de partida para el diseño de nuestro sistema de información:

![Diagrama UML del Proyecto](./documentacion/DIAGRAMA_DE_CLASES.png)

### Cambios realizados sobre el diseño
- Cambios sobre atributos
- Contract ahora está relacionada únicamente con Negotiation
- Assistance Request puede tener 1 o muchos Resquest Schedule, igual que PAP/PATI con SCHEDULE

## Diseño del SiteMap
A continuación se muestra el SiteMap del proyecto que sirve como guia para el flujo y diseño de la experiencia de usuario en nuestro sistema de información:

![Sitemap del Proyecto](./documentacion/SitemapSgOVI.png)
