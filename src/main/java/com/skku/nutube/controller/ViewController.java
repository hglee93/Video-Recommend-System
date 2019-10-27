package com.skku.nutube.controller;

import com.skku.nutube.repository.VideoRepository;
import com.skku.nutube.video.custom.cbf.VideoContentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Map;

@Controller
public class ViewController {

    @Autowired
    VideoContentAnalyzer videoContentAnalyzer;

    @Autowired
    VideoRepository videoRepository;

    @RequestMapping(value = "/", method= RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("name", "Lee hee gwan");
        return "index";
    }

    @RequestMapping(value = "/content", method= RequestMethod.GET)
    public String content(@RequestParam(value = "videoId", defaultValue = "0")String videoId, Model model) {
        String title = videoRepository.selectTitleByItemId(Integer.valueOf(videoId));
        model.addAttribute("videoId", videoId);
        model.addAttribute("videoTitle", title);
        model.addAttribute("userId", 1);
        return "content";
    }

    @GetMapping("/download")
    public StreamingResponseBody stream(HttpServletRequest req, @RequestParam("fileName") String fileName) throws Exception {
        File file = new File("C:\\Users\\jeusl\\git\\Video-Recommend-System\\src\\main\\resources\\static\\video\\sample.mp4");
        final InputStream is = new FileInputStream(file);
        return os -> {
            readAndWrite(is, os);
        };
    }

    private void readAndWrite(final InputStream is, OutputStream os) throws IOException {
        byte[] data = new byte[2048];
        int read = 0;
        while ((read = is.read(data)) > 0) {
            os.write(data, 0, read);
        }
        os.flush();
    }
}
