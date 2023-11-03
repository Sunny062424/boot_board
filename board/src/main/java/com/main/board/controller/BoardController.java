package com.main.board.controller;

import com.main.board.entity.Board;
import com.main.board.repository.BoardRepository;
import com.main.board.service.BoardService;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;


@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;
    
    @GetMapping("/board/write")
    public String boardWriteForm() {
        return "boardwrite";
    }

    @PostMapping("/board/writedo")
    public String boardWriteInsert(Board board, Model model, MultipartFile file) throws Exception {
        //System.out.println(file.getOriginalFilename());
        boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    @GetMapping("/board/list")
    public String boardList(Model model, 
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC)
                            Pageable pageable,
                            @RequestParam(value="searchKeyword", defaultValue="")String searhKeyword){
        
        Page<Board> list = null;
        
        if(searhKeyword == ""){ //검색을 안한경우 기존의 list 가져오기
            list = boardService.boardlist(pageable);
        } else { //검색한 경우
            list = boardService.boardSearchList(searhKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1; //1에서 시작위해, 햔재 페이지 가져오기
        int startPage = Math.max(nowPage - 4, 1); //a, b 중 큰 값 반환
        int endPage = Math.min(nowPage + 5 , list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "boardlist";
    }

    @GetMapping("/board/view")
    public String boardview(Model model, Integer id){
        model.addAttribute("board", boardService.boardview(id));
        return "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id){
        boardService.boardDelete(id);
        return "redirect:/board/list"; //삭제 처리후 list로 redirect
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("board", boardService.boardview(id));
        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception{
        Board boardTemp = boardService.boardview(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent()); //설정한 boardTemp를 덮어쓰는 

        boardService.write(boardTemp, file);

        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    //파일 다운로드
    @GetMapping("/board/download/{id}")
    public ResponseEntity<?> downloadAttach(@PathVariable Integer id) throws MalformedURLException {

        Board file = BoardService.boardFile(filename);

        UrlResource resource = new UrlResource("file:" + file.getSavedPath());

        String encodedFileName = UriUtils.encode(file.getOrgNm(), StandardCharsets.UTF_8);

        // 파일 다운로드 대화상자가 뜨도록 하는 헤더를 설정해주는 것
        // Content-Disposition 헤더에 attachment; filename="업로드 파일명" 값을 준다.
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition).body(resource);
    }
    
    

}