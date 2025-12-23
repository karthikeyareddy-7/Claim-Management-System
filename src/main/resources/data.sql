CREATE TABLE users (
    userid INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    email VARCHAR(100) NOT NULL,
    gender ENUM('M','F') NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL
);

CREATE TABLE claims (
    claimid INT PRIMARY KEY AUTO_INCREMENT,
    userid INT NOT NULL,  -- refers foreign key to users
    descriptioncode VARCHAR(100) NOT NULL,
    procedurecode VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    date_submitted DATE NOT NULL,
    FOREIGN KEY (userid) REFERENCES users(userid)
);
