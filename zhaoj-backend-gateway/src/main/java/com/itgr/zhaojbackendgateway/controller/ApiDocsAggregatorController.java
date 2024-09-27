package com.itgr.zhaojbackendgateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ygking
 *
 * 聚合多服务的 swagger 文档，便于前端生成 service 代码
 */
@RestController
@RequestMapping("/")
public class ApiDocsAggregatorController {

    private static final String[] API_DOCS_URLS = {
            "http://localhost:8101/api/question/v2/api-docs",
            "http://localhost:8101/api/user/v2/api-docs",
            "http://localhost:8101/api/judge/v2/api-docs",
            "http://localhost:8101/api/chartgen/v2/api-docs"
    };

    @GetMapping("/api/all/v2/api-docs")
    public Map<String, Object> aggregateApiDocs() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> aggregatedDocs = new HashMap<>();
        Map<String, String> info = Collections.unmodifiableMap(new HashMap<String, String>() {{
            put("title", "Aggregated API");
            put("version", "1.0.0");
        }});

        // 初始化 aggregatedDocs 的基本结构
        aggregatedDocs.put("swagger", "2.0");
        aggregatedDocs.put("info", info);
        aggregatedDocs.put("paths", new HashMap<String, Object>());
        aggregatedDocs.put("definitions", new HashMap<String, Object>());

        for (String url : API_DOCS_URLS) {
            Map<String, Object> apiDocs = restTemplate.getForObject(url, Map.class);
            if (apiDocs != null) {
                // 合并 paths
                Map<String, Object> paths = (Map<String, Object>) apiDocs.get("paths");
                if (paths != null) {
                    ((Map<String, Object>) aggregatedDocs.get("paths")).putAll(paths);
                }

                // 合并 definitions
                Map<String, Object> definitions = (Map<String, Object>) apiDocs.get("definitions");
                if (definitions != null) {
                    ((Map<String, Object>) aggregatedDocs.get("definitions")).putAll(definitions);
                }
            }
        }

        return aggregatedDocs;
    }
}
