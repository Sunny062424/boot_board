package com.main.board.repository;

import com.main.board.entity.Board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    Page<Board> findAll(Pageable pageable);
    
    Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);
    
}
