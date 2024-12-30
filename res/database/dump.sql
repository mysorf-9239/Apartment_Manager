SET
SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET
time_zone = "+07:00";
SET
GLOBAL event_scheduler = ON;

CREATE
DATABASE IF NOT EXISTS apartment;
USE
apartment;

CREATE TABLE `account`
(
    `id`       int UNSIGNED NOT NULL,
    `username` varchar(50) COLLATE utf8mb4_general_ci  NOT NULL,
    `password` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
    `role`     enum('admin','user') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'user',
    `image`    text COLLATE utf8mb4_general_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `account` (`id`, `username`, `password`, `role`, `image`)
VALUES (1, 'mysorf', 'password', 'admin', NULL),
       (2, 'admin1', 'ad1', 'user', NULL);

CREATE TABLE `residents`
(
    `id`               int UNSIGNED NOT NULL,
    `full_name`        varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
    `date_of_birth`    date                                    NOT NULL,
    `gender`           enum('Nam','Nữ','Khác') COLLATE utf8mb4_general_ci NOT NULL,
    `id_card`          varchar(20) COLLATE utf8mb4_general_ci  NOT NULL,
    `is_temp_resident` int NOT NULL DEFAULT '0',
    `household_id`     int UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `households`
(
    `id`                int UNSIGNED NOT NULL,
    `address`           varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
    `acreage`           decimal(10, 2)                          NOT NULL,
    `head_of_household` int UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `relationships`
(
    `id`                   int NOT NULL,
    `resident_id`          int UNSIGNED DEFAULT NULL,
    `head_of_household_id` int UNSIGNED DEFAULT NULL,
    `relationship_type`    varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `household_id`         int UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `fees`
(
    `id`              int UNSIGNED NOT NULL,
    `fee_name`        varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
    `fee_description` text COLLATE utf8mb4_general_ci,
    `amount`          decimal(10, 2)                          NOT NULL,
    `created_at`      timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status`          enum('Đang thu phí', 'Kết thúc') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Đang thu phí',
    `type`            enum('Chung', 'Riêng', 'Bắt buộc') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Chung',
    `cycle`           enum('Hàng tháng', 'Hàng năm', 'Không') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Không'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `households_fees`
(
    `id`           int            NOT NULL,
    `household_id` int UNSIGNED NOT NULL,
    `fee_id`       int UNSIGNED NOT NULL,
    `amount_due`   decimal(10, 2) NOT NULL,
    `due_date`     date           NOT NULL,
    `status`       enum('Chưa thanh toán','Đã thanh toán','Thanh toán một phần','Quá hạn','Đã hủy','Đang xử lý') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Chưa thanh toán',
    `created_at`   timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `payments`
(
    `id`             int UNSIGNED NOT NULL,
    `household_id`   int UNSIGNED NOT NULL,
    `fee_id`         int UNSIGNED NOT NULL,
    `payment_amount` decimal(10, 2) NOT NULL,
    `payment_date`   timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `payment_method` enum('Tiền mặt','Chuyển khoản','Thẻ') COLLATE utf8mb4_general_ci NOT NULL,
    `note`           text COLLATE utf8mb4_general_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE `account`
    ADD PRIMARY KEY (`id`);

ALTER TABLE `fees`
    ADD PRIMARY KEY (`id`);

ALTER TABLE `households`
    ADD PRIMARY KEY (`id`),
  ADD KEY `fk_head_of_household` (`head_of_household`);

ALTER TABLE `households_fees`
    ADD PRIMARY KEY (`id`),
  ADD KEY `household_id` (`household_id`),
  ADD KEY `fee_id` (`fee_id`);

ALTER TABLE `payments`
    ADD PRIMARY KEY (`id`),
  ADD KEY `household_id` (`household_id`),
  ADD KEY `fee_id` (`fee_id`);

ALTER TABLE `relationships`
    ADD PRIMARY KEY (`id`),
  ADD KEY `fk_resident_id` (`resident_id`),
  ADD KEY `fk_head_of_household_id` (`head_of_household_id`),
  ADD KEY `fk_householdId` (`household_id`);

ALTER TABLE `residents`
    ADD PRIMARY KEY (`id`),
  ADD KEY `fk_household` (`household_id`);

ALTER TABLE `account`
    MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

ALTER TABLE `fees`
    MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

ALTER TABLE `households`
    MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

ALTER TABLE `households_fees`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

ALTER TABLE `payments`
    MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

ALTER TABLE `relationships`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

ALTER TABLE `residents`
    MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

ALTER TABLE `households`
    ADD CONSTRAINT `fk_head_of_household` FOREIGN KEY (`head_of_household`) REFERENCES `residents` (`id`) ON DELETE CASCADE;

ALTER TABLE `households_fees`
    ADD CONSTRAINT `households_fees_ibfk_1` FOREIGN KEY (`household_id`) REFERENCES `households` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `households_fees_ibfk_2` FOREIGN KEY (`fee_id`) REFERENCES `fees` (`id`) ON
DELETE
CASCADE;

ALTER TABLE `payments`
    ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`household_id`) REFERENCES `households` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`fee_id`) REFERENCES `fees` (`id`) ON
DELETE
CASCADE;

ALTER TABLE `relationships`
    ADD CONSTRAINT `fk_head_of_household_id` FOREIGN KEY (`head_of_household_id`) REFERENCES `residents` (`id`),
  ADD CONSTRAINT `fk_householdId` FOREIGN KEY (`household_id`) REFERENCES `households` (`id`),
  ADD CONSTRAINT `fk_resident_id` FOREIGN KEY (`resident_id`) REFERENCES `residents` (`id`);

ALTER TABLE `residents`
    ADD CONSTRAINT `fk_household` FOREIGN KEY (`household_id`) REFERENCES `households` (`id`);




CREATE TRIGGER after_household_insert
    AFTER INSERT
    ON households
    FOR EACH ROW
BEGIN
    DECLARE time_interval INT;
    DECLARE interval_unit VARCHAR(20);

    -- Lấy thời gian chu kỳ và đơn vị từ bảng fees
    SET time_interval = (SELECT CASE
                                  WHEN cycle = 'Hàng tháng' THEN 1
                                  WHEN cycle = 'Hàng năm' THEN 12
                                  ELSE 0
END
FROM fees
WHERE type IN ('Chung', 'Bắt buộc')
LIMIT 1);

SET interval_unit = (SELECT CASE
                                   WHEN cycle = 'Hàng tháng' THEN 'MONTH'
                                   WHEN cycle = 'Hàng năm' THEN 'YEAR'
                                   ELSE 'DAY'
                                 END
                         FROM fees
                         WHERE type IN ('Chung', 'Bắt buộc')
                         LIMIT 1);

    -- Chèn vào bảng households_fees
INSERT INTO households_fees (household_id, fee_id, amount_due, due_date)
SELECT NEW.id,
       fees.id,  -- Thêm bảng fees để tham chiếu cột id
       CASE
           WHEN fees.type = 'Bắt buộc' THEN fees.amount * NEW.acreage
           ELSE fees.amount
           END,
       DATE_ADD(CURRENT_DATE, INTERVAL time_interval MONTH)
FROM fees
WHERE fees.type IN ('Chung', 'Bắt buộc')
    LIMIT 1;  -- Lấy một dòng từ bảng fees (nếu có nhiều dòng thì có thể cần thay đổi)
END;

CREATE TRIGGER after_fee_insert
    AFTER INSERT
    ON fees
    FOR EACH ROW
BEGIN
    DECLARE time_interval INT;
    DECLARE interval_unit VARCHAR(20);

    SET time_interval = CASE
                          WHEN NEW.cycle = 'Hàng tháng' THEN 1
                          WHEN NEW.cycle = 'Hàng năm' THEN 12
                          ELSE 0
END;

SET interval_unit = CASE
                          WHEN NEW.cycle = 'Hàng tháng' THEN 'MONTH'
                          WHEN NEW.cycle = 'Hàng năm' THEN 'YEAR'
                          ELSE 'DAY'
END;

INSERT INTO households_fees (household_id, fee_id, amount_due, due_date)
SELECT households.id,
       NEW.id,
       CASE
           WHEN NEW.type = 'Bắt buộc' THEN NEW.amount * households.acreage
           ELSE NEW.amount
           END,
       DATE_ADD(CURRENT_DATE, INTERVAL time_interval MONTH)
FROM households;
END;

CREATE EVENT auto_add_fees_by_cycle
ON SCHEDULE EVERY 1 DAY
DO
BEGINF
INSERT INTO households_fees (household_id, fee_id, amount_due, due_date, status)
SELECT hf.household_id,
       hf.fee_id,
       CASE
           WHEN f.type = 'Bắt buộc' THEN f.amount * h.acreage
           ELSE f.amount
           END,
       DATE_ADD(hf.due_date,
                INTERVAL CASE
                               WHEN f.cycle = 'Hàng tháng' THEN 1
                               WHEN f.cycle = 'Hàng năm' THEN 12
                               ELSE 0
                             END MONTH),
       'Chưa thanh toán'
FROM households_fees hf
         JOIN fees f ON hf.fee_id = f.id
         JOIN households h ON hf.household_id = h.id
WHERE f.cycle != 'Không'
          AND hf.status IN ('Đã thanh toán', 'Quá hạn')
          AND CURRENT_DATE >= hf.due_date;
END;
COMMIT;