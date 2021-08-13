package com.example.mockapi.domain;

public enum dev {
    dev1(1,1,"dev","开发环境"),
    dev2(2,1,"dev","测试环境"),
    dev3(3,1,"dev","灰度环境"),
    dev4(4,1,"dev","生产环境"),
    dev5(5,1,"dev","日常环境"),
    dev6(6,1,"dev","联调环境"),
    dev7(7,1,"dev","预发环境"),
    dev8(8,1,"dev","集成环境"),
    dev9(9,1,"dev","客开环境"),
    dev10(10,1,"dev","专属开发"),
    dev11(11,1,"dev","专属测试"),
    dev14(14,1,"dev","专属日常"),
    dev15(15,1,"dev","专属联调"),
    dev17(17,1,"dev","开发环境"),
    dev18(18,1,"dev","开发环境"),
    dev19(19,1,"dev","开发环境"),
    dev20(20,1,"dev","开发环境"),
    dev21(21,1,"dev","开发环境"),
    dev22(22,1,"dev","开发环境"),
    dev23(23,1,"dev","开发环境"),
    dev24(24,1,"dev","开发环境"),
    dev25(25,1,"dev","开发环境"),
    dev26(26,1,"dev","开发环境"),
    dev304(304,1,"dev","开发环境"),
    dev305(305,1,"dev","开发环境"),
    dev306(306,1,"dev","开发环境");
    int id;
    int dataCenterId;
    private String name;
    private String display_name;
    dev(){}
    dev(int id, int dataCenterId,String name, String display_name) {
        this.id = id;
        this.dataCenterId = dataCenterId;
        this.name = name;
        this.display_name = display_name;
    }
}
