package com.example.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.FileUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BannerProvider extends DefaultBannerProvider {

    @Override
    public String getBanner() {
        return FileUtils.readBanner(BannerProvider.class, "banner.txt");
    }

    @Override
    public String getWelcomeMessage() {
        return "Welcome to simple HTTP console!";
    }
}
