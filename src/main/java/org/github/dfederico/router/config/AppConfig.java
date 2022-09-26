package org.github.dfederico.router.config;

import java.util.List;

import lombok.Data;

@Data
public class AppConfig {
   private String applicationId;
   private String inputTopic;
   private String defaultRouteTopic;
   private String tempStateDir;
   private List<AppConfig.RouteConfig> routes;

   @Data
   public static class RouteConfig {
      private String topic;
      private int lowerBound;
      private int upperBound;
   }
}
