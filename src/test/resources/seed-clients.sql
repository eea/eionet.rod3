INSERT IGNORE INTO T_CLIENT (PK_CLIENT_ID, CLIENT_NAME, CLIENT_ACRONYM,
                             CLIENT_URL, CLIENT_ADDRESS, CLIENT_EMAIL, DESCRIPTION, POSTAL_CODE,
                             CITY, COUNTRY, CLIENT_SHORT_NAME)
VALUES ( 1, 'Test client', 'TC', 'http://www.unep.org/ozone', 'Nowhere', 'nowhere@example.com'
       , 'description', '0000', 'Longyearbyen', 'Norway', 'Test');
INSERT IGNORE INTO T_CLIENT (PK_CLIENT_ID, CLIENT_NAME, CLIENT_ACRONYM,
                             CLIENT_URL, CLIENT_ADDRESS, CLIENT_EMAIL, DESCRIPTION, POSTAL_CODE,
                             CITY, COUNTRY, CLIENT_SHORT_NAME)
VALUES (2, 'Test client2', 'TC',
        'http://www.unep.org/ozone', 'Nowhere2', 'nowhere@example.com',
        'description2', '0000', 'Longyearbyen', 'Norway', 'Test2');
