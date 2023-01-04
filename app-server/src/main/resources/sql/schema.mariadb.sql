CREATE TABLE `nat`.`tunnel_info`
(
    `id`             int          NOT NULL AUTO_INCREMENT,
    `tunnel_id`      varchar(36)  NOT NULL,
    `tunnel_name`    varchar(30)  NULL DEFAULT NULL,
    `proxy_domain`   varchar(100) NOT NULL,
    `proxy_endpoint` varchar(100) NOT NULL,
    `client_id`      varchar(36)  NOT NULL,
    `create_time`    datetime     NULL DEFAULT NULL,
    `update_time`    datetime     NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_tunnelid_client_id` (`tunnel_id` ASC, `client_id` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic;