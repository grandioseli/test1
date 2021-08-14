package com.example.mockapi.domain;

public enum EnvEnum {
    env1(1, 1, "dev", "开发环境"),
    env2(2, 1, "test", "测试环境"),
    env3(3, 1, "stage", "灰度环境"),
    env4(4, 1, "online", "生产环境"),
    env5(5, 1, "daily", "日常环境"),
    env6(6, 1, "conbine", "联调环境"),
    env7(7, 1, "pre", "预发环境"),
    env8(8, 1, "integrate", "集成环境"),
    env9(9, 1, "custom", "客开环境"),
    env10(10, 1, "premise-dev", "专属开发"),
    env11(11, 1, "premise-test", "专属测试"),
    env14(14, 1, "premise-daily", "专属日常"),
    env15(15, 1, "premise-combine", "专属联调"),
    env17(17, 1, "iteration", "迭代环境"),
    env18(18, 1, "sandbox", "沙箱环境"),
    env19(19, 1, "kfzds", "开发者大赛环境"),
    env21(21, 1, "transfer", "迁移环境"),
    env23(23, 1, "premise-caep", "专属VIP"),
    env24(24, 1, "shangyao", "上药环境"),
    env25(25, 1, "abroad-test", "海外部署测试环境"),
    env26(26, 1, "online-ap-sg1", "生产环境（新加坡）"),
    env304(304, 1, "online-cn-ecology", "生态-生产环境"),
    env305(305, 1, "test-cn-ecology", "生态-测试环境"),
    env306(306, 1, "dev-cn-ecology", "生态-开发环境");
    int id;
    int dataCenterId;
    private String name;
    private String display_name;

    EnvEnum() {
    }

    EnvEnum(int id, int dataCenterId, String name, String display_name) {
        this.id = id;
        this.dataCenterId = dataCenterId;
        this.name = name;
        this.display_name = display_name;
    }

    /**
     * 根据环境名称获取对应id
     *
     * @param name 环境名称
     * @return
     */
    public static Integer getIdByName(String name) {
        for (EnvEnum envenum : values()) {
            if (envenum.getName().equals(name)) {
                return envenum.getId();
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getDataCenterId() {
        return dataCenterId;
    }

    public String getDisplay_name() {
        return display_name;
    }
}
