-- ============================================================
-- COURSE SERVICE - Schema Oracle
-- ============================================================
CREATE SEQUENCE COURSE_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE COURSES (
    ID             NUMBER PRIMARY KEY,
    NAME           VARCHAR2(200)  NOT NULL,
    INSTRUCTOR     VARCHAR2(150)  NOT NULL,
    DURATION_HOURS NUMBER(5)      NOT NULL,
    COST           NUMBER(10,2)   NOT NULL,
    DESCRIPTION    VARCHAR2(1000),
    CATEGORY       VARCHAR2(100)  NOT NULL,
    AVAILABLE      NUMBER(1)      DEFAULT 1 NOT NULL,
    MAX_STUDENTS   NUMBER(5),
    CREATED_AT     TIMESTAMP      NOT NULL,
    CONSTRAINT chk_cost     CHECK (COST >= 0),
    CONSTRAINT chk_duration CHECK (DURATION_HOURS > 0),
    CONSTRAINT chk_avail    CHECK (AVAILABLE IN (0,1))
);

CREATE INDEX idx_courses_category  ON COURSES(CATEGORY);
CREATE INDEX idx_courses_available ON COURSES(AVAILABLE);
