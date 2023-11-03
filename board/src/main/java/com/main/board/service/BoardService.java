package com.main.board.service;

import com.main.board.entity.Board;
import com.main.board.repository.BoardRepository;

import org.apache.catalina.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.UUID;
import java.io.File;


@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository; //객체생성, 스프링이 자동으로 주입

    //글작성 처리 + v파일 업로드
    public void write(Board board, MultipartFile file) throws Exception{
        if (!"".equals(file.getOriginalFilename())) {
            
            String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/files";
    
            UUID uuid = UUID.randomUUID(); //파일이름에 붙일 랜덤이름
    
            String fileName = uuid + "_" + file.getOriginalFilename();
    
            File saveFile = new File(projectPath, fileName);
    
            file.transferTo(saveFile);
    
            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
    
            boardRepository.save(board);

            System.out.println(board);

        }else{

            boardRepository.save(board);
        }
    }

    //게시글 리스트 불러오기 처리
    public Page<Board> boardlist(Pageable pageable){
        return boardRepository.findAll(pageable); //Board라는 class가 담긴 list를 찾아 반환
    }
    
    //게시글 검색
    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable){
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    //특정 게시글 불러오기 처리
    public Board boardview(Integer id){
        return boardRepository.findById(id).get();
    }

    //특정 게시글 삭제
    public void boardDelete(Integer id){
        boardRepository.deleteById(id);
    }



}
