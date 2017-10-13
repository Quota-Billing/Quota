-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2017-10-13 15:51:34.291

-- tables
-- Table: partner
CREATE TABLE partner (
  partner_id varchar(128)  NOT NULL,
  api_key varchar(128)  NOT NULL,
  CONSTRAINT api_key_unique UNIQUE (api_key) NOT DEFERRABLE  INITIALLY IMMEDIATE,
  CONSTRAINT partner_pk PRIMARY KEY (partner_id)
);

-- Table: product
CREATE TABLE product (
  product_id varchar(128)  NOT NULL,
  partner_id varchar(128)  NOT NULL,
  product_name varchar(256)  NOT NULL,
  CONSTRAINT product_name_unique UNIQUE (product_name) NOT DEFERRABLE  INITIALLY IMMEDIATE,
  CONSTRAINT product_pk PRIMARY KEY (product_id,partner_id)
);

-- Table: quota
CREATE TABLE quota (
  quota_id varchar(128)  NOT NULL,
  product_id varchar(128)  NOT NULL,
  partner_id varchar(128)  NOT NULL,
  quota_name varchar(256)  NOT NULL,
  type varchar(128)  NOT NULL,
  CONSTRAINT quota_name_unique UNIQUE (quota_name) NOT DEFERRABLE  INITIALLY IMMEDIATE,
  CONSTRAINT quota_pk PRIMARY KEY (quota_id,product_id,partner_id)
);

CREATE INDEX type_index on quota (type ASC);

-- Table: tier
CREATE TABLE tier (
  tier_id varchar(128)  NOT NULL,
  quota_id varchar(128)  NOT NULL,
  product_id varchar(128)  NOT NULL,
  partner_id varchar(128)  NOT NULL,
  tier_name varchar(256)  NOT NULL,
  max varchar(256)  NOT NULL,
  price varchar(256)  NOT NULL,
  CONSTRAINT tier_name_unique UNIQUE (tier_name) NOT DEFERRABLE  INITIALLY IMMEDIATE,
  CONSTRAINT tier_pk PRIMARY KEY (tier_id,quota_id,product_id,partner_id)
);

-- Table: user
CREATE TABLE "user" (
  user_id varchar(128)  NOT NULL,
  product_d varchar(128)  NOT NULL,
  partner_id varchar(128)  NOT NULL,
  CONSTRAINT user_pk PRIMARY KEY (user_id,product_d,partner_id)
);

-- Table: user_tier
CREATE TABLE user_tier (
  user_id varchar(128)  NOT NULL,
  tier_id varchar(128)  NOT NULL,
  quota_id varchar(128)  NOT NULL,
  product_id varchar(128)  NOT NULL,
  partner_id varchar(128)  NOT NULL,
  value varchar(256)  NOT NULL,
  CONSTRAINT user_tier_pk PRIMARY KEY (user_id,tier_id,quota_id,product_id,partner_id)
);

-- foreign keys
-- Reference: product_partner (table: product)
ALTER TABLE product ADD CONSTRAINT product_partner
FOREIGN KEY (partner_id)
REFERENCES partner (partner_id)
ON DELETE  CASCADE
ON UPDATE  CASCADE
NOT DEFERRABLE
INITIALLY IMMEDIATE
;

-- Reference: quota_product (table: quota)
ALTER TABLE quota ADD CONSTRAINT quota_product
FOREIGN KEY (product_id, partner_id)
REFERENCES product (product_id, partner_id)
ON DELETE  CASCADE
ON UPDATE  CASCADE
NOT DEFERRABLE
INITIALLY IMMEDIATE
;

-- Reference: tier_quota (table: tier)
ALTER TABLE tier ADD CONSTRAINT tier_quota
FOREIGN KEY (quota_id, product_id, partner_id)
REFERENCES quota (quota_id, product_id, partner_id)
ON DELETE  CASCADE
ON UPDATE  CASCADE
NOT DEFERRABLE
INITIALLY IMMEDIATE
;

-- Reference: user_product (table: user)
ALTER TABLE "user" ADD CONSTRAINT user_product
FOREIGN KEY (product_d, partner_id)
REFERENCES product (product_id, partner_id)
ON DELETE  CASCADE
ON UPDATE  CASCADE
NOT DEFERRABLE
INITIALLY IMMEDIATE
;

-- Reference: user_tier_tier (table: user_tier)
ALTER TABLE user_tier ADD CONSTRAINT user_tier_tier
FOREIGN KEY (tier_id, quota_id, product_id, partner_id)
REFERENCES tier (tier_id, quota_id, product_id, partner_id)
ON DELETE  CASCADE
ON UPDATE  CASCADE
NOT DEFERRABLE
INITIALLY IMMEDIATE
;

-- Reference: user_tier_user (table: user_tier)
ALTER TABLE user_tier ADD CONSTRAINT user_tier_user
FOREIGN KEY (user_id, product_id, partner_id)
REFERENCES "user" (user_id, product_d, partner_id)
ON DELETE  CASCADE
ON UPDATE  CASCADE
NOT DEFERRABLE
INITIALLY IMMEDIATE
;

-- End of file.
