WITH inserted_users AS (
INSERT INTO users (username, password)
VALUES
    ('first_admin', '{pbkdf2}93ceaac177f545929aa023831d907141a39b3e89f0167e60580c16810e6ca5669d7249f3d788c1a78574cc22df45143d41c09911cab9ea629f3856402e7b3147e76d2303ae10e6ee520a78ad947f1595'),
    ('first_guest', '{pbkdf2}34dea86775929bf81f0a114cdea89894f10212e91f0fc23ae4836db5dc4b6863d27e0a8000e4d351d767d385f7a59fa99270c1f1c7de0d96ea0747e377eaf301bfc5416e11496bceac8c2c25dbd34519')
    RETURNING user_id, username
    ),
    inserted_permissions AS (
INSERT INTO permissions (authority)
VALUES
    ('ROLE_ADMIN'),
    ('ROLE_GUEST')
    RETURNING permission_id, authority
    )
INSERT INTO user_permission (user_id, permission_id)
SELECT u.user_id, p.permission_id
FROM inserted_users u
         JOIN inserted_permissions p
              ON (p.authority = 'ROLE_ADMIN' AND u.username = 'first_admin')  -- 'first_admin' com 'ADMIN'
                  OR (p.authority = 'ROLE_GUEST' AND u.username = 'first_admin')  -- 'first_admin' com 'GUEST'
                  OR (p.authority = 'ROLE_GUEST' AND u.username = 'first_guest');  -- 'first_guest' com 'GUEST'