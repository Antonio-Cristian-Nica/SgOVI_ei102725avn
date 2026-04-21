# SgOVI_ei102725avn
SgOVI - Sistema de Gestión de Oficina de Vida Independiente. Proyecto académico diseñado para administrar usuarios con diversidad funcional, gestionar el catálogo de actividades sociales y coordinar a los profesionales de asistencia personal (PAP/PATI).

Enlaces de interés:
- https://ovicastello.org/
- https://docs.google.com/forms/d/e/1FAIpQLSelYRAnQE9fS0C3xpDTZJlK2HaI8BtbXQ1x3g4GEI60EKAYrQ/viewform


## Instrucciones de acceso a la BBDD
Para el acceso a la base de datos de db-aules usamos el siguiente comando →  psql -h db-aules.uji.es -U ei102725avn ei102725avn

El nombre del grupo es →  ei102725avn

La contraseña para el acceso a la base de datos es → vivaMessi1010

Cuentas ya activadas desde el diseño físico:

      - Administrador --> admin0 (1234)
      - Usuario OVI --> juan.perez (patata)
      - PAP/PATI --> ana.garcia (patata)


## Lo que debe hacer la aplicación
1. Gestión de Ovi Users --> El sistema posibilita el alta y la modificación de datos personales y de contacto de cada OVI User, incluyendo el registro explícito del consentimiento informado según la normativa LOPD/RGPD.
Implementado:
Registro de OVI Users mediante formulario con validación completa de todos los campos, incluyendo formato de email, teléfono, edad mínima de 3 años y aceptación LOPD.
Las credenciales se guardan en la tabla CREDENTIALS con la cuenta desactivada (activated = false) y estado approvalPending hasta que el técnico la valide.
Las contraseñas se encriptan con JASYPT antes de guardarse en la BD.
El técnico OVI puede ver el listado de usuarios pendientes y activar su cuenta desde su portal, lo que cambia activated = true y status = active.
Una vez activada la cuenta, el usuario puede acceder a su portal completo con todas las opciones disponibles. Si intenta acceder antes de ser validado, verá una página informativa con el estado actual de su cuenta.
Los usuarios pueden modificar sus datos personales desde su portal.
Los usuarios pueden cambiar su contraseña desde su portal, introduciendo la contraseña actual y la nueva (mínimo 6 caracteres).

      - Usuario creado con esta funcionalidad: LeoMessi (patata)

2. Gestión de candidatos a PAP o PATI --> Cualquier persona interesada puede trabajar como asistente personal registrandose en la aplicación como PAP/PATI. Los datos a registrar se definirán en base a los formularios actuales que tiene la OVI. Al igual que pasaba con los OVI Users, aquí el pap/pati tendrá que poner sus credenciales en el formulario de registro pero su cuenta estará desactivada en un principio, por lo que solo podrá entrar para ver la activación de esa cuenta. El técnico OVI tendrá otra opción para validar Pap/Patis y será el que activará la cuenta de ese pap/pati y a partir de este momento ya podrá acceder a la sección "Mi portal" y ver todas las opciones que tiene. 

      - Usuario creado con esta funcionalidad: edgar.adell (patata)

3. Solicitudes de Asistencia Personal --> Las personas usuarias de la OVI deben poder registrar una petición de asistencia personal y seguir el estado de la misma (en revisión, aprobada, cerrada con contrato, cerrada con contrato finalizado o rechazada). La asignación se hará por parte del técnico directamente, es decir, lo hará este de forma manual a través de una opción que tendrá para ello en la sección "Mi portal". Si se llega a un acuerdo definitivo, se firmará un contrato, por lo que la aplicación debe guardar los datos de inicio y final de contrato, y el documento PDF del contrato definitivo. (Esto último preguntarle en clase). 

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



