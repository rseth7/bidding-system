DROP TABLE IF EXISTS auction_details;
DROP TABLE IF EXISTS auction_detail_history;

CREATE TABLE auction_details (
  id BIGINT AUTO_INCREMENT  PRIMARY KEY,
  item_code VARCHAR(50) NOT NULL UNIQUE,
  step_rate NUMERIC NOT NULL,
  minimum_base_rate NUMERIC NOT NULL,
  state VARCHAR(250) NOT NULL,
  current_bid_rate NUMERIC NOT NULL,
  created_at datetime DEFAULT CURRENT_TIMESTAMP,
  updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by VARCHAR(250) NOT NULL,
  updated_by VARCHAR(250) NOT NULL,
  version INT NOT NULL
);

CREATE TABLE auction_detail_history (
  id BIGINT AUTO_INCREMENT  PRIMARY KEY,
  user_token VARCHAR(50) NOT NULL,
  item_code VARCHAR(50) NOT NULL,
  step_rate NUMERIC NOT NULL,
  minimum_base_rate NUMERIC NOT NULL,
  state VARCHAR(250) NOT NULL,
  current_bid_rate NUMERIC NOT NULL,
  created_at datetime DEFAULT CURRENT_TIMESTAMP,
  updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by VARCHAR(250) NOT NULL,
  updated_by VARCHAR(250) NOT NULL
);

CREATE INDEX idx_auction_details_item_code ON auction_details(item_code);