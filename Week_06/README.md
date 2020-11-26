# homework
## 必做题
基于电商交易场景（用户、商品、订单），设计一套简单的表结构。

本次设计比较简单，因为这个场景可以扩展的方向太大了，而且自己做的电商类项目也比较少，就设计了几张主要的表。
目前能想到的扩展点：1是国际化，2是围绕着用户表、订单表、商品表的外围属性表，包括用户所关联的其他信息（如会员等），商品的库存、属性等。

目前的设计思路：
* 用户表（描述用户的基本信息）
* 用户地址表（描述用户所填的地址的信息，及用户和订单的关联关系）
* 订单表（描述订单的基本信息）
* 订单商品表（描述订单所含商品的基本信息，及订单与商品的关联关系）
* 商品表（描述商品的基本信息）

下面是表的ER图及表结构DDL：

![](https://github.com/eazonshaw/JAVA-000/blob/main/Week_06/1.png)

```sql
-- -----------------------------------------------------
-- Table `mydb`.`USER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`USER` (
  `USER_ID` INT NOT NULL COMMENT '用户唯一标识',
  `TYPE` VARCHAR(45) NULL COMMENT '用户类型',
  `STATUS` VARCHAR(10) NOT NULL COMMENT '用户状态',
  `PHONE` VARCHAR(45) NOT NULL COMMENT '手机号',
  `IDENTITY` VARCHAR(45) NULL COMMENT '身份证',
  `BIRTH` DATE NULL COMMENT '生日',
  `USER_NAME` VARCHAR(45) NULL COMMENT '用户名',
  `SEX` VARCHAR(4) NULL COMMENT '性别',
  `NICK_NAME` VARCHAR(45) NULL COMMENT '昵称',
  `LOGIN_NAME` VARCHAR(45) NULL COMMENT '登录名',
  `CREATED_TIME` DATETIME(10) NULL,
  `UPDATED_TIME` DATETIME(10) NULL,
  PRIMARY KEY (`USER_ID`),
  UNIQUE INDEX `PHONE_UNIQUE` (`PHONE` ASC) VISIBLE)
ENGINE = InnoDB
COMMENT = '用户表';


-- -----------------------------------------------------
-- Table `mydb`.`SHOP_WARE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`SHOP_WARE` (
  `WARES_ID` INT NOT NULL COMMENT '商品的唯一标识',
  `WARE_NAME` VARCHAR(45) NULL COMMENT '商品名称',
  `WARE_PRICE` VARCHAR(45) NULL COMMENT '商品价格',
  `CREATED_TIME` DATETIME(10) NULL,
  `UPDATED_TIME` DATETIME(10) NULL,
  PRIMARY KEY (`WARES_ID`))
ENGINE = InnoDB
COMMENT = '商品表';


-- -----------------------------------------------------
-- Table `mydb`.`USER_ADDRESS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`USER_ADDRESS` (
  `ADDRESS_ID` INT NOT NULL,
  `USER_ID` INT NOT NULL,
  `ADDRESS_DETAIL` VARCHAR(500) NULL COMMENT '地址详情',
  `TO_USERNAME` VARCHAR(45) NULL COMMENT '收货人',
  `TO_PHONE` VARCHAR(45) NULL COMMENT '收货人联系方式',
  `STATUS` VARCHAR(10) NULL COMMENT '地址的状态',
  `CREATED_TIME` DATETIME(10) NULL,
  `UPDATED_TIME` DATETIME(10) NULL,
  PRIMARY KEY (`ADDRESS_ID`),
  INDEX `fk_USER_ADDRESS_USER_idx` (`USER_ID` ASC) VISIBLE,
  CONSTRAINT `fk_USER_ADDRESS_USER`
    FOREIGN KEY (`USER_ID`)
    REFERENCES `mydb`.`USER` (`USER_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = '用户地址表';


-- -----------------------------------------------------
-- Table `mydb`.`SHOP_ORDER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`SHOP_ORDER` (
  `ORDER_ID` INT NOT NULL COMMENT '订单唯一标识',
  `ADDRESS_ID` INT NOT NULL,
  `ORDER_CODE` VARCHAR(45) NOT NULL COMMENT '订单编号',
  `ORDER_TITLE` VARCHAR(45) NULL COMMENT '订单概要',
  `ORDER_SUMMARY` VARCHAR(45) NULL COMMENT '订单金额',
  `ORDER_STATUS` VARCHAR(45) NULL COMMENT '订单状态',
  `CREATED_TIME` DATETIME(10) NULL,
  `UPDATED_TIME` DATETIME(10) NULL,
  PRIMARY KEY (`ORDER_ID`),
  UNIQUE INDEX `ORDER_CODE_UNIQUE` (`ORDER_CODE` ASC) VISIBLE,
  INDEX `fk_SHOP_ORDER_USER_ADDRESS1_idx` (`ADDRESS_ID` ASC) VISIBLE,
  CONSTRAINT `fk_SHOP_ORDER_USER_ADDRESS1`
    FOREIGN KEY (`ADDRESS_ID`)
    REFERENCES `mydb`.`USER_ADDRESS` (`ADDRESS_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = '商品订单表';


-- -----------------------------------------------------
-- Table `mydb`.`SHOP_ORDER_WARE_REL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`SHOP_ORDER_WARE_REL` (
  `OW_ID` INT NOT NULL,
  `ORDER_ID` INT NOT NULL,
  `WARES_ID` INT NOT NULL,
  `NUM` VARCHAR(45) NULL COMMENT '商品的数量',
  `ACCOUNT` VARCHAR(45) NULL COMMENT '商品的总价',
  `CREATED_TIME` DATETIME(10) NULL,
  `UPDATED_TIME` DATETIME(10) NULL,
  PRIMARY KEY (`OW_ID`),
  INDEX `fk_SHOP_ORDER_WARE_REL_SHOP_ORDER1_idx` (`ORDER_ID` ASC) VISIBLE,
  INDEX `fk_SHOP_ORDER_WARE_REL_SHOP_WARE1_idx` (`WARES_ID` ASC) VISIBLE,
  CONSTRAINT `fk_SHOP_ORDER_WARE_REL_SHOP_ORDER1`
    FOREIGN KEY (`ORDER_ID`)
    REFERENCES `mydb`.`SHOP_ORDER` (`ORDER_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_SHOP_ORDER_WARE_REL_SHOP_WARE1`
    FOREIGN KEY (`WARES_ID`)
    REFERENCES `mydb`.`SHOP_WARE` (`WARES_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = '订单商品关联表';
```
