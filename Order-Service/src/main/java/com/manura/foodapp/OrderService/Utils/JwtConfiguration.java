package com.manura.foodapp.OrderService.Utils;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Configuration
@Getter
@Setter
@ToString
public class JwtConfiguration {
    @NestedConfigurationProperty
    private Header header = new Header();
    private int expiration = 3600;
    private int reseteExpiration = 600;
    private String privateKey = "qxBEEQv7E8aviX1KUcdOiF5ve5COUPAr";
    private String type = "encrypted";


    @Getter
    @Setter
    public static class Header {
        private String name = "Authorization";
        private String prefix = "Bearer ";
    }
}
