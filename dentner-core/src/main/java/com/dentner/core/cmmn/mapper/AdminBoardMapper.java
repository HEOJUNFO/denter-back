package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.BoardDto;
import com.dentner.core.cmmn.dto.BoardAddDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminBoardMapper {

    int selectBoardListCnt(BoardDto boardDto);

    int deleteAllPopBoard();

    int updatePopBoard(@Param("boardNo") int boardNo);

    int insertBoard(BoardAddDto boardAddDto);

    int updateBoard(BoardAddDto boardAddDto);

    int deleteBoard(@Param("boardNo") int boardNo);
}
