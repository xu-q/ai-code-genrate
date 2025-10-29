package com.aicodegenerate.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ProjectDownloadService {

    void downloadProjectZip(String projectPath, String projectName, HttpServletResponse response);
}
