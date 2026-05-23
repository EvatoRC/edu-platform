-- ============================================================
-- COURSE SERVICE - Datos iniciales de demostración
-- ============================================================
INSERT INTO COURSES (ID, NAME, INSTRUCTOR, DURATION_HOURS, COST, DESCRIPTION, CATEGORY, AVAILABLE, MAX_STUDENTS, CREATED_AT)
VALUES (COURSE_SEQ.NEXTVAL, 'Desarrollo Web con React', 'Ana García', 40, 149.99,
        'Aprende React desde cero hasta nivel avanzado, incluyendo hooks, context y Redux.',
        'Desarrollo Web', 1, 50, SYSTIMESTAMP);

INSERT INTO COURSES (ID, NAME, INSTRUCTOR, DURATION_HOURS, COST, DESCRIPTION, CATEGORY, AVAILABLE, MAX_STUDENTS, CREATED_AT)
VALUES (COURSE_SEQ.NEXTVAL, 'Spring Boot Microservices', 'Carlos Mendoza', 60, 199.99,
        'Diseña y despliega microservicios con Spring Boot, Docker y Kubernetes.',
        'Backend', 1, 40, SYSTIMESTAMP);

INSERT INTO COURSES (ID, NAME, INSTRUCTOR, DURATION_HOURS, COST, DESCRIPTION, CATEGORY, AVAILABLE, MAX_STUDENTS, CREATED_AT)
VALUES (COURSE_SEQ.NEXTVAL, 'Machine Learning con Python', 'Laura Torres', 80, 249.99,
        'Fundamentos de ML: regresión, clasificación, redes neuronales y scikit-learn.',
        'Data Science', 1, 30, SYSTIMESTAMP);

INSERT INTO COURSES (ID, NAME, INSTRUCTOR, DURATION_HOURS, COST, DESCRIPTION, CATEGORY, AVAILABLE, MAX_STUDENTS, CREATED_AT)
VALUES (COURSE_SEQ.NEXTVAL, 'Diseño UX/UI con Figma', 'Sofía Reyes', 30, 99.99,
        'Principios de diseño, prototipado y sistemas de diseño con Figma.',
        'Diseño', 1, 60, SYSTIMESTAMP);

INSERT INTO COURSES (ID, NAME, INSTRUCTOR, DURATION_HOURS, COST, DESCRIPTION, CATEGORY, AVAILABLE, MAX_STUDENTS, CREATED_AT)
VALUES (COURSE_SEQ.NEXTVAL, 'DevOps con AWS y GitHub Actions', 'Roberto Díaz', 50, 179.99,
        'CI/CD, contenedores, infraestructura como código y despliegue en la nube.',
        'DevOps', 1, 35, SYSTIMESTAMP);

INSERT INTO COURSES (ID, NAME, INSTRUCTOR, DURATION_HOURS, COST, DESCRIPTION, CATEGORY, AVAILABLE, MAX_STUDENTS, CREATED_AT)
VALUES (COURSE_SEQ.NEXTVAL, 'Bases de Datos Oracle Avanzado', 'Miguel Fuentes', 45, 159.99,
        'Optimización de queries, PL/SQL, índices y administración de Oracle DB.',
        'Bases de Datos', 1, 25, SYSTIMESTAMP);

COMMIT;
