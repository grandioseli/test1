package com.example.mockapi.domain;

public enum AuthEnum {
    auth1("c87e2267-1001-4c70-bb2a-ab41f3b81aa3", "TvDTf0rUs0l5n8rA", "uIu0YdD4ZflTD5WYZQVALLfFp9SkQh");

    private String providerId;
    private String accessKey;
    private String accessSecret;

    AuthEnum() {
    }

    AuthEnum(String providerId, String accessKey, String accessSecret) {
        this.providerId = providerId;
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
    }

    /**
     * 根据租户id获取对应accessKey
     *
     * @param providerId 微服务租户id
     *
     */
    public static String getAccessKeyByProviderId(String providerId) {
        for (AuthEnum authenum : values()) {
            if (authenum.getProviderId().equals(providerId)) {
                return authenum.getAccessKey();
            }
        }
        return null;
    }

    /**
     * 根据租户id获取accessSecret
     *
     * @param providerId 微服务租户id
     * @return
     */
    public static String getAccessSecretByProviderId(String providerId) {
        for (AuthEnum authenum : values()) {
            if (authenum.getProviderId().equals(providerId)) {
                return authenum.getAccessSecret();
            }
        }
        return null;
    }
    public String getProviderId()
    {
        return providerId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }
}
