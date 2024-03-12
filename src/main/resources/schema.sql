CREATE TABLE SUBSCRIBER
(
    SUBSCRIBERID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    FNAME VARCHAR(256),
    LNAME VARCHAR(256) NOT NULL,
    PHONE NUMBER NOT NULL DEFAULT 0,
    MAIL VARCHAR(256) NOT NULL,
    ISACTIV BOOLEAN DEFAULT TRUE
);