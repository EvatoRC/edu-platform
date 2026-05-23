-- ============================================================
-- STUDENT SERVICE - Schema Oracle
-- ============================================================
CREATE SEQUENCE STUDENT_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE STUDENTS (
    ID         NUMBER PRIMARY KEY,
    NAME       VARCHAR2(150) NOT NULL,
    EMAIL      VARCHAR2(200) NOT NULL UNIQUE,
    PHONE      VARCHAR2(20),
    BIO        VARCHAR2(500),
    ACTIVE     NUMBER(1)    DEFAULT 1 NOT NULL,
    CREATED_AT TIMESTAMP    NOT NULL,
    UPDATED_AT TIMESTAMP    NOT NULL,
    CONSTRAINT chk_student_active CHECK (ACTIVE IN (0,1))
);

CREATE INDEX idx_students_email  ON STUDENTS(EMAIL);
CREATE INDEX idx_students_active ON STUDENTS(ACTIVE);
