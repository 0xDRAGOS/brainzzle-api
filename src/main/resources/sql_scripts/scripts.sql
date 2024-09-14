CREATE SCHEMA brainzzle_schema;
CREATE TABLE users
(
    user_id    SERIAL       NOT NULL PRIMARY KEY,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(256) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL
);

CREATE TABLE questions (
    question_id   SERIAL    NOT NULL PRIMARY KEY,
    user_id       INT       NOT NULL,
    question_text TEXT      NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE answers_questions (
    answer_id   SERIAL    NOT NULL PRIMARY KEY,
    question_id INT       NOT NULL,
    user_id     INT       NOT NULL,
    answer_text TEXT      NOT NULL,
    is_correct BOOLEAN    NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE images_questions (
    image_id SERIAL NOT NULL PRIMARY KEY,
    question_id INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
);

INSERT INTO users(email, password, first_name, last_name) VALUES ('test@example.com', 'test', 'FirstName', 'LastName');
INSERT INTO questions(user_id, question_text) VALUES (1, 'test test test');
INSERT INTO answers_questions(question_id, user_id, answer_text, is_correct) VALUES (1, 1, 'test', TRUE);
INSERT INTO answers_questions(question_id, user_id, answer_text, is_correct) VALUES (1, 1, 'test2', FALSE);
INSERT INTO answers_questions(question_id, user_id, answer_text, is_correct) VALUES (1, 1, 'test3', FALSE);
INSERT INTO answers_questions(question_id, user_id, answer_text, is_correct) VALUES (1, 1, 'test4', FALSE);
INSERT INTO answers_questions(question_id, user_id, answer_text, is_correct) VALUES (1, 1, 'test5', FALSE);
INSERT INTO images_questions(question_id, image_url) VALUES (1, 'url.test.ro');

