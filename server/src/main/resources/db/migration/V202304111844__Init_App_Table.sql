-- ----------------------------
-- Table structure for app
-- ----------------------------
CREATE TABLE `app`
(
	`id`           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
	`user_id`      BIGINT       NOT NULL COMMENT '用户ID',
	`code`         VARCHAR(32)  NOT NULL DEFAULT '' COMMENT 'APP编码',
	`name`         VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'APP名称',
	`icon`         VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'APP图标',
	`description`  VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'APP描述',
	`usedCount`    INT          NOT NULL DEFAULT 0 COMMENT '使用次数',
	`paidUseCount` INT          NOT NULL DEFAULT 0 COMMENT '付费使用次数',
	`forms`        JSON         NOT NULL COMMENT 'APP表单模板',
	`roles`        JSON         NOT NULL COMMENT 'APP预置问题模板',
	`deleted_by`   VARCHAR(64)           DEFAULT '' COMMENT '删除者',
	`deleted_at`   DATETIME              DEFAULT NULL COMMENT '删除时间',
	`created_at`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
	`created_by`   VARCHAR(64)           DEFAULT '' COMMENT '创建者',
	`updated_at`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
	`updated_by`   VARCHAR(64)           DEFAULT '' COMMENT '更新者',
	INDEX `idx_user_id` (`user_id`) USING BTREE COMMENT '用户ID索引',
	INDEX `idx_code` (`code`) USING BTREE COMMENT 'APP编码索引'
) ENGINE = InnoDB
	AUTO_INCREMENT = 1
	DEFAULT CHARSET = utf8mb4 COMMENT ='会员信息表';
