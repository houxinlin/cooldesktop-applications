package com.cooldesktop.app.upload.controller;

import com.cooldesktop.app.upload.chunk.TaskManager;
import com.cooldesktop.app.upload.utils.FileUtils;
import com.cooldesktop.app.upload.utils.TokenUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Controller
public class UploadController implements ApplicationContextAware {

    @Autowired
    TaskManager taskManager;

    private ApplicationContext applicationContext;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("token", TokenUtils.getToken());
        return "index";
    }

    @GetMapping("/task/create")
    @ResponseBody
    public String createId() {
        return taskManager.generatorTaskId();
    }

    @PostMapping("upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("taskId") String taskId,
                         @RequestParam("fileName") String fileName,
                         @RequestParam("chunkId") String chunkId,
                         @RequestParam("fileSize") long fileSize,
                         @RequestHeader(value = "token", required = false, defaultValue = "") String token) {
        try {
            if (!TokenUtils.getToken().equals(token)) {
                return "-1";
            }
            String taskDirectory = FileUtils.createTaskDirectory(taskId);
            file.transferTo(Paths.get(taskDirectory, chunkId));
            if (taskManager.getDeleteDirectorTask(taskId) == null) return "-1";
            synchronized (taskManager.getDeleteDirectorTask(taskId)) {
                Stream<Path> list = Files.list(Paths.get(taskDirectory));
                if (list.map(path -> FileUtils.fileSize(path)).mapToLong(value -> value).sum() == fileSize) {
                    Path storagePath = FileUtils.getStoragePath(taskDirectory, fileName);
                    FileUtils.merge(Paths.get(taskDirectory), storagePath);
                    this.applicationContext.publishEvent(new Event(this, 1, storagePath));
                    return "1";
                }
            }
            return "0";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
