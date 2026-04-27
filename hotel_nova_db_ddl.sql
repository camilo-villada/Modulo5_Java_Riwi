CREATE DATABASE IF NOT EXISTS hotel_nova_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE hotel_nova_db;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role ENUM('ADMIN', 'RECEPTIONIST') NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_users_username UNIQUE (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS rooms (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_number VARCHAR(10) NOT NULL,
  type VARCHAR(50) NOT NULL,
  capacity INT NOT NULL,
  price_per_night DECIMAL(10,2) NOT NULL,
  status ENUM('AVAILABLE', 'OCCUPIED') NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_rooms_room_number UNIQUE (room_number),
  CONSTRAINT chk_rooms_capacity_positive CHECK (capacity > 0),
  CONSTRAINT chk_rooms_price_non_negative CHECK (price_per_night >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS guests (
  id INT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  document_number VARCHAR(20) NOT NULL,
  email VARCHAR(100),
  phone_number VARCHAR(20),
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_guests_document_number UNIQUE (document_number)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reservations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  guest_id INT NOT NULL,
  room_id INT NOT NULL,
  user_id INT NOT NULL,
  check_in_date DATETIME NOT NULL,
  check_out_date DATETIME NOT NULL,
  total_cost DECIMAL(10,2) NOT NULL,
  status ENUM('ACTIVE', 'FINISHED', 'CANCELLED') NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_reservations_dates CHECK (check_out_date > check_in_date),
  CONSTRAINT chk_reservations_total_cost_non_negative CHECK (total_cost >= 0),
  CONSTRAINT fk_reservations_guest
    FOREIGN KEY (guest_id) REFERENCES guests (id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_reservations_room
    FOREIGN KEY (room_id) REFERENCES rooms (id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_reservations_user
    FOREIGN KEY (user_id) REFERENCES users (id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX idx_reservations_guest_id (guest_id),
  INDEX idx_reservations_room_id (room_id),
  INDEX idx_reservations_user_id (user_id),
  INDEX idx_reservations_check_in_date (check_in_date),
  INDEX idx_reservations_check_out_date (check_out_date)
) ENGINE=InnoDB;
