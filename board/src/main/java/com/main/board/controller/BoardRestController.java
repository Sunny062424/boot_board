package com.main.board.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.main.board.service.BoardService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class BoardRestController {

    @Autowired
    private BoardService boardService;
    
    @GetMapping("/board/download/{id}")
    public void downloadAttach(@PathVariable("id") Integer id,
        HttpServletResponse response ) throws MalformedURLException, UnsupportedEncodingException {
        String filename = boardService.getFilepath(id);
        String[] outputParsingFilename = filename.split("/");
        String realFilename = outputParsingFilename[outputParsingFilename.length-1];
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        String outputfilename = new String(realFilename.getBytes("KSC5601"),"8859_1");
        response.setHeader("Content-Disposition", "attachment; filename=\""+outputfilename+"\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
        try {
            InputStream is = new FileInputStream(filename);
            OutputStream os = response.getOutputStream();
            FileCopyUtils.copy(is, os);
            is.close();
            os.close();
        } catch (Exception e) {
            System.out.println("ERR:"+e.toString());
        }
    }
}
