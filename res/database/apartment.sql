-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Oct 16, 2024 at 07:26 PM
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
  `role` enum('admin','user') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`id`, `username`, `password`, `role`) VALUES
(1, 'mysorf', 'password', 'admin'),
(2, 'admin1', 'ad1', 'user');

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
