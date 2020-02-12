package com.kodomo.stockhelper.config;

public class CustomMysqlDialect extends org.hibernate.dialect.MySQL5InnoDBDialect {
    public String getTableTypeString() {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }
}