package com.example.board.dao;

import com.example.board.dto.Board;
import com.example.board.dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class BoardDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsertOperations insertBoard;

    //생성자 주입, 스프링이 자동으로 HikeriCP Bean을 주입함.
    public BoardDao(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertBoard = new SimpleJdbcInsert(dataSource)
                .withTableName("board")
                .usingGeneratedKeyColumns("board_id");//자동으로 증가되는 id설정.
    }

    @Transactional
    public void addBoard(String nickname, String title, String content, Integer userId, String image, String reviewText, String reviewImage) {
        Board board = new Board();
        board.setWriter(nickname); //유저 등록 순서에 따른 테이블 id 생성.
        board.setTitle(title);
        board.setContent(content);
        board.setRedate(LocalDateTime.now());
        board.setUserId(userId);
        board.setImage(image);

        //리뷰 관련 dao
        board.setReviewText(reviewText);
        board.setReviewImage(reviewImage);

        SqlParameterSource params = new BeanPropertySqlParameterSource(board);
        insertBoard.execute(params);
    }

    @Transactional(readOnly = true)
    public int getTotalCount() {
        String sql = "select count(*) as totalCount from board";
        Integer totalCount = jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
        return totalCount.intValue();

    }

    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        int start = (page - 1) * 10;
        String sql = "select b.user_id, b.board_id, b.title, b.redate, b.view_cnt, u.nickname " +
                "from board b, user u where b.user_id = u.user_id " +
                "order by board_id desc limit :start,10";

        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);
        List<Board> list = jdbcTemplate.query(sql, Map.of("start", start), rowMapper);

        return list;
    }

    //읽어오기만 하면 readOnly = true
    @Transactional(readOnly = true)
    public Board getBoard(int boardId) {
        String sql = "select b.user_id, b.board_id, b.title, b.redate, b.view_cnt, u.nickname, b.content, b.image " +
                "from board b, user u where b.user_id = u.user_id " +
                "and b.board_id = :boardId";

        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);
        Board board = jdbcTemplate.queryForObject(sql, Map.of("boardId", boardId), rowMapper);
        return board;
    }

    @Transactional
    public void updateViewCnt(int boardId) {
        String sql = "update board\n" +
                "set view_cnt = view_cnt +1\n" +
                "where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }

    @Transactional
    public void deleteBoard(int boardId) {
        String sql = "delete from board where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }

    @Transactional
    public void updateBoard(int boardId, String title, String content) {
        String sql = "update board\n" +
                "set title = :title, content = :content\n" +
                "where board_id = :boardId";
        Board board = new Board();
        board.setBoardId(boardId);
        board.setTitle(title);
        board.setContent(content);
        SqlParameterSource params = new BeanPropertySqlParameterSource(board);
        jdbcTemplate.update(sql, params);
//        jdbcTemplate.update(sql, Map.of("boardId", boardId, "title", title, "content", content)); -> 한줄로 가능하지만 오타 가능성 있음.
    }

    public List<Board> findAll() {
        String sql = "SELECT * FROM board";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Board.class));
    }

    public Board findById(int bid) {
        String sql = "SELECT * FROM board WHERE board_id=:id";
        MapSqlParameterSource param = new MapSqlParameterSource("id", bid);
        return jdbcTemplate.queryForObject(sql, param, new BeanPropertyRowMapper<>(Board.class));
    }


}
