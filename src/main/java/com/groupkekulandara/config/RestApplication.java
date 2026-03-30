package com.groupkekulandara.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api") // This is your URL prefix
public class RestApplication extends Application {
    // This stays empty. It just "turns on" Jakarta REST services.
}