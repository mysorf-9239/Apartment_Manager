-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Oct 17, 2024 at 07:09 AM
-- Server version: 8.3.0
-- PHP Version: 8.3.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `apartment`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
  `id` int UNSIGNED NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `role` enum('admin','user') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'user',
  `image` text COLLATE utf8mb4_general_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`id`, `username`, `password`, `role`, `image`) VALUES
(1, 'mysorf', 'password', 'admin', NULL),
(2, 'admin1', 'ad1', 'user', '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');

-- --------------------------------------------------------

--
-- Table structure for table `fees`
--

CREATE TABLE `fees` (
  `id` int UNSIGNED NOT NULL,
  `fee_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `fee_description` text COLLATE utf8mb4_general_ci,
  `amount` decimal(10,2) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` enum('active','inactive') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'active',
  `type` enum('all','part') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'all'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `fees`
--

INSERT INTO `fees` (`id`, `fee_name`, `fee_description`, `amount`, `created_at`, `updated_at`, `status`, `type`) VALUES
(1, 'fee1', 'This is a fee', 100000.00, '2024-10-01 07:04:46', '2024-10-16 14:28:29', 'active', 'part'),
(2, 'fee2', 'This is a fee', 10000.00, '2024-10-01 07:04:58', '2024-10-16 14:28:32', 'active', 'part'),
(3, 'Phí dịch vụ 1', 'Test', 1000000.00, '2024-10-01 07:50:59', '2024-10-16 14:28:34', 'active', 'part'),
(4, 'Phí dịch vụ 2', 'Test change', 10000.00, '2024-10-01 07:51:41', '2024-10-16 14:28:38', 'active', 'part');

--
-- Triggers `fees`
--
DELIMITER $$
CREATE TRIGGER `after_fee_insert` AFTER INSERT ON `fees` FOR EACH ROW BEGIN
    -- Kiểm tra nếu type của fee mới là 'all'
    IF NEW.type = 'all' THEN
        -- Chèn khoản phí mới cho tất cả các hộ gia đình trong bảng households
        INSERT INTO households_fees (household_id, fee_id, amount_due, due_date, status, created_at, updated_at)
        SELECT 
            households.id,               -- household_id
            NEW.id,                      -- fee_id (phí vừa được thêm)
            NEW.amount,                  -- amount_due (dựa trên amount của phí)
            CURDATE() + INTERVAL 30 DAY, -- due_date (giả sử hạn thanh toán là 30 ngày sau khi phí được thêm)
            'Chưa thanh toán',           -- status
            NOW(),                       -- created_at
            NOW()                        -- updated_at
        FROM households;  -- Bảng chứa tất cả các household (gia đình) hiện có
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `households`
--

CREATE TABLE `households` (
  `id` int UNSIGNED NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `head_of_household` int UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `households`
--

INSERT INTO `households` (`id`, `address`, `head_of_household`) VALUES
(1, '123 Đường A, Thành phố B', 1),
(4, '111, Đường N', 3),
(5, '123, 234', 14);

-- --------------------------------------------------------

--
-- Table structure for table `households_fees`
--

CREATE TABLE `households_fees` (
  `id` int NOT NULL,
  `household_id` int UNSIGNED NOT NULL,
  `fee_id` int UNSIGNED NOT NULL,
  `amount_due` decimal(10,2) NOT NULL,
  `due_date` date NOT NULL,
  `status` enum('Chưa thanh toán','Đã thanh toán','Thanh toán một phần','Quá hạn','Đã hủy','Đang xử lý') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Chưa thanh toán',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `households_fees`
--

INSERT INTO `households_fees` (`id`, `household_id`, `fee_id`, `amount_due`, `due_date`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 100000.00, '2024-10-10', 'Đã thanh toán', '2024-10-01 14:00:56', '2024-10-02 16:09:46'),
(3, 4, 1, 100000.00, '2024-10-10', 'Chưa thanh toán', '2024-10-01 14:01:33', '2024-10-01 14:01:33'),
(4, 5, 1, 100000.00, '2024-11-01', 'Đã thanh toán', '2024-10-02 06:18:34', '2024-10-02 06:20:06'),
(7, 5, 2, 100000.00, '2024-11-01', 'Chưa thanh toán', '2024-10-02 06:18:34', '2024-10-02 06:18:34'),
(8, 4, 2, 100000.00, '2024-11-01', 'Chưa thanh toán', '2024-10-02 06:18:34', '2024-10-02 06:18:34'),
(9, 1, 2, 100000.00, '2024-11-01', 'Chưa thanh toán', '2024-10-02 06:18:34', '2024-10-02 06:18:34'),
(10, 5, 3, 100000.00, '2024-11-01', 'Chưa thanh toán', '2024-10-02 06:18:34', '2024-10-02 06:18:34'),
(11, 4, 3, 100000.00, '2024-11-01', 'Chưa thanh toán', '2024-10-02 06:18:34', '2024-10-02 06:18:34'),
(12, 1, 3, 100000.00, '2024-11-01', 'Chưa thanh toán', '2024-10-02 06:18:34', '2024-10-02 06:18:34'),
(13, 5, 4, 100000.00, '2024-11-01', 'Chưa thanh toán', '2024-10-02 06:18:34', '2024-10-02 06:18:34'),
(14, 4, 4, 100000.00, '2024-11-01', 'Chưa thanh toán', '2024-10-02 06:18:34', '2024-10-02 06:18:34'),
(15, 1, 4, 100000.00, '2024-11-01', 'Chưa thanh toán', '2024-10-02 06:18:34', '2024-10-02 06:18:34');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `id` int UNSIGNED NOT NULL,
  `household_id` int UNSIGNED NOT NULL,
  `fee_id` int UNSIGNED NOT NULL,
  `payment_amount` decimal(10,2) NOT NULL,
  `payment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `payment_method` enum('Tiền mặt','Chuyển khoản','Thẻ') COLLATE utf8mb4_general_ci NOT NULL,
  `note` text COLLATE utf8mb4_general_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`id`, `household_id`, `fee_id`, `payment_amount`, `payment_date`, `payment_method`, `note`) VALUES
(1, 1, 1, 10000.00, '2024-10-01 15:12:21', 'Tiền mặt', NULL),
(3, 1, 1, 10000.00, '2024-10-01 15:14:16', 'Chuyển khoản', NULL),
(4, 1, 1, 10000.00, '2024-10-01 18:07:01', 'Tiền mặt', NULL),
(6, 5, 1, 49999.00, '2024-10-01 18:14:52', 'Tiền mặt', NULL),
(7, 5, 1, 30000.00, '2024-10-01 18:14:58', 'Tiền mặt', NULL),
(8, 5, 1, 20000.00, '2024-10-02 06:20:06', 'Tiền mặt', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `relationships`
--

CREATE TABLE `relationships` (
  `id` int NOT NULL,
  `resident_id` int UNSIGNED DEFAULT NULL,
  `head_of_household_id` int UNSIGNED DEFAULT NULL,
  `relationship_type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `household_id` int UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `relationships`
--

INSERT INTO `relationships` (`id`, `resident_id`, `head_of_household_id`, `relationship_type`, `household_id`) VALUES
(1, 2, 1, 'Vợ', 1),
(4, 4, 3, 'Vợ', 4),
(5, 5, 3, 'Con', 4),
(6, 13, 14, 'Vợ', 5);

-- --------------------------------------------------------

--
-- Table structure for table `residents`
--

CREATE TABLE `residents` (
  `id` int UNSIGNED NOT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `date_of_birth` date NOT NULL,
  `gender` enum('Nam','Nữ','Khác') COLLATE utf8mb4_general_ci NOT NULL,
  `id_card` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `is_temp_resident` tinyint(1) NOT NULL DEFAULT '0',
  `household_id` int UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `residents`
--

INSERT INTO `residents` (`id`, `full_name`, `date_of_birth`, `gender`, `id_card`, `is_temp_resident`, `household_id`) VALUES
(1, 'Nguyễn Văn A', '1990-10-10', 'Nam', '123456789012', 0, 1),
(2, 'Nguyễn Thị B', '1991-10-10', 'Nữ', '987654321098', 0, 1),
(3, 'Trần Văn C', '1991-10-10', 'Nam', '456789123456', 0, 4),
(4, 'Trần Thị D', '1991-10-10', 'Nữ', '654321789012', 0, 4),
(5, 'Trần Văn E', '2010-10-10', 'Nam', '789123456789', 0, 4),
(6, 'Lê Văn E', '2014-09-08', 'Nam', '1234567890', 0, NULL),
(7, 'Lê Thị F', '2015-09-28', 'Nữ', '1234567890', 0, NULL),
(8, 'Lê Văn E', '2014-09-08', 'Nam', '1234567890', 0, NULL),
(9, 'Lê Thị F', '2015-09-28', 'Nữ', '1234567890', 0, NULL),
(10, 'Lê Văn M', '2014-09-08', 'Nam', '1234567890', 0, NULL),
(11, 'Lê Thị N', '2015-09-28', 'Nữ', '1234567890', 0, NULL),
(12, 'Lê Văn P', '2014-09-08', 'Nam', '1234567890', 0, NULL),
(13, 'Lê Thị Q', '2015-09-28', 'Nữ', '1234567890', 0, 5),
(14, 'Nguyen Van T', '1989-01-01', 'Nam', '123456789012', 0, 5),
(17, 'Tran Duc B', '2000-10-10', 'Nam', '29129192919219', 0, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `fees`
--
ALTER TABLE `fees`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `households`
--
ALTER TABLE `households`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_head_of_household` (`head_of_household`);

--
-- Indexes for table `households_fees`
--
ALTER TABLE `households_fees`
  ADD PRIMARY KEY (`id`),
  ADD KEY `household_id` (`household_id`),
  ADD KEY `fee_id` (`fee_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `household_id` (`household_id`),
  ADD KEY `fee_id` (`fee_id`);

--
-- Indexes for table `relationships`
--
ALTER TABLE `relationships`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_resident_id` (`resident_id`),
  ADD KEY `fk_head_of_household_id` (`head_of_household_id`),
  ADD KEY `fk_householdId` (`household_id`);

--
-- Indexes for table `residents`
--
ALTER TABLE `residents`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_household` (`household_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `account`
--
ALTER TABLE `account`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `fees`
--
ALTER TABLE `fees`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `households`
--
ALTER TABLE `households`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `households_fees`
--
ALTER TABLE `households_fees`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `relationships`
--
ALTER TABLE `relationships`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `residents`
--
ALTER TABLE `residents`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `households`
--
ALTER TABLE `households`
  ADD CONSTRAINT `fk_head_of_household` FOREIGN KEY (`head_of_household`) REFERENCES `residents` (`id`);

--
-- Constraints for table `households_fees`
--
ALTER TABLE `households_fees`
  ADD CONSTRAINT `households_fees_ibfk_1` FOREIGN KEY (`household_id`) REFERENCES `households` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `households_fees_ibfk_2` FOREIGN KEY (`fee_id`) REFERENCES `fees` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`household_id`) REFERENCES `households` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`fee_id`) REFERENCES `fees` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `relationships`
--
ALTER TABLE `relationships`
  ADD CONSTRAINT `fk_head_of_household_id` FOREIGN KEY (`head_of_household_id`) REFERENCES `residents` (`id`),
  ADD CONSTRAINT `fk_householdId` FOREIGN KEY (`household_id`) REFERENCES `households` (`id`),
  ADD CONSTRAINT `fk_resident_id` FOREIGN KEY (`resident_id`) REFERENCES `residents` (`id`);

--
-- Constraints for table `residents`
--
ALTER TABLE `residents`
  ADD CONSTRAINT `fk_household` FOREIGN KEY (`household_id`) REFERENCES `households` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
